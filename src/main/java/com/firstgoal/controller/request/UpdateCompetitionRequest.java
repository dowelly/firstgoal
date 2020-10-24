package com.firstgoal.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.firstgoal.web.Player;
import lombok.Data;

@Data
public class UpdateCompetitionRequest {
    private Player[] selections;

    @JsonCreator
    public UpdateCompetitionRequest(
        @JsonProperty("selections") Player[] selections
    ) {
        this.selections = selections;
    }
}
