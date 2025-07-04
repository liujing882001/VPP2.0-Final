package com.example.vvpservice.chinasouthernpower.model;

import java.util.Date;

@lombok.Data
public class Data {
    private Date ts;
    private RData rd;

    // 手动添加缺失的getter/setter方法以确保编译通过
    public Date getTs() { return ts; }
    public void setTs(Date ts) { this.ts = ts; }
    public RData getRd() { return rd; }
    public void setRd(RData rd) { this.rd = rd; }
}
