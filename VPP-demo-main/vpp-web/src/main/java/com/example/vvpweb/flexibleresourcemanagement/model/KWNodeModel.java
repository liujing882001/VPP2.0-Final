package com.example.vvpweb.flexibleresourcemanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KWNodeModel implements Serializable {

    Double device_rated_power;
}
