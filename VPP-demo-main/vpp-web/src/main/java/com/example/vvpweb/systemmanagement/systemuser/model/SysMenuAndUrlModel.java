package com.example.vvpweb.systemmanagement.systemuser.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SysMenuAndUrlModel implements Serializable {

    /**
     * 菜单ID
     */
    private String menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    private String component;
}
