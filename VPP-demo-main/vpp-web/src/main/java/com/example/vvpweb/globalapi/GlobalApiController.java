package com.example.vvpweb.globalapi;

import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpservice.globalapi.service.GlobalApiService;
import com.example.vvpweb.globalapi.model.ListProjSubCommand;
import com.example.vvpweb.globalapi.model.NodeTreeCommand;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@EnableAsync
@Slf4j
@RestController
@RequestMapping("/global")
@CrossOrigin
@Api(value = "全局接口", tags = {"全局接口"})
public class GlobalApiController {
    @Resource
    private GlobalApiService globalApiService;

    @ApiOperation("查用户权限下节点列表")
    @UserLoginToken
    @RequestMapping(value = "useNodes", method = {RequestMethod.POST})
    public ResponseResult useNodes() {
        return ResponseResult.success(globalApiService.useNodes());
    }
    @ApiOperation("查区域和节点类型下的项目节点")
    @UserLoginToken
    @RequestMapping(value = "cityATypeProNodes", method = {RequestMethod.POST})
    public ResponseResult cityATypeProNodes() {
        return ResponseResult.success(globalApiService.cityATypeProNodes());
    }

    @ApiOperation("查用户权限下节点树及节点设备数量")
    @UserLoginToken
    @RequestMapping(value = "useNodeDevTree", method = {RequestMethod.POST})
    public ResponseResult findTreeByRole() {
        return ResponseResult.success(globalApiService.findTreeByRole());
    }
    @ApiOperation("查询某个节点的树关系")
    @UserLoginToken
    @RequestMapping(value = "nodeTree", method = {RequestMethod.POST})
    public ResponseResult findTreeByStationId(@RequestBody NodeTreeCommand command) {
        return ResponseResult.success(globalApiService.findTreeByStationId(command.getStationId()));
    }
    @ApiOperation("查询某个节点及其下属的树关系")
    @UserLoginToken
    @RequestMapping(value = "nodeSubTree", method = {RequestMethod.POST})
    public ResponseResult findSubTreeByStationId(@RequestBody NodeTreeCommand command) {
        return ResponseResult.success(globalApiService.findSubTreeByStationId(command.getStationId()));
    }
    @ApiOperation("查询用户权限下运营中项目节点数量接口-权限")
    @UserLoginToken
    @RequestMapping(value = "listActSNForUser", method = {RequestMethod.POST})
    public ResponseResult listActSNForUser() {
        return ResponseResult.success(globalApiService.listActSNForUser().size());
    }
    @ApiOperation("查询运营中项目节点列表接口")
    @UserLoginToken
    @RequestMapping(value = "listActiveSN", method = {RequestMethod.POST})
    public ResponseResult listActiveSN() {
        return ResponseResult.success(globalApiService.listActiveSN());
    }

    @ApiOperation("查项目节点及其下面系统节点接口（系统节点不分类）")
    @UserLoginToken
    @RequestMapping(value = "listProjSub", method = {RequestMethod.POST})
    public ResponseResult listProjAndSub(@RequestBody ListProjSubCommand command) {
        return ResponseResult.success(globalApiService.listProjAndSub(command.getQuery(), command.getKeyword()));
    }
    @ApiOperation("查项目节点及其下面系统节点接口（系统节点分类）")
    @UserLoginToken
    @RequestMapping(value = "listProjAndSubCategory", method = {RequestMethod.POST})
    public ResponseResult listProjAndSubCategory(@RequestBody ListProjSubCommand command) {
        return ResponseResult.success(globalApiService.listProjAndSubCategory(command.getQuery(), command.getKeyword()));
    }
//    @ApiOperation("根据节点id列表查电价列表")
//    @UserLoginToken
//    @RequestMapping(value = "getPriceListByNodeIds", method = {RequestMethod.POST})
//    public ResponseResult getPriceListByNodeIds(@RequestBody ListProjSubCommand command) {
//        return ResponseResult.success(globalApiService.listProjAndSubCategory(command.getQuery(), command.getKeyword()));
//    }
}
