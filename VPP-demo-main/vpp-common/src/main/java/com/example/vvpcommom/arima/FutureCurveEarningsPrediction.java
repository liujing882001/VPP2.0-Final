package com.example.vvpcommom.arima;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.example.vvpcommom.TimeUtil;
import org.apache.commons.math3.analysis.function.HarmonicOscillator;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.optimization.fitting.*;
import org.apache.commons.math3.optimization.general.*;
import org.apache.commons.math3.analysis.function.HarmonicOscillator;

public class FutureCurveEarningsPrediction {

    private FutureCurveEarningsPrediction() {
    }


    // 执行线性回归计算
    public static double[] predict(List<Date> dates, double[] earnings,int numDaysToPredict) throws ParseException {

        List<Double> result = new ArrayList<>();

        // 计算收益与日期之间的线性关系
        double[] coefficients = linearRegression(dates, Arrays.stream(earnings).boxed().collect(Collectors.toList()));

        // 使用线性回归模型进行未来收益的预测
        for (int i = 1; i <= numDaysToPredict; i++) {
            Date nextDate = new Date(dates.get(dates.size() - 1).getTime() + 24 * 60 * 60 * 1000 * i);
            double predictedEarnings = coefficients[0] + coefficients[1] * dateToDouble(nextDate);
           System.out.println("预测收益日期: " + TimeUtil.toYmdStr(nextDate) + ", 预测收益值: " + predictedEarnings);
            result.add(predictedEarnings);
        }

        return result.stream().mapToDouble(i->i).toArray();
    }

    // 执行线性回归计算
    private static double[] linearRegression(List<Date> dates, List<Double> earnings) {
        int n = dates.size();
        double sumX = 0.0;
        double sumY = 0.0;
        double sumXY = 0.0;
        double sumX2 = 0.0;

        for (int i = 0; i < n; i++) {
            double x = dateToDouble(dates.get(i));
            double y = earnings.get(i);

            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        return new double[]{intercept, slope};
    }

    // 将日期转换为double以进行线性回归计算
    private static double dateToDouble(Date date) {
        return (double) date.getTime();
    }
}
