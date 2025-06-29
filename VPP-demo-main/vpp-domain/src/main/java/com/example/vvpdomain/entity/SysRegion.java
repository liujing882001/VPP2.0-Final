package com.example.vvpdomain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "sys_dict_region")
@EntityListeners(AuditingEntityListener.class)
public class SysRegion {

    /**
     * "地区主键编号"
     */

    @Id
    @Column(name = "region_id")
    private String regionId;

    /**
     * "地区名称"
     */

    @Column(name = "region_name")
    private String regionName;

    /**
     * "地区缩写"
     */

    @Column(name = "region_short_name")
    private String regionShortName;

    /**
     * "行政地区编号"
     */

    @Column(name = "region_code")
    private String regionCode;

    /**
     * "地区父id"
     */

    @Column(name = "region_parent_id")
    private String regionParentId;

    /**
     * "地区级别 1-省、自治区、直辖市 2-地级市、地区、自治州、盟 3-市辖区、县级市、县"
     */

    @Column(name = "region_level")
    private String regionLevel;


}


