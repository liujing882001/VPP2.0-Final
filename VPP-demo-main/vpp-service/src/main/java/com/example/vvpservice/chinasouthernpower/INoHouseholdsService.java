package com.example.vvpservice.chinasouthernpower;

import com.example.vvpservice.chinasouthernpower.model.Data;
import com.example.vvpservice.chinasouthernpower.model.DeviceInfo;
import com.example.vvpservice.chinasouthernpower.model.DeviceSetInfo;

import java.util.Date;
import java.util.List;

public interface INoHouseholdsService {

    /**
     * @param resourceId 户号
     * @return
     */
    boolean noHouseholdsDeviceIsOnline(String resourceId);

    /**
     * @param resourceId 户号
     * @return
     */
    Data lastData(String resourceId);

    /**
     * @param resourceId 户号
     * @param start      开始时间
     * @param end        结束时间
     * @return
     */
    List<Data> findHistoryData(String resourceId, Date start, Date end);


    /**
     * @param noHouseholds         户号
     * @param responsibleStartTime 响应开始时间
     * @param responsibleEndTime   响应结束时间
     * @return 该户号下冷机设备可参与需求响应的功率（负荷）
     */
    double findResponsiblePowerByNoHouseholds(String noHouseholds, Date responsibleStartTime, Date responsibleEndTime);


    /**
     * @param noHouseholds 户号
     * @return 获取该户号下当前在线冷机设备的信息集合（冷机设备编号，冷机设备额定功率,冷机设备当前冷冻水出水温度，
     * ,冷机设备当前冷冻水进水温度,冷机设备当前冷冻水出水温度设置值）
     */
    List<DeviceInfo> findNoHouseholdsDeviceInfo(String noHouseholds);

}
