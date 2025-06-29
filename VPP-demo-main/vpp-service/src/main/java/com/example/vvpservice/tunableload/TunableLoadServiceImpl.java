package com.example.vvpservice.tunableload;
import java.util.Date;

import com.example.vvpcommom.EntityUtils;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.AnalysisLoadDayRepository;
import com.example.vvpdomain.AnalysisLoadMonthRepository;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.tunableload.model.RTLoadModel;
import com.example.vvpservice.tunableload.model.RTLoadMonthModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TunableLoadServiceImpl implements ITunableLoadService {

    private static Logger logger = LoggerFactory.getLogger(TunableLoadServiceImpl.class);
    @Resource
    AnalysisLoadDayRepository analysisLoadDayRepository;
    @Resource
    AnalysisLoadMonthRepository AnalysisLoadMonthRepository;
    @Autowired
    private IUserService userService;

    public List<RTLoadModel> getNearlyADayList(List<String> ids) {
        SimpleDateFormat fmt_ymd_hds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fmt_ymd_hds.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        List<RTLoadModel> models = new ArrayList<>();

        try {
            Date dt_e = new Date();
            Date dt_s = fmt_ymd_hds.parse(fmt_ymd_hds.format(TimeUtil.dateAddDay(dt_e, -1)));

            List<Object[]> analysisLoadDayList = analysisLoadDayRepository.findAllNearlyADayList(dt_s, ids);
            if (analysisLoadDayList != null && analysisLoadDayList.size() > 0) {
                List<RTLoadModel> loadModels = EntityUtils.castEntity(analysisLoadDayList, RTLoadModel.class, new RTLoadModel());

                if (loadModels != null && loadModels.size() > 0) {
                    for (RTLoadModel loadModel : loadModels) {

                        RTLoadModel rtLoadModel = new RTLoadModel();
                        rtLoadModel.setTs(loadModel.getTs());
                        rtLoadModel.setValue(loadModel.getValue());
                        models.add(rtLoadModel);
                    }
                }

            }

            // 使用 Stream 排序
            List<RTLoadModel> remodels = models.stream()
                    .sorted(Comparator.comparing(RTLoadModel::getTs))
                    .collect(Collectors.toList());
            return remodels;

        } catch (Exception e) {
        }
        return null;
    }

    public double findNowLoad(List<String> ids) {
        try {
            SimpleDateFormat fmt_ymd_hds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            fmt_ymd_hds.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            Date dt_s = fmt_ymd_hds.parse(fmt_ymd_hds.format(TimeUtil.dateAddDay(new Date(), -1)));
            return analysisLoadDayRepository.findNowLoad(dt_s, ids);
        } catch (Exception e) {
        }
        return 0;
    }

    public List<RTLoadModel> getNearlySevenDaysList(List<String> ids) {
        SimpleDateFormat fmt_ymd_hds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 国内时区是GMT+8
        fmt_ymd_hds.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        try {
            Date dt_e = new Date();
            Date dt_s = fmt_ymd_hds.parse(fmt_ymd_hds.format(TimeUtil.dateAddDay(dt_e, -7)));

            List<RTLoadModel> models = new ArrayList<>();

            List<Object[]> analysisLoadDayList = analysisLoadDayRepository.findAllNearlyADayList(dt_s, ids);
            if (analysisLoadDayList != null && analysisLoadDayList.size() > 0) {
                List<RTLoadModel> rtLoadModels = EntityUtils.castEntity(analysisLoadDayList, RTLoadModel.class, new RTLoadModel());
                if (rtLoadModels != null && rtLoadModels.size() > 0) {
                    for (RTLoadModel loadModel : rtLoadModels) {
                        RTLoadModel rtLoadModel = new RTLoadModel();
                        rtLoadModel.setTs(loadModel.getTs());
                        rtLoadModel.setValue(loadModel.getValue());
                        models.add(rtLoadModel);
                    }
                }
            }
            // 使用 Stream 排序
            List<RTLoadModel> remodels = models.stream()
                    .sorted(Comparator.comparing(RTLoadModel::getTs))
                    .collect(Collectors.toList());
            return remodels;
        } catch (Exception e) {
        }
        return null;
    }

    public List<RTLoadMonthModel> getNearlyAMonthList(List<String> ids) {

        SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
        // 国内时区是GMT+8
        fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        try {
            Date dt = new Date();

            Date ts = fmt_ymd.parse(fmt_ymd.format(TimeUtil.dateAddMonths(dt, -1)));
            Date te = fmt_ymd.parse(fmt_ymd.format(dt));

            List<RTLoadMonthModel> models = new ArrayList<>();

            List<Object[]> analysisLoadMonthList = AnalysisLoadMonthRepository.findAllAutoMonthList(ids, ts, te);
            if (analysisLoadMonthList != null && analysisLoadMonthList.size() > 0) {

                List<RTLoadMonthModel> loadMonthModels = EntityUtils.castEntity(analysisLoadMonthList, RTLoadMonthModel.class, new RTLoadMonthModel());
                if (loadMonthModels != null && loadMonthModels.size() > 0) {
                    for (RTLoadMonthModel month : loadMonthModels) {
                        RTLoadMonthModel rtLoadMonthModel = new RTLoadMonthModel();
                        rtLoadMonthModel.setTs(month.getTs());
                        rtLoadMonthModel.setValue(month.getValue());
                        models.add(rtLoadMonthModel);
                    }
                }
                // 使用 Stream 排序
                List<RTLoadMonthModel> remodels = models.stream()
                        .sorted(Comparator.comparing(RTLoadMonthModel::getTs))
                        .collect(Collectors.toList());
                return remodels;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public List<RTLoadMonthModel> getAutoMonthList(Date ts_s, Date ts_e, List<String> ids) {
        SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
        // 国内时区是GMT+8
        fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));


        try {
            Date ts = fmt_ymd.parse(fmt_ymd.format(TimeUtil.getMonthStart(ts_s)));
            Date te = fmt_ymd.parse(fmt_ymd.format(TimeUtil.getMonthEnd(ts_e)));

            List<RTLoadMonthModel> models = new ArrayList<>();

            List<Object[]> analysisLoadMonthList = AnalysisLoadMonthRepository.findAllAutoMonthList(ids, ts, te);
            if (analysisLoadMonthList != null && analysisLoadMonthList.size() > 0) {

                List<RTLoadMonthModel> loadMonthModels = EntityUtils.castEntity(analysisLoadMonthList, RTLoadMonthModel.class, new RTLoadMonthModel());
                if (loadMonthModels != null && loadMonthModels.size() > 0) {
                    for (RTLoadMonthModel month : loadMonthModels) {
                        RTLoadMonthModel rtLoadMonthModel = new RTLoadMonthModel();
                        rtLoadMonthModel.setTs(month.getTs());
                        rtLoadMonthModel.setValue(month.getValue());
                        models.add(rtLoadMonthModel);
                    }
                }

                // 使用 Stream 排序
                List<RTLoadMonthModel> remodels = models.stream()
                        .sorted(Comparator.comparing(RTLoadMonthModel::getTs))
                        .collect(Collectors.toList());
                return remodels;
            }
        } catch (Exception e) {
        }
        return null;
    }
}
