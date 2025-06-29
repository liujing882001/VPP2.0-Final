package com.example.vvpweb.flexibleresourcemanagement.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class BuildFactoryModel implements Serializable {
    String systemId;
    double load;

    public BuildFactoryModel() {
    }

    public BuildFactoryModel(String systemId, double load) {
        this.systemId = systemId;
        this.load = load;
    }
}
