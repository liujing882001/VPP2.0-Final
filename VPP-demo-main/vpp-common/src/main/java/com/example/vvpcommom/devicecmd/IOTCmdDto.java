package com.example.vvpcommom.devicecmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IOTCmdDto implements Serializable {

    private String mecId;

    private String deviceId;

    private List<DevicePointDto> devicePointDtoList = new ArrayList<>();

}
