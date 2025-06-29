package com.example.vvpdomain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RAPriceDTO {
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDate effectiveDate;
    private String sTime;
    private BigDecimal price;
    private BigDecimal strategy;

    public RAPriceDTO(){}
    public RAPriceDTO(LocalDateTime effectiveDate, String sTime, BigDecimal price, BigDecimal strategy) {
        this.effectiveDate = effectiveDate.toLocalDate();
        this.sTime = sTime;
        this.price = price;
        this.strategy = strategy;
    }
}
