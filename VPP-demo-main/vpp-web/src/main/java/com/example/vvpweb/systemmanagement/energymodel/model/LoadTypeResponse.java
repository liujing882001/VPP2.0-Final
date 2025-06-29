package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoadTypeResponse implements Serializable {
    /**
     *  负荷类型
     */
    private String load_type;
    /**
     * 负荷类型名称
     */
    private String load_type_name;
    /**
     * 符合性质
     */
    private String load_properties;

    /**
     * 符合性质 名称
     */
    private String load_properties_name;
}
