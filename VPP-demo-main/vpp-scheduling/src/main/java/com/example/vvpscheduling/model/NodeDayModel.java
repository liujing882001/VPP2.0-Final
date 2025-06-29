package com.example.vvpscheduling.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeDayModel implements Serializable {
    private String day;
    private String nodeId;
    private Double longitude;
    private Double latitude;
}
