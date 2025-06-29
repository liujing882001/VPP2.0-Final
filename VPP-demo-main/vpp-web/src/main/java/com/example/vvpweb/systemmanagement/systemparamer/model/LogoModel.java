package com.example.vvpweb.systemmanagement.systemparamer.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogoModel implements Serializable {

    private String id;
    private String paramName;

    private String sysLogo;
    private String mainLogo;

    private String platformName;

}
