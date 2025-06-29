package com.example.vvpweb.flexibleresourcemanagement.model;

import lombok.Data;

import java.util.List;

@Data
public class GetNodesLocationCommand {
    private List<String> nodes;
}
