package com.firstgoal.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
@RedisHash(value = "Competition", timeToLive = 15800)
@Slf4j
public class Competition implements Serializable {

    private String id;
    private String owner;
    private Fixture fixture;
    private String eventUrl;
    private boolean live;
    private boolean started;
    private Player[] selections = {};
    private ArrayList<Participant> participants = new ArrayList<>();
    private Participant winner;

    @JsonCreator
    public Competition(
        @JsonProperty("teams") Fixture fixture
    ) {
        this.fixture = fixture;
        this.id = RandomStringUtils.randomAlphanumeric(10);
        this.live = false;
        this.started = false;
    }

    public void setWinner(Player scorer) {
        winner = getParticipants().stream().filter(
            participant -> participant.getAssigments().contains(scorer)
        ).findFirst().get();
        log.info("{} won competition {}", winner.getName(), getId());
    }

    public String printAssignments() {
        String output = "";
        for (Participant participant: participants) {
            output = output + participant.getName() +"\n";
            for (Player assignment : participant.getAssigments()) {
                output = output + " - " + assignment + "\n";
            }
        }
        return output;
    }
}
