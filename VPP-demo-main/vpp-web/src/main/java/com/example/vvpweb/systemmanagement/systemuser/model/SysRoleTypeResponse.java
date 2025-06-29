package com.example.vvpweb.systemmanagement.systemuser.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysRoleTypeResponse implements Serializable {

    int roleKey;

    String roleKeyDesc;
}
