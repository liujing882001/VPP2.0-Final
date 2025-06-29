package com.example.vvpweb.systemmanagement.nodemodel;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.SysRegionRepository;
import com.example.vvpdomain.entity.SysRegion;
import com.example.vvpweb.systemmanagement.nodemodel.model.RegionInfoResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zph
 * @description 省市区管理
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/system_management/sysregion_model")
@CrossOrigin
@Api(value = "系统管理-省市区", tags = {"系统管理-省市区"})
public class SysRegionController {
    /**
     * 1-省、自治区、直辖市 2-地级市、地区、自治州、盟 3-市辖区、县级市、县
     */
    private static final String PROVINCE_LEVEL = "1";

    private static final String AREA_LEVEL = "2";

    private static final String COUNTY_LEVEL = "3";

    @Autowired
    private SysRegionRepository sysRegionRepository;

    /**
     * 省、自治区、直辖市 字典表
     */
    @UserLoginToken
    @ApiOperation("省、自治区、直辖市基本信息")
    @RequestMapping(value = "regionProvinces", method = {RequestMethod.POST})
    public ResponseResult<List<RegionInfoResponse>> regionProvincesList() {
        List<RegionInfoResponse> result = new ArrayList<>();

        List<SysRegion> allByRegionLevel = sysRegionRepository.findAllByRegionLevel(PROVINCE_LEVEL);

        allByRegionLevel.forEach(e -> {
            RegionInfoResponse regionInfoResponse = new RegionInfoResponse();
            regionInfoResponse.setRegionId(e.getRegionId());
            regionInfoResponse.setRegionCode(e.getRegionCode());
            regionInfoResponse.setRegionName(e.getRegionName());
            regionInfoResponse.setRegionShortName(e.getRegionShortName());

            result.add(regionInfoResponse);
        });

        return ResponseResult.success(result);
    }


    /**
     * 地级市、地区、自治州、盟 字典表
     */
    @UserLoginToken
    @ApiOperation("地级市、地区、自治州、盟基本信息")
    @RequestMapping(value = "regionCity", method = {RequestMethod.POST})
    public ResponseResult<List<RegionInfoResponse>> regionArea(@RequestParam("provinceRegionId") String provinceRegionId) {
        List<RegionInfoResponse> result = new ArrayList<>();

        List<SysRegion> allByRegionLevel = sysRegionRepository.findAllByRegionLevelAndRegionParentId(AREA_LEVEL, provinceRegionId);

        allByRegionLevel.forEach(e -> {
            RegionInfoResponse regionInfoResponse = new RegionInfoResponse();
            regionInfoResponse.setRegionId(e.getRegionId());
            regionInfoResponse.setRegionCode(e.getRegionCode());
            regionInfoResponse.setRegionName(e.getRegionName());
            regionInfoResponse.setRegionShortName(e.getRegionShortName());

            result.add(regionInfoResponse);
        });

        return ResponseResult.success(result);
    }


    /**
     * 地级市、地区、自治州、盟 字典表
     */
    @UserLoginToken
    @ApiOperation("市辖区、县级市、县基本信息")
    @RequestMapping(value = "regionCounty", method = {RequestMethod.POST})
    public ResponseResult<List<RegionInfoResponse>> regionCounty(@RequestParam("cityRegionId") String citryRegionId) {
        List<RegionInfoResponse> result = new ArrayList<>();

        List<SysRegion> allByRegionLevel = sysRegionRepository.findAllByRegionLevelAndRegionParentId(COUNTY_LEVEL, citryRegionId);

        allByRegionLevel.forEach(e -> {
            RegionInfoResponse regionInfoResponse = new RegionInfoResponse();
            regionInfoResponse.setRegionId(e.getRegionId());
            regionInfoResponse.setRegionCode(e.getRegionCode());
            regionInfoResponse.setRegionName(e.getRegionName());
            regionInfoResponse.setRegionShortName(e.getRegionShortName());

            result.add(regionInfoResponse);
        });

        return ResponseResult.success(result);
    }


}