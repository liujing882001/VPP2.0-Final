package com.example.vvpscheduling.service.ancillary.service;


public interface IAncillaryServicesService {

    /**
     * 开启定时任务
     *
     * @param asId
     */
    void initAncillaryTask(String asId);

    /**
     * 结束定时任务
     *
     * @param asId
     */
    void cancelAncillaryTask(String asId);

}
