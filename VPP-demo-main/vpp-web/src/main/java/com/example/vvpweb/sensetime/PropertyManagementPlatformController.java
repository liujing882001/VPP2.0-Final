package com.example.vvpweb.sensetime;

import com.example.vvpcommom.EntityUtils;
import com.example.vvpcommom.PassToken;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpdomain.DeviceRepository;
import com.example.vvpdomain.IotTsKvLastRepository;
import com.example.vvpdomain.IotTsKvMeteringDevice96Repository;
import com.example.vvpdomain.entity.IotTsKvLast;
import com.example.vvpdomain.entity.IotTsKvMeteringDevice96;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.tunableload.ITunableLoadService;
import com.example.vvpservice.tunableload.model.RTLoadModel;
import com.example.vvpweb.flexibleresourcemanagement.model.KWNodeModel;
import com.example.vvpweb.flexibleresourcemanagement.model.LoadResponse;
import com.example.vvpweb.sensetime.load.Load;
import com.example.vvpweb.sensetime.load.LoadData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("/property_management_platform/load/")
@CrossOrigin
@Api(value = "第三方使用", tags = {"第三方使用"})
public class PropertyManagementPlatformController {


    @Resource
    private IotTsKvMeteringDevice96Repository device96Repository;

    @PassToken
    @ApiOperation("网管平台-负荷统计")
    @RequestMapping(value = "getLoadCount", method = {RequestMethod.POST})
    public ResponseResult<List<Load>> getLoadCount(@RequestParam("noHousehold") String noHousehold) {

        try {
            SimpleDateFormat sdf_ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf_ymdhms.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            List<Load> loads = new ArrayList<>();
            List<IotTsKvMeteringDevice96> device96s = device96Repository.findAll96LoadData(noHousehold);

            if (device96s != null && device96s.size() > 0) {

                for (IotTsKvMeteringDevice96 device96 : device96s) {
                    if (new Date().compareTo(device96.getCountDataTime()) <= 0) {
                        continue;
                    }

                    Load load = new Load();
                    load.setTime(sdf_ymdhms.format(device96.getCountDataTime()));
                    load.setValue(device96.getHTotalUse());
                    loads.add(load);
                }
            }
            return ResponseResult.success(loads);
        } catch (Exception e) {
        }
        return ResponseResult.error("获取储能资源数据失败!");
    }
}
