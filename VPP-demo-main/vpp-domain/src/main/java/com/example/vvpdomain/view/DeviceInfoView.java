package com.example.vvpdomain.view;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author zph
 * @description 设备基本信息视图
 * @date 2024-02-20
 */
@Entity
@Getter
@Table(name = "device_info_view")
@EntityListeners(AuditingEntityListener.class)
public class DeviceInfoView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * id
     */
    @Column(name = "id")
    private String id;
    /**
     * 设备序列号
     */
    @Column(name = "device_sn")
    private String deviceSn;

    /**
     * 设备状态是否在线
     */
    @Column(name = "`online`")
    private Boolean online;

    /**
     * 参数键名
     */
    @Column(name = "config_key")
    private String configKey;
    /**
     * 负荷类型：-       ，
     * 空调（air_conditioning），
     * 充电桩（charging_piles），
     * 照明（lighting），
     * 其它（others）
     */
    @Column(name = "load_type")
    private String loadType;

    /**
     符合性质：    -    ，
     可调节负荷(adjustable_load)，
     可转移负荷(transferable_load)，
     可中断负荷(interruptible_load)，
     其它负荷(other_loads)
     */
    @Column(name = "load_properties")
    private String loadProperties;
    /**
     * 额定功率
     */
    @Column(name = "device_rated_power")
    private Double deviceRatedPower;
    /**
     * 实时功率
     */
    @Column(name = "device_real_power")
    private double deviceRealPower;
    /**
     * 是否存在load属性
     */
    @Column(name = "has_load_attribute")
    private Boolean hasLoadAttribute;

    public DeviceInfoView() {
    }

}