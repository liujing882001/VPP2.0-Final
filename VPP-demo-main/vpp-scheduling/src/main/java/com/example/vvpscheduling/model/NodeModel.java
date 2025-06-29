package com.example.vvpscheduling.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeModel implements Serializable {

    private String nodeId;

}
