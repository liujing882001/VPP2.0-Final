package com.example.vvpcommom;

public class FieldConvertUtil {
    /**
     * 负荷类型：-       ，
     * 空调（air_conditioning），
     * 充电桩（charging_piles），
     * 照明（lighting），
     * 其它（others）
     */

    public static String convertLoadType(String loadType) {
        if("-".equals(loadType)){
            return "-";
        }
        if("air_conditioning".equals(loadType)){
            return "空调";
        }
        if("charging_piles".equals(loadType)){
            return "充电桩";
        }
        if("lighting".equals(loadType)){
            return "照明";
        }
        if("others".equals(loadType)){
            return "其它";
        }
        return "";
    }


    /**
     符合性质：    -    ，
     可调节负荷(adjustable_load)，
     可转移负荷(transferable_load)，
     可中断负荷(interruptible_load)，
     其它负荷(other_loads)
     */
    public static String convertLoadProperties(String loadProperties) {
        if("-".equals(loadProperties)){
            return "-";
        }
        if("adjustable_load".equals(loadProperties)){
            return "可调节负荷";
        }
        if("transferable_load".equals(loadProperties)){
            return "可转移负荷";
        }
        if("interruptible_load".equals(loadProperties)){
            return "可中断负荷";
        }
        if("other_loads".equals(loadProperties)){
            return "其它负荷";
        }
        return "";
    }
}
