package com.firstgoal.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Fixture implements Serializable {

    private Team homeTeam;
    private Team awayTeam;
    private String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());


    @JsonCreator
    public Fixture(
        @JsonProperty("awayTeam") Team awayTeam,
        @JsonProperty("homeTeam") Team homeTeam
    ) {
        this.awayTeam = awayTeam;
        this.homeTeam = homeTeam;
    }
}
