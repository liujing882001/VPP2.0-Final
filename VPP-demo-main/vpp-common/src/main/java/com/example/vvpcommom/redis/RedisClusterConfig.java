package com.example.vvpcommom.redis;

import java.util.ArrayList;
import java.util.List;


public class RedisClusterConfig {
    private List<String> nodes = new ArrayList<>();

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }
}
