package com.example.vvpweb.sensetime.load;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class LoadData implements Serializable {
    private List<Load> load = new ArrayList<>();
}