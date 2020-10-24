package com.firstgoal.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Team implements Serializable {

    private String name;
    private Player[] startingSquad;

    @JsonCreator
    public Team(Map<String, Object> team) {
        this.name = String.valueOf(team.get("name"));

        List<Player> players = new ArrayList<>();

        //parse players
        for (Map<String, Object> player : (List<Map<String, Object>>)team.get("players")) {
            if (isStarting(player)) {
                players.add(Player.builder()
                    .name(((Map<String, String>)player.get("name")).get("abbreviation"))
                    .position(((Map<String, String>)player.get("meta")).get("positionRegular"))
                    .build());
            }
        }
        this.startingSquad = players.toArray(new Player[players.size()]);
    }

    private boolean isStarting(Map<String, Object> player) {
        return ((Map<String, String>)player.get("meta")).get("status").equals("starter");
    }
}
