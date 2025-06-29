package com.example.vvpweb.flexibleresourcemanagement.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class LoadMonthModelResponse2 implements Serializable {
    private BigInteger louyu;
    private BigInteger gongchang;
    private BigInteger chuneng;
    private BigInteger guangfu;

    public LoadMonthModelResponse2() {
    }

    public LoadMonthModelResponse2(BigInteger louyu, BigInteger gongchang, BigInteger chuneng, BigInteger guangfu) {
        this.louyu = louyu;
        this.gongchang = gongchang;
        this.chuneng = chuneng;
        this.guangfu = guangfu;
    }
}
