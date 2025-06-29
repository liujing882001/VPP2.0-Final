package com.example.vvpweb.runschedule.runstrategy.model;


import com.example.vvpcommom.devicecmd.AirConditioningDTO;
import com.example.vvpcommom.devicecmd.OtherConditioningDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = false) // 添加这个注解
public class StrategyDetailModel extends StrategyViewModel {
    /**
     * 策略类型 0 空调策略 1 其他策略(针对照明、基站充电桩等，可对设备的启动/停止进行控制)
     */
    @ApiModelProperty(value = "策略类型 0 空调策略 1 其他策略(针对照明、基站充电桩等，可对设备的启动/停止进行控制)", name = "strategyType", required = true)
    private int strategyType;

    private List<String> deviceIdList;

    private OnceExe onceExe;

    private CycleExe cycleExe;

    private OtherConditioningDTO otherConditioningDTO;

    private AirConditioningDTO airConditioningDTO;

//    private Integer powerGrid;

    @Data
    public static class OnceExe {
        private String ymd;

        private String times;
    }

    @Data
    public static class CycleExe {
        private String cycleTimes;

        private List<Integer> cycleWeeks;
    }


}
