# MapshotProxyServer-Public
맵샷 레이어 추가를 위한 프록시 서버

## 개발 환경, 기술 스택
* Java 11, Spring Boot
* Vworld Open API (WMS, GetMap) [vworld-wms 페이지](https://www.vworld.kr/dev/v4dv_wmsguide2_s001.do)
* Oracle Cloud Server, CentOS 8
* Naver Cloud Server, CentOS 7.8
* Let's Encrypt, OpenSSL

## 프로젝트 개요
### Vworld
* 기존 Mapshot 서비스에 토지이용계획도, 도시계획도 등을 덧입히기 위해 사용
* CORS 문제로 인해 재가공 어려움, Vworld에서 Proxy 서버 이용 권유

### Oracle Cloud Server
* 무료 클라우드 서버를 이용하기 위해 선택
* CentOS 8로 설치, 방화벽과 도메인 세팅 후 jar 파일로 서버 실행 (내장 톰캣 사용)
* 하지만 해외 서버에서는 Vworld 이미지 호출이 금지됨 (문제점 파트에서 서술)

### Naver Cloud Server
* 국내 아이피를 이용하기 위해 신청, Micro 서버 타입에 CentOS 7.8로 설치
* 그제서야 사진 정상적으로 잘 받아옴


## 문제점
### Oracle Cloud Server
* 분명 서울 Region으로 서버를 세팅했는데도 불구하고 호출이 안됨
* Ip 확인 결과, 장소는 한국으로 찍히지만 국내에서 관리되는 Ip가 아니라고 나옴
* Asia Pacific Network Information Centre(APNIC)의 Ip로 확인

### Naver Cloud Server
* 서버는 빠르고 좋은데, 생각보다 비쌈
* 공인 Ip 발급 & 유지비, 서버 운영비 등등
* Mapshot 광고 수익으로 충당이 가능할지 의문

## 기록사항
#### Oracle CentOS 8 방화벽 설정
* firewalld에 포트 추가 후 iptable에도 추가
* iptable에 추가 안해주면 안열림
* Naver에 세팅한 CentOS 7.8은 firewalld에만 추가해도 작동 문제없었음
```
firewall-cmd --permanent --add-port=80/tcp --zone=public
firewall-cmd --permanent --add-port=443/tcp --zone=public
firewall-cmd --permanent --add-port=8080/tcp --zone=public

firewall-cmd --zone=public --add-forward-port=port=80:proto=tcp:toport=8080 --permanent
firewall-cmd --zone=public --add-forward-port=port=443:proto=tcp:toport=8080 --permanent

firewall-cmd --reload
<!-- 네이버는 여기까지만 -->

iptables -I INPUT 5 -i ens3 -p tcp --dport 8080 -m state --state NEW,ESTABLISHED -j ACCEPT

iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
iptables -t nat -A PREROUTING -p tcp --dport 443 -j REDIRECT --to-port 8080
```

#### Let's Encrypt, OpenSSL
```
<!-- let's encrypt 발급 -->
<!-- manual로 발급시 certbot renew 불가능, webroot로 다시 기록 -->
certbot certonly -d '신청할 도메인' --manual --preferred-challenges dns --server https://acme-v02.api.letsencrypt.org/directory --email example@example.com

<!-- opensssl로 p12 변환 -->
openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out keystore.p12 -name 이름 -CAfile chain.pem -caname root
```

#### Java
```
chmod 777 ./gradlew
./gradlew build

<!-- 추가 작업 있으면 끝에 & 붙이기 -->
nohup java -jar 파일명.jar
```

## 결과물 샘플
* 토지이용 계획도
![토지이용계획도](https://user-images.githubusercontent.com/59993347/124061198-6cfeef00-da69-11eb-9ad1-91d994562d94.png)

* 지적도
![지적도](https://user-images.githubusercontent.com/59993347/124061199-6d978580-da69-11eb-8b0a-5722ba8046e9.png)





