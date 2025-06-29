package com.example.vvpweb.smartscreen.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class NameValueModel implements Serializable {
    private String name;
    private String value;
}
