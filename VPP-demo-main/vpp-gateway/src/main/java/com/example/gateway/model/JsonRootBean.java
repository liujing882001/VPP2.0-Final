package com.example.gateway.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class JsonRootBean implements Serializable {

    private long tag;
    private String noHouseholds;
    private String mecId;
    private List<DeviceCommand> deviceCommand = new ArrayList<>();
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date datetime;
}
