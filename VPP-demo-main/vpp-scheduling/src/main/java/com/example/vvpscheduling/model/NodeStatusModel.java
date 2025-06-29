package com.example.vvpscheduling.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class NodeStatusModel implements Serializable {

    BigInteger status;

    public NodeStatusModel() {
    }

    public NodeStatusModel(BigInteger status) {
        this.status = status;
    }
}
