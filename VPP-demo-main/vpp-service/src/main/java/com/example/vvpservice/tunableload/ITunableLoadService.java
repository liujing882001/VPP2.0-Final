package com.example.vvpservice.tunableload;

import com.example.vvpservice.tunableload.model.RTLoadModel;
import com.example.vvpservice.tunableload.model.RTLoadMonthModel;

import java.util.Date;
import java.util.List;

public interface ITunableLoadService {

    List<RTLoadModel> getNearlyADayList(List<String> ids);

    double findNowLoad(List<String> ids);

    List<RTLoadModel> getNearlySevenDaysList(List<String> ids);


    List<RTLoadMonthModel> getNearlyAMonthList(List<String> ids);

    List<RTLoadMonthModel> getAutoMonthList(Date ts_s, Date ts_e, List<String> ids);
}
