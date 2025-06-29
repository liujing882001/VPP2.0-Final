package com.example.vvpcommom.arima;

import com.example.vvpcommom.TimeUtil;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class EvaluatePolynomialPrediction {

    private EvaluatePolynomialPrediction() {
    }


    // 执行线性回归计算
    public static double[] predict(List<Date> dates, double[] earningItems,int numDaysToPredict){

        List<Double> result = new ArrayList<>();

        List<Double> earnings = Arrays.stream(earningItems)
                                        .boxed()
                                        .collect(Collectors.toList());
        // 使用多项式拟合进行曲线模型预测
        int degree = 2; // 多项式的阶数
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
        WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < dates.size(); i++) {
            obs.add(dateToDouble(dates.get(i)), earnings.get(i));
        }
        double[] coefficients = fitter.fit(obs.toList());

        // 使用多项式模型进行未来用电量的预测
        for (int i = 1; i <= numDaysToPredict; i++) {
            Date nextDate = new Date(dates.get(dates.size() - 1).getTime() + 24 * 60 * 60 * 1000 * i);
            double predictedConsumption = evaluatePolynomial(coefficients, dateToDouble(nextDate));
//            System.out.println("预测用电日期: " + TimeUtil.toYmdStr(nextDate) + ", 预测用电量: " + predictedConsumption);
            result.add(predictedConsumption);
        }

        return result.stream().mapToDouble(i->i).toArray();
    }

    // 使用多项式系数计算预测值
    private static double evaluatePolynomial(double[] coefficients, double x) {
        double result = 0.0;
        for (int i = 0; i < coefficients.length; i++) {
            result += coefficients[i] * Math.pow(x, i);
        }
        return result;
    }

    // 将日期转换为double以进行线性回归计算
    private static double dateToDouble(Date date) {
        return (double) date.getTime();
    }
}
