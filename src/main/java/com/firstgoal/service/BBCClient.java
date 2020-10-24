package com.firstgoal.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class BBCClient {

    @SneakyThrows
    public JsonNode get(String url) {
        HttpResponse<JsonNode> response
            = Unirest.get(url)
            .header("accept", "application/json")
            .asJson();

        return response.getBody();
    }
}
