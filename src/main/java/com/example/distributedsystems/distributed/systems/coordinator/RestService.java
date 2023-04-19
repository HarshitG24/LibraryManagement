package com.example.distributedsystems.distributed.systems.coordinator;
//need to refactor

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class RestService {
    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ResponseEntity<Object> post(String url, Object data) {
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // build the request
        HttpEntity<Object> entity = new HttpEntity<>(data, headers);

        // send POST request
        return restTemplate.postForEntity(url, entity, Object.class);
    }

    public ResponseEntity<Object> get(String url, Object data) {
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // build the request
        HttpEntity<Object> entity = new HttpEntity<>(data, headers);

        // send POST request
        return restTemplate.getForEntity(url, Object.class);
    }

    public String generateURL(String host, int port, String super_var,String var) {
        return "http://"+host+":"+port+"/"+super_var+"/"+var;
    }
}

