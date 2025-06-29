package com.example.vvpcommom;

public class SysCfgTypeUtil {

    /**
     * 光伏发电
     */
    public static String PV = "photovoltaic";

    //region 光伏发电 下集合
    /**
     * 散射辐照度
     */
    public static String PV_Diffuse_Horizontal_Radiation = "Diffuse_Horizontal_Radiation";
    /**
     * 功率
     */
    public static String PV_Active_Power = "Active_Power";
    /**
     * 能耗，有功电能
     */
    public static String PV_Energy = "Energy";
    /**
     * 风向
     */
    public static String PV_Wind_Direction = "Wind_Direction";
    /**
     * 总辐射度
     */
    public static String PV_Global_Horizontal_Radiation = "Global_Horizontal_Radiation";
    /**
     * 时间
     */
    public static String PV_Timestamp = "Timestamp";
    /**
     * 天气温度摄氏
     */
    public static String PV_Weather_Temperature_Celsius = "Weather_Temperature_Celsius";
    /**
     * 天气日降雨
     */
    public static String PV_Weather_Daily_Rainfall = "Weather_Daily_Rainfall";
    /**
     * 辐射总倾斜度
     */
    public static String PV_Radiation_Global_Tilted = "Radiation_Global_Tilted";
    /**
     * 天气相对湿度
     */
    public static String PV_Weather_Relative_Humidity = "Weather_Relative_Humidity";
    /**
     * 散射福照倾斜度
     */
    public static String PV_Radiation_Diffuse_Tilted = "Radiation_Diffuse_Tilted";
    /**
     * 风速
     */
    public static String PV_Wind_Speed = "Wind_Speed";
    //endregion

    /**
     * 可调负荷
     */
    public static String LOAD = "sys_transferable_load";

    //region 可调符合 下集合
    /**
     * 相对湿度
     */
    public static String LOAD_Relative_Humidity = "Relative_Humidity";
    /**
     * 最低温度
     */
    public static String LOAD_Min_Temperature = "Min_Temperature";
    /**
     * 平均温度
     */
    public static String LOAD_Avg_Temperature = "Avg_Temperature";
    /**
     * 负载，功率
     */
    public static String LOAD_Load = "Load";
    /**
     * 降雨量
     */
    public static String LOAD_Rainfall = "Rainfall";
    /**
     * 能耗，有功电能
     */
    public static String LOAD_Energy = "Energy";
    /**
     * 最高温度
     */
    public static String LOAD_Max_Temperature = "Max_Temperature";
    /**
     * 时间
     */
    public static String LOAD_Timestamp = "Timestamp";
    //endregion


    /**
     * 储能
     */
    public static String STORAGE_ENERGY = "storage_energy";
}
