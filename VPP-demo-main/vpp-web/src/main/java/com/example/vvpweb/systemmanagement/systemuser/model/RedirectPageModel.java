package com.example.vvpweb.systemmanagement.systemuser.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RedirectPageModel implements Serializable {

    String token;
    String url;
}
