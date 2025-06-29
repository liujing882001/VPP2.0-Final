package com.example.vvpweb.presalesmodule.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceData {
    /**
     * 开始时间~结束时间
     */
    private String period;

    /**
     * 时段分类：尖、峰、平、谷、深谷
     */
    private String timeCategory;

    /**
     * 价格
     */
    private BigDecimal price;
}
