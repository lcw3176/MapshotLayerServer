package com.joebrooks.mapshotpublic.Controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = {"https://mapshot.netlify.app", "https://testservermapshot.netlify.app"})
@RestController
@RequestMapping("/maps")
public class MapController {

    @GetMapping
    public ResponseEntity Home(@RequestParam("coors") String coors, @RequestParam("layers") String layers) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.vworld.kr/req/wms?SERVICE=WMS&");
        sb.append("key=키값&");
        sb.append("domain=https://mapshotproxyserver.kro.kr&");
        sb.append("request=GetMap&format=image/png&");
        sb.append("width=1000&height=1000&transparent=TRUE&BGCOLOR=0xFFFFFF&");
        sb.append("BBOX=");
        sb.append(coors);
        sb.append("&");
        sb.append("LAYERS=");
        sb.append(layers);
        sb.append("&");
        sb.append("STYLES=");
        sb.append(layers);

        RestTemplate rt = new RestTemplate();
        byte[] binary = rt.getForObject(sb.toString(), byte[].class);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(binary);
    }
}
