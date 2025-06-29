package com.example.vvpscheduling.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Setter
@Getter
public class DayModel implements Serializable {

    private String day;

    public DayModel() {
    }

    public DayModel(String day) {
        this.day = day;
    }
}
