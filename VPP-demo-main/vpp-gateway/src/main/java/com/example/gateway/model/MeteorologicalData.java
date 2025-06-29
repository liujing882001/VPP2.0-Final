package com.example.gateway.model;


public class MeteorologicalData {
    //实时-2m 气温
    private double rtTt2;
    //实时-小时累计降水
    private double rtRain;
    //"实时采集时间"
    private String ts;
    //预测-2m 气温
    private double predTt2;
    //预测-小时累计降水
    private double predRain;

    private double rtSh;


    private double rtSsr;

    private double rtWs10;

    private double rtUu;


    private double rtVv;

    private double rtPs;

    private double rtDt;

    private double predSsrd;

    private double predTsdsr;

    private double predSTsr;

    private double predWs10;

    private double predWd10;

    private double predWs100;

    private double predWd100;

    private double predRh;

    private double predPs;

    private double predTcc;

    public double getRtTt2() {
        return rtTt2;
    }

    public void setRtTt2(double rtTt2) {
        this.rtTt2 = rtTt2;
    }

    public double getRtRain() {
        return rtRain;
    }

    public void setRtRain(double rtRain) {
        this.rtRain = rtRain;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public double getPredTt2() {
        return predTt2;
    }

    public void setPredTt2(double predTt2) {
        this.predTt2 = predTt2;
    }

    public double getPredRain() {
        return predRain;
    }

    public void setPredRain(double predRain) {
        this.predRain = predRain;
    }

    public double getRtSh() {
        return rtSh;
    }

    public void setRtSh(double rtSh) {
        this.rtSh = rtSh;
    }

    public double getRtSsr() {
        return rtSsr;
    }

    public void setRtSsr(double rtSsr) {
        this.rtSsr = rtSsr;
    }

    public double getRtWs10() {
        return rtWs10;
    }

    public void setRtWs10(double rtWs10) {
        this.rtWs10 = rtWs10;
    }

    public double getRtUu() {
        return rtUu;
    }

    public void setRtUu(double rtUu) {
        this.rtUu = rtUu;
    }

    public double getRtVv() {
        return rtVv;
    }

    public void setRtVv(double rtVv) {
        this.rtVv = rtVv;
    }

    public double getRtPs() {
        return rtPs;
    }

    public void setRtPs(double rtPs) {
        this.rtPs = rtPs;
    }

    public double getRtDt() {
        return rtDt;
    }

    public void setRtDt(double rtDt) {
        this.rtDt = rtDt;
    }

    public double getPredSsrd() {
        return predSsrd;
    }

    public void setPredSsrd(double predSsrd) {
        this.predSsrd = predSsrd;
    }

    public double getPredTsdsr() {
        return predTsdsr;
    }

    public void setPredTsdsr(double predTsdsr) {
        this.predTsdsr = predTsdsr;
    }

    public double getPredSTsr() {
        return predSTsr;
    }

    public void setPredSTsr(double predSTsr) {
        this.predSTsr = predSTsr;
    }

    public double getPredWs10() {
        return predWs10;
    }

    public void setPredWs10(double predWs10) {
        this.predWs10 = predWs10;
    }

    public double getPredWd10() {
        return predWd10;
    }

    public void setPredWd10(double predWd10) {
        this.predWd10 = predWd10;
    }

    public double getPredWs100() {
        return predWs100;
    }

    public void setPredWs100(double predWs100) {
        this.predWs100 = predWs100;
    }

    public double getPredWd100() {
        return predWd100;
    }

    public void setPredWd100(double predWd100) {
        this.predWd100 = predWd100;
    }

    public double getPredRh() {
        return predRh;
    }

    public void setPredRh(double predRh) {
        this.predRh = predRh;
    }

    public double getPredPs() {
        return predPs;
    }

    public void setPredPs(double predPs) {
        this.predPs = predPs;
    }

    public double getPredTcc() {
        return predTcc;
    }

    public void setPredTcc(double predTcc) {
        this.predTcc = predTcc;
    }
}
