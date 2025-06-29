package com.example.vvpweb.externalapi.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class QueryDCCommand {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date sDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date eDate;
}
