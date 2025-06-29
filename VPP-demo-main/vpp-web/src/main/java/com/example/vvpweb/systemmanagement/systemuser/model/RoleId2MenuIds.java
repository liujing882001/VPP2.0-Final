package com.example.vvpweb.systemmanagement.systemuser.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RoleId2MenuIds {
    @ApiModelProperty(value = "角色id", name = "roleId", required = true)
    private String roleId;
    @ApiModelProperty(value = "菜单id集合", required = true)
    private List<String> menuIds;

}