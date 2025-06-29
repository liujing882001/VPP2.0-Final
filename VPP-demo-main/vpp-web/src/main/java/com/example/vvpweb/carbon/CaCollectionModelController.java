package com.example.vvpweb.carbon;

import com.alibaba.fastjson.JSON;
import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.PinyinUtils;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.CaCollectionModelRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.SysDictTypeRepository;
import com.example.vvpdomain.entity.CaCollectionModel;
import com.example.vvpdomain.entity.Node;
import com.example.vvpdomain.entity.SysDictNode;
import com.example.vvpdomain.entity.SysDictType;
import com.example.vvpservice.carbon.service.CaCollectionModelService;
import com.example.vvpservice.iotdata.model.IotDeviceView;
import com.example.vvpservice.iotdata.service.IIotDeviceService;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpweb.systemmanagement.nodemodel.model.NodeResponse;
import com.example.vvpweb.systemmanagement.nodemodel.model.SystemNameResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author maoyating
 * @description 碳资产-碳排放因子
 * @date 2022-08-09
 */
@RestController
@RequestMapping("/carbon/collectionModel")
@CrossOrigin
@Api(value = "碳资产管理-采集模型", tags = {"碳资产管理-采集模型"})
public class CaCollectionModelController {

    @Autowired
    private CaCollectionModelRepository caCollectionModelRepository;
    @Autowired
    private SysDictTypeRepository systemRepository;
    @Autowired
    private CaCollectionModelService caCollectionModelService;
    @Autowired
    private IIotDeviceService iIotDeviceService;
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private IUserService userService;

    @ApiOperation("获取采集模型列表")
    @UserLoginToken
    @RequestMapping(value = "/getCollectionModelList", method = {RequestMethod.POST})
    public ResponseResult<PageModel> getEmissionFactorList(@RequestParam("nodeId") String nodeId, @RequestParam("scopeType") Integer scopeType) {
        List<CaCollectionModel> caCollectionModels = caCollectionModelService.getCaCollectionModelList(nodeId, scopeType);
        PageModel pageModel = new PageModel();
        pageModel.setContent(caCollectionModels);
        pageModel.setTotalPages(1);
        pageModel.setTotalElements(caCollectionModels.size());
        pageModel.setNumber(1);
        return ResponseResult.success(pageModel);
    }

    @ApiOperation("编辑采集模型")
    @UserLoginToken
    @RequestMapping(value = "/editCollectionModel", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult editEmissionFactor(@RequestBody @Valid CaCollectionModel caCollectionModel) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");
        try {
            if (StringUtils.isBlank(caCollectionModel.getCollectionModelId())) {
                caCollectionModel.setCollectionModelId(UUID.randomUUID().toString());
                caCollectionModel.setCreatedTime(new Date());
            }
            caCollectionModel.setUpdateTime(new Date());
            caCollectionModel.setSStatus(1);
            caCollectionModelRepository.save(caCollectionModel);
            return ResponseResult.success(caCollectionModel);
        } catch (Exception e) {
            return ResponseResult.error("修改信息异常 +" + e.getMessage());
        }
    }

    @ApiOperation("删除采集模型")
    @UserLoginToken
    @RequestMapping(value = "/removeCollectionModel", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult removeEmissionFactor(@RequestParam("collectionModelId") String collectionModelId) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request1 = attributes.getRequest();
        String userId = request1.getHeader("authorizationCode");
        try {
            CaCollectionModel caCollectionModel = caCollectionModelRepository.findById(collectionModelId).orElse(null);
            if (caCollectionModel == null) {
                return ResponseResult.error("采集模型不存在,请刷新页面。");
            }
            caCollectionModel.setSStatus(0);
            caCollectionModelRepository.save(caCollectionModel);
            return ResponseResult.success("删除成功！");
        } catch (Exception e) {
            return ResponseResult.error("删除信息异常 +" + e.getMessage());
        }
    }

    /**
     * 获取节点列表
     */
    @ApiOperation("获取节点列表")
    @UserLoginToken
    @RequestMapping(value = "nodeList", method = {RequestMethod.POST})
    public ResponseResult<List<NodeResponse>> nodeList() {
        List<NodeResponse> list = new ArrayList<>();

//        List<Node> nodes = nodeRepository.findAllByNodeIdIn(userService.getAllowNodeIds());
        List<Node> nodes = nodeRepository.findAll();
        if (nodes != null && nodes.size() > 0) {

            List<SysDictType> allSystemTypes = systemRepository.findAll();

            nodes.stream().forEach(node -> {

                NodeResponse nodeRep = new NodeResponse();
                nodeRep.setId(node.getNodeId());
                nodeRep.setName(node.getNodeName());
                SysDictNode nodeType = node.getNodeType();
                if (nodeType != null) {
                    nodeRep.setNodeTypeName(nodeType.getNodeTypeName());
                }
                nodeRep.setLatitude(node.getLatitude());
                nodeRep.setLongitude(node.getLongitude());
                nodeRep.setNodePostType(node.getNodePostType());
                List<String> sysIds = JSON.parseArray(node.getSystemIds(), String.class);
                if (sysIds != null & sysIds.size() > 0) {
                    List<SysDictType> systemTypes = new ArrayList<>();
                    allSystemTypes.forEach(l -> {
                        if (sysIds.contains(l.getSystemId())) {
                            systemTypes.add(l);
                        }
                    });

                    if (systemTypes != null && systemTypes.size() > 0) {
                        nodeRep.setSystemNames(systemTypes.stream().map(u -> u.getSystemName()).collect(Collectors.joining(",")));
                    }
                }
                list.add(nodeRep);
            });
        }

        if (list != null && list.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(list, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getNodeTypeName().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getNodeTypeName().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(list);
    }

    /**
     * 指定节点下系统列表
     */
    @ApiOperation("根据节点获取系统列表")
    @UserLoginToken
    @RequestMapping(value = "nodeSystemList", method = {RequestMethod.POST})
    public ResponseResult<List<SystemNameResponse>> nodeSystemList(@RequestParam("nodeId") String nodeId) {
        List<SystemNameResponse> list = new ArrayList<>();

        Node node = nodeRepository.findById(nodeId).orElse(null);
        if (node != null) {
            List<String> sysIds = JSON.parseArray(node.getSystemIds(), String.class);
            if (sysIds != null & sysIds.size() > 0) {
                List<SysDictType> systemTypes = systemRepository.findAllBySystemIdIn(sysIds);
                if (systemTypes != null && systemTypes.size() > 0) {
                    systemTypes.stream().forEach(p -> {
                        SystemNameResponse response = new SystemNameResponse();
                        response.setId(p.getSystemId());
                        response.setSystemName(p.getSystemName());
                        list.add(response);
                    });
                }
            }
        }
        if (list != null && list.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(list, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getSystemName().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getSystemName().substring(0, 1)).toLowerCase()));

        }
        return ResponseResult.success(list);
    }

    /**
     * 获取所有的系统
     */
    @ApiOperation("根据节点和系统获取设备列表及点位")
    @UserLoginToken
    @RequestMapping(value = "getAllDevice", method = {RequestMethod.POST})
    public ResponseResult<List<IotDeviceView>> getAllDevice(@RequestParam("nodeId") String nodeId, @RequestParam("systemId") String systemId) {
        try {
            List<IotDeviceView> iotDeviceViews = iIotDeviceService.devicesOfNodeAndSystem(nodeId, systemId);
            return ResponseResult.success(iotDeviceViews);
        } catch (Exception ex) {
            return ResponseResult.error("获取所有的系统失败!");
        }
    }
}