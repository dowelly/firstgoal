package com.firstgoal.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddParticpantRequest {
    private String name;

    @JsonCreator
    public AddParticpantRequest(@JsonProperty("name") String name) {
        this.name = name;
    }
}
