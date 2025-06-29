package com.example.vvpscheduling.model.slicing;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public  class LoadData {
        LocalDateTime timestamp;
        double power;

        public LoadData(LocalDateTime timestamp, double power) {
            this.timestamp = timestamp;
            this.power = power;
        }
    }
