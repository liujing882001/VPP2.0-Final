package com.example.vvpservice.externalapi.service;

import com.example.vvpdomain.entity.DemandCalendar;
import com.example.vvpservice.externalapi.model.AvailVppDataDTO;
import com.example.vvpservice.externalapi.model.VppDataDTO;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface EPApiService {

    List<DemandCalendar> findByDateBetween(Date sDate, Date eDate);
    List<AvailVppDataDTO> getAvailVppData(List<String> nodeIds);
    VppDataDTO getVppDataList(String dataId, LocalDateTime sTime,LocalDateTime eTime);
    VppDataDTO getVppDataListNow(String dataId, LocalDateTime sTime,LocalDateTime eTime);

}
