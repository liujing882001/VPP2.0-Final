package com.example.vvpweb.presalesmodule.model.factory;

import com.example.vvpdomain.entity.RevenueLoadDto;
import com.example.vvpweb.presalesmodule.model.*;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class RevenueEstimationUtils {

    //报告数据
    //参数:第一年削峰填谷总收益、评估年限、更换电池占总成本比例、电池衰减系数、寿命年限、客户分享比例、平台服务费、电站设计容量、采购价格、前5年维保比例、5-10年维保比例、10-25年维保比例、保险费率、设备费用、工程费用
    public RevenueEstVO reportEstEasy(RevenueEstDTO estDTO){
        return reportEst(estDTO.getFirstPeakValleyIncome(),estDTO.getAssessPeriod(),estDTO.getBatteryReplRatio(), estDTO.getLifespan(),
                estDTO.getBatteryDegCoeff(),estDTO.getCustomerShare(),estDTO.getPlatformRate(),
                estDTO.getDesignCapacity(),estDTO.getPurchasePrice(),estDTO.getMainTRatio5Y(),estDTO.getMainTRatio5_10Y(),estDTO.getMainTRatio10_25Y(),
                estDTO.getInsuranceRate(),estDTO.getEquipmentCostRatio(),estDTO.getEngineeringCostRatio());
    }
    public RevenueEstVO reportEst(
            Double firstPeakValleyIncome,Integer assessPeriod,Double batteryReplRatio,Integer lifespan,
            Double batteryDegCoeff,Double customerShare, Double platformRate,
            Double designCapacity,Double purchasePrice,Double mainTRatio5Y,Double mainTRatio5_10Y,Double mainTRatio10_25Y,
            Double insuranceRate,Double equipmentCostRatio,Double engineeringCostRatio) {
        //投资总额
        Double totalInvestment = totalInvestmentEst(designCapacity,purchasePrice);
        //储能投资回报周期
        AtomicReference<Double> storagePayback = new AtomicReference<>(0.0);
        //储能总收入=削峰填谷总收益=年放电量*平均放电电价-年充电量*平均充电电价
        AtomicReference<Double> storageTotalRev = new AtomicReference<>(0.0);
        //储能总成本费用=支出合计+当期折旧费
        AtomicReference<Double> storageTotalCost = new AtomicReference<>(0.0);
        //现金流入=削峰填谷总收益*(1-客户分享比例) - 回收固定资产余值
        List<Double> cashInflow = new ArrayList<>();
        cashInflow.add(0.0);
        //资产方收入=削峰填谷总收益*(1-客户分享比例)
        List<Double> assetIncomeAfterShare = new ArrayList<>();
        assetIncomeAfterShare.add(0.0);
        //现金流出=建设投资+流动资金+经营成本+税金及附加+增值税+租金
        List<Double> cashOutflow = new ArrayList<>();
        cashOutflow.add(totalInvestment);
        //建设投资=八年一换
        List<Double> constInvest = new ArrayList<>();
        AtomicInteger constInvestCount = new AtomicInteger(0);
        constInvest.add(totalInvestment);
        //经营成本=支出合计=设备维护费用+保险费+折旧费+电池运维费用+人工费+平台服务费
        List<Double> operatingCost = new ArrayList<>();
        operatingCost.add(0.0);
        //增值税=销项税额-建设期进项税余额-运营期进项税
        List<Double> vat = new ArrayList<>();
        vat.add(0.0);
//        log.info("lifespan:{}",lifespan);

        //设备费用
        Double equipmentCost = totalInvestment * equipmentCostRatio;
        //工程费用
        Double engineeringCost = totalInvestment * engineeringCostRatio;
//        log.info("equipmentCostRatio设备：{}，engineeringCostRatio工程：{}",equipmentCostRatio,engineeringCostRatio);

        //净现金流量,V0=totalInvestment，V0=投资总额 Vn=现金流入-现金流出
        List<Double> netCashFlowBeforeTax = new ArrayList<>();
        netCashFlowBeforeTax.add(-totalInvestment);
        AtomicInteger batteryDegCoeffYear = new AtomicInteger(0);
        AtomicReference<Double> maintenanceRatio = new AtomicReference<>(mainTRatio5Y);
        Double insuranceFee = insuranceFeeEst(totalInvestment,insuranceRate);
//        log.info("totalInvestment:{},insuranceRate:{},insuranceFee:{}",totalInvestment,insuranceRate,insuranceFee);
        Double constInputTaxBalV1 = equipmentCost / 1.13 * 0.13 + engineeringCost / 1.06 * 0.06;
        AtomicReference<Double> preConstInputTaxBal = new AtomicReference<>(constInputTaxBalV1);
//        log.info("equipmentCost设备：{}，engineeringCost工程：{},constInputTaxBalV1:{}",equipmentCost,engineeringCost,constInputTaxBalV1);
        AtomicReference<Double> preOutputTax = new AtomicReference<>(0.0);
        AtomicReference<Double> preOperationInputTax = new AtomicReference<>(0.0);
        AtomicReference<Double> cumulativeNetCashFlowBeforeTax = new AtomicReference<>(-totalInvestment);
        AtomicReference<Double> firstPeakValleyIncomeNow = new AtomicReference<>(firstPeakValleyIncome);
        IntStream.range(1, assessPeriod + 1).forEach(i -> {
            //当前电池可用率
            Double batteryDegCoeffNow = 1 - batteryDegCoeff;
            //当前削峰填谷总收益
            Double peakValleyIncome;
            if (i == 1 || i == 9 || i ==17 ) {
                peakValleyIncome = firstPeakValleyIncome;
            } else {
                peakValleyIncome = firstPeakValleyIncomeNow.get() * batteryDegCoeffNow;
            }
            firstPeakValleyIncomeNow.set(peakValleyIncome);

            storageTotalRev.updateAndGet(v -> v + peakValleyIncome);
            //当前资产方扣除客户分享收益
            Double assetIncomeAfterShareNow = assetIncomeAfterShareEst(peakValleyIncome,customerShare);
//            log.info("第：{}年，peakValleyIncome:{},batteryDegCoeffNow:{}",i,peakValleyIncome,batteryDegCoeffNow);

            if (i > 5 && i<= 10) {
                maintenanceRatio.set(mainTRatio5_10Y);
            } else if(i > 10) {
                maintenanceRatio.set(mainTRatio10_25Y);
            }
            Double maintenanceCost = maintenanceCostEst(designCapacity,maintenanceRatio.get());
            Double operatingCostNow = operatingCostEst(maintenanceCost,insuranceFee,platformFeeEst(assetIncomeAfterShareNow,platformRate),0.0,0.0,0.0);
            Double vatNow;
            Double outputTax = outputTaxEst(assetIncomeAfterShareNow);
            Double operationInputTax = operationInputTaxEst(operatingCostNow);
            if (i == 1) {
                vatNow = vatEst(outputTax,preConstInputTaxBal.get(),operationInputTax);
//                log.info("第：{}年，vatNow:{},outputTax销项税:{},constInputTaxBal建设期进项税余额：{}，operationInputTax运营期进项税:{}"
//                        ,i,vatNow,outputTax,preConstInputTaxBal.get(),operationInputTax);

            } else {
                Double constInputTaxBal = constInputTaxBalEst(preConstInputTaxBal.get(),preOutputTax.get(),preOperationInputTax.get());
                vatNow = vatEst(outputTax,constInputTaxBal,operationInputTax);
                preConstInputTaxBal.set(constInputTaxBal);
//                log.info("第：{}年，vatNow:{},outputTax销项税:{},constInputTaxBal建设期进项税余额：{},operationInputTax运营期进项税:{}"
//                        ,i,vatNow,outputTax,constInputTaxBal,operationInputTax);

            }
            preOutputTax.set(outputTax);
            preOperationInputTax.set(operationInputTax);
            int numberOfPeriods = i / lifespan;
            int totalPeriods = assessPeriod / lifespan;
            Double constInvestNow;
            if ((i == (lifespan + 1) && assessPeriod >= 16) || (i == (2 * lifespan + 1)&& assessPeriod >= 24) || (i == (3 * lifespan + 1) && assessPeriod >= 32) || (i == (4 * lifespan + 1) && assessPeriod >= 40) || (i == (5 * lifespan + 1) && assessPeriod >= 40)) {
                batteryDegCoeffYear.set(0);
                constInvestNow = constInvestEst(totalInvestment,batteryReplRatio);
            } else {
                constInvestNow = 0.0;
                batteryDegCoeffYear.getAndAdd(1);
            }
            constInvest.add(constInvestNow);
            Double cashInflowNow = CashInflowEst(peakValleyIncome,customerShare,0.0);
            cashInflow.add(cashInflowNow);
            assetIncomeAfterShare.add(assetIncomeAfterShareNow);
            Double cashOutflowNow = cashOutflowEst(constInvestNow,operatingCostNow,vatNow);
//            log.info("第：{}年，cashOutflowNow现金流出:{},constInvestNow建设投资:{},operatingCostNow经营成本：{},vatNow增值税:{}"
//                    ,i,cashOutflowNow,constInvestNow,operatingCostNow,vatNow);
            cashOutflow.add(cashOutflowNow);
            operatingCost.add(operatingCostNow);
            vat.add(vatNow);
            Double netCashFlowBeforeTaxNow = netCashFlowBeforeTaxEst(cashInflowNow,cashOutflowNow);
            Double cumulativeNetCashFlowBeforeTaxPre = cumulativeNetCashFlowBeforeTax.get();
            Double cumulativeNetCashFlowBeforeTaxNow = cumulativeNetCashFlowBeforeTaxPre + netCashFlowBeforeTaxNow;

            if (cumulativeNetCashFlowBeforeTaxPre * cumulativeNetCashFlowBeforeTaxNow < 0 ) {
                Double now = Math.abs(cumulativeNetCashFlowBeforeTaxPre)/Math.abs(netCashFlowBeforeTaxNow) + i;
                storagePayback.set(now);
            }
            cumulativeNetCashFlowBeforeTax.set(cumulativeNetCashFlowBeforeTaxNow);
            netCashFlowBeforeTax.add(netCashFlowBeforeTaxNow);
            storageTotalCost.updateAndGet(v -> v + cashOutflowNow);

        });
        //储能IRR（税前）
        Double storageIRRPreTax = storageIRRPreTaxEst(netCashFlowBeforeTax);
        //储能利润总额=储能总收入-储能总成本费用
        Double storageTotalProfit = storageTotalRev.get() - storageTotalCost.get();
        //投资方储能X年总收益=储能总收入*(1-客户)
        Double invTotalRevXYears = storageTotalRev.get() * (1 - customerShare);
        //投资方储能平均年收益=储能总收入*(1-客户)/年限
        Double invAvgAnnualRev = invTotalRevXYears / assessPeriod;
        //电力用户储能分成比例=customerShare
        //电力用户储能X年总收益=储能总收入*电力用户储能分成比例
        Double usrTotalRevXYears = storageTotalRev.get() * customerShare;
        //电力用户储能平均年收益=储能总收入*电力用户储能分成比例/年限
        Double usrAvgAnnualRev = usrTotalRevXYears / assessPeriod;
        DecimalFormat df = new DecimalFormat("#.00");
        RevenueEstVO revenueEstVO = new RevenueEstVO();
        revenueEstVO.setStorageIRRPreTax(Double.valueOf(df.format(storageIRRPreTax * 100.0)));
        revenueEstVO.setStoragePayback(Double.valueOf(df.format(storagePayback.get())));
        revenueEstVO.setStorageTotalRev(Double.valueOf(df.format(storageTotalRev.get() / 10000.0)));
        revenueEstVO.setStorageTotalCost(Double.valueOf(df.format(storageTotalCost.get() / 10000.0)));
        revenueEstVO.setStorageTotalProfit(Double.valueOf(df.format(storageTotalProfit / 10000.0)));
        revenueEstVO.setInvTotalRevXYears(Double.valueOf(df.format(invTotalRevXYears / 10000.0)));
        revenueEstVO.setInvAvgAnnualRev(Double.valueOf(df.format(invAvgAnnualRev / 10000.0)));
        revenueEstVO.setCustomerShare(Double.valueOf(df.format(customerShare * 100)));
        revenueEstVO.setUsrTotalRevXYears(Double.valueOf(df.format(usrTotalRevXYears / 10000.0)));
        revenueEstVO.setUsrAvgAnnualRev(Double.valueOf(df.format(usrAvgAnnualRev / 10000.0)));
        revenueEstVO.setCashInflow(cashInflow.stream().map(d -> Double.valueOf(df.format(d / 10000.0))).collect(Collectors.toList()));
        revenueEstVO.setAssetIncomeAfterShare(assetIncomeAfterShare.stream().map(d -> Double.valueOf(df.format(d / 10000.0))).collect(Collectors.toList()));
        revenueEstVO.setCashOutflow(cashOutflow.stream().map(d -> Double.valueOf(df.format(d / 10000.0))).collect(Collectors.toList()));
        revenueEstVO.setConstInvest(constInvest.stream().map(d -> Double.valueOf(df.format(d / 10000.0))).collect(Collectors.toList()));
        revenueEstVO.setOperatingCost(operatingCost.stream().map(d -> Double.valueOf(df.format(d / 10000.0))).collect(Collectors.toList()));
        revenueEstVO.setVat(vat.stream().map(d -> Double.valueOf(df.format(d / 10000.0))).collect(Collectors.toList()));
        revenueEstVO.setNetCashFlowBeforeTax(netCashFlowBeforeTax.stream().map(d -> Double.valueOf(df.format(d / 10000.0))).collect(Collectors.toList()));
        revenueEstVO.setResult(true);
        return revenueEstVO;
    }
    //投资总额
    public Double totalInvestmentEst(Double designCapacity,Double purchasePrice) {
        Double result = designCapacity * purchasePrice * 1000;
        return result;
    }
    //现金流入
    public Double CashInflowEst(Double peakValleyIncome,Double customerShare,Double fixedAssetsResidual) {
        Double result = peakValleyIncome * (1 - customerShare) - fixedAssetsResidual;
        return result;
    }
    //资产方收入=资产方扣除客户分享收益
    public Double assetIncomeAfterShareEst(Double peakValleyIncome,Double customerShare) {
        Double result = peakValleyIncome * (1 - customerShare);
        return result;
    }
    //现金流出
    public Double cashOutflowEst(Double constInvest,Double operatingCost,Double vat) {
        Double result = constInvest + operatingCost + vat;
        return result;
    }
    //建设投资
    public Double constInvestEst(Double totalInvestment,Double batteryReplRatio) {
        Double result = totalInvestment * batteryReplRatio;
        return result;
    }
    //经营成本
    public Double operatingCostEst(Double maintenanceCost,Double insuranceFee,Double platformFee,
                                   Double depreciationCost,Double batteryOMCost,Double laborCost) {
        Double result =  maintenanceCost + insuranceFee + platformFee + depreciationCost + batteryOMCost + laborCost;
        return result;
    }
    //设备维护费用
    public Double maintenanceCostEst(Double designCapacity,Double maintenanceRatio) {
        Double result = designCapacity * maintenanceRatio * 1000;
        return result;
    }
    //保险费
    public Double insuranceFeeEst(Double totalInvestment,Double insuranceRate) {
        Double result = totalInvestment * insuranceRate;
        return result;
    }
    //平台服务费
    public Double platformFeeEst(Double assetIncomeAfterShare, Double platformRate) {
        Double result = assetIncomeAfterShare * platformRate;
        return result;
    }
    //增值税
    public Double vatEst(Double outputTax,Double constInputTaxBal,Double operationInputTax) {
        Double result = (outputTax - constInputTaxBal - operationInputTax) < 0 ? 0 : (outputTax - constInputTaxBal - operationInputTax);
        return result;
    }
    //销项税额
    public Double outputTaxEst(Double assetIncomeAfterShare) {
        Double result = assetIncomeAfterShare / 1.06 * 0.06;
        return result;
    }
    //次年建设期进项税余额
    public Double constInputTaxBalEst(Double preConstInputTaxBal,Double preOutputTax,Double preOperationInputTax) {
        Double result = preConstInputTaxBal - preOutputTax + preOperationInputTax;
        return result < 0 ? 0 : result;
    }
    //运营期进项税
    public Double operationInputTaxEst(Double operatingCost) {
        Double result = operatingCost / 1.06 * 0.06;
        return result;
    }
    //净现金流量
    public Double netCashFlowBeforeTaxEst(Double cashInflow,Double cashOutflow) {
        Double result = cashInflow - cashOutflow;
        return result;
    }
    private static final double EPSILON = 1e-6;

    private static final int MAX_ITER = 1000;

    /**
     * 计算内部收益率（IRR）
     *
     * @param cashFlows 所得税前净现金流量列表
     * @return 财务内部收益率（IRR）
     */
    public static double storageIRRPreTaxEst(List<Double> cashFlows) {
        double lowerBound = -1.0;
        double upperBound = 1.0;
        double irr = 0.0;
        int iter = 0;
        while (Math.abs(upperBound - lowerBound) > EPSILON && iter < MAX_ITER) {
            irr = (lowerBound + upperBound) / 2;
            double npv = calculateNPV(irr, cashFlows);
            if (npv > 0) {
                lowerBound = irr;
            } else {
                upperBound = irr;
            }
            iter++;
        }
        if (iter == MAX_ITER) {
            System.out.println("警告: 超过最大迭代次数，IRR 可能未能完全收敛。");
        }
        return irr;
    }

    /**
     * 计算净现值（NPV）
     *
     * @param rate      折现率（试探的 IRR）
     * @param cashFlows 所得税前净现金流量列表
     * @return 对应折现率下的净现值
     */
    private static double calculateNPV(double rate, List<Double> cashFlows) {
        double npv = 0.0;
        for (int t = 0; t < cashFlows.size(); t++) {
            npv += cashFlows.get(t) / Math.pow(1 + rate, t);
        }
        return npv;
    }
    //todo,福莹提供年充放电的状态、电量和时间
    //todo,电价库提供月份、类型、时段、电价
    //充放电的状态、电量和时间 & 月份、类型、时段、电价 》》》得到》》》
    //平均充放电电价、平均充放电小时数、年充放电量、年充放电金额、削峰填谷总收益
    public Map<String,Double> powerBasicParameter(List<RevenueLoadDto> list,PredictEnergyCommand command, ElectricityQueryYearlyResponse response) {
        List<PredictEnergyModel> capacityList = command.getCapacityList();
        Map<String,List<PriceData>> priceMap = response.getPricesList().stream()
                .collect(Collectors.toMap(PriceInfo::getMonth, priceInfo -> priceInfo.getPrices().stream()
                        .sorted(Comparator.comparing(PriceData::getPeriod))
                        .collect(Collectors.toList())));
        AtomicReference<Double> chargeAmount = new AtomicReference<>(0.0);
        AtomicReference<Double> dischargeAmount = new AtomicReference<>(0.0);
        String projectId = command.getProjectId();
        IntStream.range(0, capacityList.size()).forEach(i -> {
            PredictEnergyModel model = capacityList.get(i);
            Double value = model.getValue();
            LocalDateTime date = model.getDate();
            RevenueLoadDto dto = new RevenueLoadDto();
            dto.setId(projectId + date);
            dto.setProjectId(projectId);
            dto.setPower(value);
            dto.setTime(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));
            list.add(dto);
            int month = date.getMonthValue();
            int hour = date.getHour();
            int minute = date.getMinute();
            int count = hour * 4 + minute / 15;
            PriceData priceData = priceMap.get(String.valueOf(month)).get(count);
            Double endValue = value * priceData.getPrice().doubleValue();
//            log.info("电量:{},月份：{},对应电价日期：{},电价：{},结果：{}",JSON.toJSONString(model),month,date,JSON.toJSON(priceData),endValue);
            if (value < 0) {
                chargeAmount.getAndUpdate(v -> v + endValue);
            } else {
                dischargeAmount.getAndUpdate(v -> v + endValue);
            }

                });
        Map<String,Double> resultMap = new HashMap<>();
//        resultMap.put("avgChargingPrice",Double.valueOf(0));
//        resultMap.put("avgDischargePrice",Double.valueOf(0));
//        resultMap.put("annualChargingHours",Double.valueOf(0));
//        resultMap.put("annualDischargeHours",Double.valueOf(0));
//        resultMap.put("annualChargingAmount",Double.valueOf(0));
//        resultMap.put("annualDischargeAmount",Double.valueOf(0));
//        resultMap.put("annualChargingAmountTax",Double.valueOf(0));
//        resultMap.put("annualDischargeAmountTax",Double.valueOf(0));
        resultMap.put("peakValleyIncome",dischargeAmount.get() + chargeAmount.get());
        return resultMap;
    }

}
