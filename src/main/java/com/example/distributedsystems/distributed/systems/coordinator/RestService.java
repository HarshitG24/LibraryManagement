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
 * A service that provides methods for making RESTful API calls.
 */
@Service
public class RestService {
    private final RestTemplate restTemplate;

    /**
     * Constructs a new RestService with the given RestTemplateBuilder.
     *
     * @param restTemplateBuilder the RestTemplateBuilder used to build the RestTemplate used by this service.
     */
    public RestService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Sends a POST request with the given data to the specified URL, and returns the response entity.
     *
     * @param url the URL to send the request to.
     * @param data the data to include in the request body.
     * @return the response entity from the server.
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

    /**
     * Sends a POST request with the given data to the specified URL, and returns the response entity.
     *
     * @param url the URL to send the request to.
     * @param data the data to include in the request body.
     * @param responseType the type of the response entity.
     * @return the response entity from the server.
     */
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
     * Sends a GET request with the given data to the specified URL, and returns the response entity.
     *
     * @param url the URL to send the request to.
     * @param data the data to include in the request body.
     * @return the response entity from the server.
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

