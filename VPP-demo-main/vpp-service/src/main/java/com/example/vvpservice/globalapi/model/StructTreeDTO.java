package com.example.vvpservice.globalapi.model;


import java.util.ArrayList;
import java.util.List;
public class StructTreeDTO {

    private String id;

    private String key;

    private String title;

    private String type;
    private String nodeType;
    private String nodeState;


    private List<StructTreeDTO> children = new ArrayList<>();

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
    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public List<StructTreeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<StructTreeDTO> children) {
        this.children = children;
    }

    public String getNodeState() {
        return nodeState;
    }

    public void setNodeState(String nodeState) {
        this.nodeState = nodeState;
    }
}
