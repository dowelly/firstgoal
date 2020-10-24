package com.firstgoal.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateCompetitonRequest {
    private String lineupUrl;
    private String owner;
    private String eventUrl;

    @JsonCreator
    public CreateCompetitonRequest(
        @JsonProperty("lineupUrl") String url,
        @JsonProperty("owner") String owner,
        @JsonProperty("eventUrl") String eventUrl
    ) {
        this.lineupUrl = url;
        this.owner = owner;
        this.eventUrl = eventUrl;
    }
}
