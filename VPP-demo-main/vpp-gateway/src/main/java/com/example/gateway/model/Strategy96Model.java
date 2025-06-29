package com.example.gateway.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Strategy96Model implements Serializable {

    /**
     * 有序的96点 时间升序
     * */
    private Every15MinuteModel[] cfStrategy = new Every15MinuteModel[96] ;

    /**
     * 是否立即生效
     * */
    private  boolean  immediateEffectiveness;

}
