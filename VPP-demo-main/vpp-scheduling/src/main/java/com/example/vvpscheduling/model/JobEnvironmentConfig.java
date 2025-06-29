package com.example.vvpscheduling.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;


@Component
@Data
public class JobEnvironmentConfig {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private String ScheduledCron;


    @PostConstruct
    public void init() {
        switch (activeProfile) {
            case "china":
                this.ScheduledCron = "0 00 18 * * ?";
                break;
            default:
                this.ScheduledCron = "0 45 23 * * ?";
                break;
        }
    }
}
