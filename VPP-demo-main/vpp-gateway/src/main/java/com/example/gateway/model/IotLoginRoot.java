/**
 * Copyright 2023 json.cn
 */
package com.example.gateway.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class IotLoginRoot implements Serializable {

    private String token;
    private String refreshToken;
}
