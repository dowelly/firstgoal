package com.firstgoal.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
public class Player implements Serializable {

    private String position;
    private String name;

    @JsonCreator
    public Player(@JsonProperty("position") String position, @JsonProperty("name") String name) {
        this.position = position;
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Player) {
            if (((Player) object).getName().equals(this.getName())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return getPosition() + " " + getName();
    }
}
