package com.example.vvpweb.demand.aigorithmmodel;


import lombok.Data;

import java.util.List;

@Data
public class DemandAlgorithmVo {

    String resp_id;
    String node_id;
    String prompt;
    String node_name;
    Double command_value;
    String weights;
    String init_value;
    String opt_x;
    List<String> x;
    String alter_mode;
    Double opt_fx;
    Double alter_value;
    public DemandAlgorithmVo(){

    }
    public DemandAlgorithmVo(String respId, String nodeId, String prompt, String nodeName, Double commandValue, String weights, String initVo, List<String> x) {
        this.resp_id = respId;
        this.node_id = nodeId;
        this.prompt = prompt;
        this.node_name = nodeName;
        this.command_value = commandValue;
        this.weights = weights;
        this.init_value = initVo;
        this.x = x;
    }
    public DemandAlgorithmVo(String respId, String nodeId, String nodeName, Double i, String weights, String initVo, String alter_mode) {
        this.resp_id = respId;
        this.node_id = nodeId;
        this.node_name = nodeName;
        this.opt_fx = i;
        this.weights = weights;
        this.opt_x = initVo;
        this.alter_mode = alter_mode;
    }

    public DemandAlgorithmVo(String respId, String nodeId, String nodeName, Double i, String weights, String initVo, String alter_mode, Double alter_value) {
        this.resp_id = respId;
        this.node_id = nodeId;
        this.node_name = nodeName;
        this.opt_fx = i;
        this.weights = weights;
        this.opt_x = initVo;
        this.alter_mode = alter_mode;
        this.alter_value = alter_value;
    }
}
