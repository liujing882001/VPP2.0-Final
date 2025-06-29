package com.example.vvpdomain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "library_rid")
public class LibraryRid {
    /**
     * dnID_resourceID_rID
     */
    @Id
    @Column(name = "id")
    private String id;

    /**
     * 虚拟电厂编码
     */
    @Column(name = "dn_id")
    private String dnID;

    /**
     * 设备id
     */
    @Column(name = "resource_id")
    private String resourceID;

    /**
     * 上报数据类型
     */
    @Column(name = "rid")
    private Integer rID;

    /**
     * 上报数据类型详情
     */
    @Column(name = "rid_desc")
    private String ridDesc;

    /**
     * 设备序列号
     */
    @Column(name = "device_sn")
    private String deviceSn;

    /**
     * 系统id
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * 节点id
     */
    @Column(name = "node_id")
    private String nodeId;
}
