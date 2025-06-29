package com.example.vvpcommom.arima;

import java.util.ArrayList;
import java.util.Arrays;
import timeseries.TimeSeries;
import timeseries.models.Forecast;
import timeseries.models.arima.Arima;


public   class TimeSeriesPredictUtil {

    private TimeSeriesPredictUtil() {
    }

    /**
     * 对时间序列进行预测，输入对数据需要是有序甚至规律的时间序列。另外，不需要输入时间时间，
     * 如果你需要得到时间和预测值的键值对，你需要自己组装
     *
     * @param data  需要预测的数据
     * @param steps 你需要预测未来多少个单位的数据，比如5天，5周或者5个月或者5年。
     * @param draw  是否绘图，测试时可以通过绘图直观的翻译数据预测是否准确
     * @return 预测值数组
     */
    public static double[] predict(double[] data, int steps, boolean draw) {

        TimeSeries series = new TimeSeries(data);

        ArrayList<Integer> options = new ArrayList<>();
        int[] params = new int[]{0, 1, 2};
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();

        repeatableArrangement(3, params, options, result);

        ArrayList<Integer> bestChoice = new ArrayList<>(3);
        ArrayList<Integer> sBestChoice = new ArrayList<>(6);
        double minimalAic = Double.MAX_VALUE;

        try {
            for (ArrayList<Integer> item : result) {

                try {
                    if (item.isEmpty()) {
                        continue;
                    }

                    Arima.ModelOrder order = Arima.order(item.get(0), item.get(1), item.get(2));
                    Arima model = Arima.model(series, order);
                    double aic = model.aic();

                    if (aic < minimalAic) {
                        minimalAic = aic;
                        bestChoice.clear();
                        bestChoice.addAll(item);

                        sBestChoice.clear();
                    }

                    for (ArrayList<Integer> sItem : result) {
                        try {
                            if (sItem.isEmpty()) {
                                continue;
                            }

                            Arima.ModelOrder sOrder = Arima.order(item.get(0), item.get(1), item.get(2),
                                    sItem.get(0), sItem.get(1), sItem.get(2));
                            Arima sModel = null;
                            try {
                                sModel = Arima.model(series, sOrder);
                            } catch (Exception ex) {
                                System.out.println("data = " + ex);
                                continue;
                            }
                            double sAic = sModel.aic();

                            if (sAic < minimalAic) {
                                minimalAic = sAic;
                                bestChoice.clear();

                                sBestChoice.clear();
                                sBestChoice.addAll(item);
                                sBestChoice.addAll(sItem);
                            }

                        } catch (Exception ex) {
                            System.out.println("data = " + ex);
                            continue;
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("data = " + ex);
                    continue;
                }
            }
        } catch (Exception ex) {
            System.out.println("data = " + Arrays.toString(data) + ", steps = " + steps + ", draw = " + draw);
        }

        Arima.ModelOrder order = sBestChoice.isEmpty()
                ? Arima.order(bestChoice.get(0), bestChoice.get(1), bestChoice.get(2))
                : Arima.order(sBestChoice.get(0), sBestChoice.get(1), sBestChoice.get(2),
                sBestChoice.get(3), sBestChoice.get(4), sBestChoice.get(5));
        Arima model = Arima.model(series, order);

        Forecast forecast = model.forecast(steps);

        if (draw) {
            forecast.plot();
        }

        return forecast.forecast().asArray();
    }

    private static void repeatableArrangement(int k, int[] arr, ArrayList<Integer> tmpArr,
                                              ArrayList<ArrayList<Integer>> result) {
        if (k == 1) {
            for (int j : arr) {
                tmpArr.add(j);
                ArrayList<Integer> tmp = new ArrayList<>(tmpArr);
                result.add(tmp);
                tmpArr.remove(tmpArr.size() - 1);
            }
        } else if (k > 1) {
            for (int j : arr) {
                tmpArr.add(j);
                repeatableArrangement(k - 1, arr, tmpArr, result);
                tmpArr.remove(tmpArr.size() - 1);
            }
        }
    }
}


