package com.example.vvpservice.tree.model;

import java.util.ArrayList;
import java.util.List;

public class StructTreeResponse {

    private String id;

    private String key;

    private String title;

    private String type;

    private int deviceSize = 0;

    private double load;

    private List<StructTreeResponse> children = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDeviceSize() {
        return deviceSize;
    }

    public void setDeviceSize(int deviceSize) {
        this.deviceSize = deviceSize;
    }

    public List<StructTreeResponse> getChildren() {
        return children;
    }

    public void setChildren(List<StructTreeResponse> children) {
        this.children = children;
    }

    public double getLoad() {
        return load;
    }

    public void setLoad(double load) {
        this.load = load;
    }
}
