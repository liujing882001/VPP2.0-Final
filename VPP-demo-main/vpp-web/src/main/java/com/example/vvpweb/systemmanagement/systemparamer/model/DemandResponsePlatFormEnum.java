package com.example.vvpweb.systemmanagement.systemparamer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum  DemandResponsePlatFormEnum {

    FJ("1", "福建"),
    XM("2", "厦门"),
    SZ("3", "深圳"),
    SD("4", "山东"),
    ZJ("5", "浙江"),
    JS("6", "江苏"),
    GWFJ("7", "国网.福建"),
    GWXM("8", "国网.厦门"),
    GWSZ("9", "国网.深圳"),
    GWSD("10", "国网.山东"),
    GWZJ("11", "国网.浙江"),
    GWJS("12", "国网.江苏");

    /**
     * key
     */
    private String id;

    private String desc;
}
