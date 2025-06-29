package com.example.gateway.entity;

import lombok.Data;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommandContext implements Serializable {

    //设备的 设备码，点位码，控制值
    private List<CommandUnit> commandUnits = new ArrayList<>();

}
