package com.example.vvpdomain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
public class RAEnergySocDTO {
    private String nodeId;
    private Double soc;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ts;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    public RAEnergySocDTO() {
    }

    public RAEnergySocDTO(String nodeId, Double soc, Date ts,Date createdTime) {
        this.nodeId = nodeId;
        this.soc = soc;
        this.ts = ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        this.createdTime = createdTime;

    }
}
