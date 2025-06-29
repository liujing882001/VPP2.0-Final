package com.example.vvpweb.flexibleresourcemanagement.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class NodeTypeNumberResponse implements Serializable {

    private String name;
    private int number;
    private int order;
    private String icon;

}
