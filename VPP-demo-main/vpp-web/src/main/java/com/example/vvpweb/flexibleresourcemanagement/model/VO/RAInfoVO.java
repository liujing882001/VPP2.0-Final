package com.example.vvpweb.flexibleresourcemanagement.model.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RAInfoVO {
    private String time;
    private BigDecimal Dynamic;
    private BigDecimal Fixed;
}
