package com.example.vvpweb.systemmanagement.systemuser.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysUserShareRatioModel implements Serializable {
    String userId;
    Double shareRatio;
}
