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

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }
    public String getSTime() { return sTime; }
    public void setSTime(String sTime) { this.sTime = sTime; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getStrategy() { return strategy; }
    public void setStrategy(BigDecimal strategy) { this.strategy = strategy; }
}
