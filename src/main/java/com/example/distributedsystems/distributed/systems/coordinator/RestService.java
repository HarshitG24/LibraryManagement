package com.example.distributedsystems.distributed.systems.coordinator;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * helper function that is used to send requests to other one port to other
 */
@Service
public class RestService {
    private final RestTemplate restTemplate;

    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     *
     * @param url of the port and the api to be called
     * @param data data to be sent
     * @return the output of the post
     * it sends the post request to all the other ports
     */
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

    public <T> T post(String url, Object data, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // build the request
        HttpEntity<Object> entity = new HttpEntity<>(data, headers);

        // send POST request
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
        return response.getBody();
    }

    /**
     *
     * @param url url of the port
     * @param data data to be sent
     * @return the response
     * sends the get request
     */

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

}

