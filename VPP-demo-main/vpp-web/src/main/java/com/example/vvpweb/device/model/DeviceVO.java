package com.example.vvpweb.device.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeviceVO {
    private String deviceId;

    /**
     * 设备序列号
     */
    private String deviceSn;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 设备品牌
     */
    private String deviceBrand;

    /**
     * 备注
     */
    private String deviceLabel;

    /**
     * 所属节点
     */
    private String nodeName;

    /**
     * 所属系统ID
     */
    private String systemId;

    /**
     * 所属系统
     */
    private String systemName;

    /**
     * 设备类型:计量或者非计量设备
     * other 是 非计量设备
     */
    private String configKey;

    /**
     * 额定功率
     */
    private Double deviceRatedPower;
    /**
     * 是否在线
     */
    private Boolean online;
    /**
     * 负荷类型：-       ，
     * 空调（air_conditioning），
     * 充电桩（charging_piles），
     * 照明（lighting），
     * 其它（others）
     */
    private String loadType;
    /**
     符合性质：    -    ，
     可调节负荷(adjustable_load)，
     可转移负荷(transferable_load)，
     可中断负荷(interruptible_load)，
     其它负荷(other_loads)
     */
    private String loadProperties;

    /**
     * 是否在线
     */
    private Boolean mecOnline;

    private String mecName;

    private List<DeviceVO.DevicePointVO> pointViewList = new ArrayList<>();
    @Data
    public static class DevicePointVO {
        private String pointId;

        private String pointSn;

        private String pointName;

        private String pointDesc;
        private Boolean online;
    }
}
