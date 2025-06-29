package com.example.vvpweb.externalapi;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpservice.externalapi.model.AvailVppDataDTO;
import com.example.vvpservice.externalapi.service.EPApiService;
import com.example.vvpweb.externalapi.model.QueryDCCommand;
import com.example.vvpweb.externalapi.model.QueryVppDataCommand;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@EnableAsync
@Slf4j
@RestController
@RequestMapping("/algorithm")
@CrossOrigin
@Api(value = "算法接口", tags = {"算法接口"})
public class AlgorithmApiController {
    @Resource
    EPApiService epApiService;

    @ApiOperation("查询日历表")
    @UserLoginToken
    @RequestMapping(value = "queryDemandCalendar", method = {RequestMethod.POST})
    public ResponseResult queryDemandCalendar(@RequestBody QueryDCCommand command) {
        return ResponseResult.success(epApiService.findByDateBetween(command.getSDate(), command.getEDate()));
    }
    @ApiOperation("查询可获取的业务聚合数据列表")
    @UserLoginToken
    @RequestMapping(value = "getAvailVppData", method = {RequestMethod.POST})
    public ResponseResult getAvailVppData(@RequestBody QueryVppDataCommand command) {
        List<AvailVppDataDTO> data = epApiService.getAvailVppData(command.getNodeIds());
        return ResponseResult.success(data);
    }
    @ApiOperation("查询业务聚合数据")
    @UserLoginToken
    @RequestMapping(value = "getVppDataList", method = {RequestMethod.POST})
    public ResponseResult getVppDataList(@RequestBody QueryVppDataCommand command) {
        return ResponseResult.success(epApiService.getVppDataList(command.getDataId(), command.getSTime(), command.getETime()));
    }

    @ApiOperation("查询业务聚合数据目前的")
    @UserLoginToken
    @RequestMapping(value = "getVppDataListNow", method = {RequestMethod.POST})
    public ResponseResult getVppDataListNow(@RequestBody QueryVppDataCommand command) {
        return ResponseResult.success(epApiService.getVppDataListNow(command.getDataId(), command.getSTime(), command.getETime()));
    }

}
