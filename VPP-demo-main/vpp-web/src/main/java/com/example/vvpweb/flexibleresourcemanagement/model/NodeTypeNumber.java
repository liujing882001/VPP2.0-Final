package com.example.vvpweb.flexibleresourcemanagement.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class NodeTypeNumber implements Serializable {

    private String node_type_id;
    private BigInteger node_type_number;

    public NodeTypeNumber() {
    }

    public NodeTypeNumber(String node_type_id, BigInteger node_type_number) {
        this.node_type_id = node_type_id;
        this.node_type_number = node_type_number;
    }
}
