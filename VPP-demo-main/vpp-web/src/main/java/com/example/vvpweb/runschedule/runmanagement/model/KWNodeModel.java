package com.example.vvpweb.runschedule.runmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KWNodeModel implements Serializable {

    String node_id;
    Double device_rated_power;
}
