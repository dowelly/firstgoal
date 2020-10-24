package com.firstgoal.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Participant implements Serializable {
    private String name;
    private ArrayList<Player> assigments = new ArrayList<>();

    public Participant(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Participant) {
            if (((Participant) object).getName().equals(this.getName())) {
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
        return getName();
    }
}
