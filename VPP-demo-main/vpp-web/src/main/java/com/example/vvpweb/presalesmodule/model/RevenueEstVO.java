package com.example.vvpweb.presalesmodule.model;

import lombok.Data;

import java.util.List;

@Data
public class RevenueEstVO {

    //储能IRR（税前）
    private Double storageIRRPreTax;
    //储能投资回报周期
    private Double storagePayback;
    //储能总收入
    private Double storageTotalRev;
    //储能总成本费用
    private Double storageTotalCost;
    //储能利润总额
    private Double storageTotalProfit;
    //投资方储能X年总收益
    private Double invTotalRevXYears;
    //投资方储能平均年收益
    private Double invAvgAnnualRev;
    //电力用户储能分成比例
    private Double customerShare;
    //电力用户储能X年总收益
    private Double usrTotalRevXYears;
    //电力用户储能平均年收益
    private Double usrAvgAnnualRev;
    //现金流入
    private List<Double> cashInflow;
    //资产方收入
    private List<Double> assetIncomeAfterShare;
    //现金流出
    private List<Double> cashOutflow;
    //建设投资
    private List<Double> constInvest;
    //经营成本
    private List<Double> operatingCost;
    //增值税
    private List<Double> vat;
    //净现金流量
    private List<Double> netCashFlowBeforeTax;

    private boolean result;
}
