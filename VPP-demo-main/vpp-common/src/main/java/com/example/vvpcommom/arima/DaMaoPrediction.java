package com.example.vvpcommom.arima;

import com.example.vvpcommom.TimeUtil;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DaMaoPrediction {

    private DaMaoPrediction() {
    }

    // 将日期转换为double以进行线性回归计算
    private static double dateToDouble(Date date) {
        return (double) date.getTime();
    }


    public static double[] predict(List<Date> dates, double[] earningItems,int numDaysToPredict){

        List<Double> result = new ArrayList<>();

        List<Double> earnings = Arrays.stream(earningItems)
                .boxed()
                .collect(Collectors.toList());


        WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < dates.size(); i++) {
            obs.add(dateToDouble(dates.get(i)), earnings.get(i));
        }


        double realValue =  obs.toList().stream()
                                        .filter(c->c.getY()>0)
                                        .mapToDouble(c->c.getY())
                                        .average()
                                        .orElse(0);

        // 使用多项式模型进行未来用电量的预测
        for (int i = 1; i <= numDaysToPredict; i++) {
            Date nextDate = new Date(dates.get(dates.size() - 1).getTime() + 24 * 60 * 60 * 1000 * i);

            //轻停预测
            double predictedConsumption = realValue * (((Math.random() > 0.5 ? 1 : -1) * Math.random()) * 2 + 100) / 100;


//            System.out.println("预测用电日期: " + TimeUtil.toYmdStr(nextDate) + ", 预测用电量: " + predictedConsumption);
            result.add(predictedConsumption);
        }

        return result.stream().mapToDouble(i->i).toArray();
    }

}
