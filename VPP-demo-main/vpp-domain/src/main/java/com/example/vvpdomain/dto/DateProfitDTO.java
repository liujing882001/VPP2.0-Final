package com.example.vvpdomain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DateProfitDTO {
    private String date;
    private Double value;
    public DateProfitDTO(){}
    public DateProfitDTO(Date date, double value) {
        this.date = date.toString().split(" ")[0];
        this.value = value;
    }
}