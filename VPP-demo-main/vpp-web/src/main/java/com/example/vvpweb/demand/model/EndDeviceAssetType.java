package com.example.vvpweb.demand.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EndDeviceAssetType {
    Electric_Vehicle("电动汽车"),
    Energy_Management_System("智慧能源管理系统"),
    EVSE("充电桩"),
    Exterior_Lighting("外部照明设施"),
    Generation_Systems("发电系统"),
    Interior_Lighting("内部照明设施"),
    Irrigation_Pump("排灌水泵"),
    Load_Control_Switch("负荷控制开关"),
    Managed_Commercial_and_Industrial_Loads("工商业负荷管理器");

    private String desc;
}
