package com.example.vvpweb.systemmanagement.systemmodel;

import com.example.vvpcommom.*;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.SysDictTypeRepository;
import com.example.vvpdomain.entity.Node;
import com.example.vvpdomain.entity.SysDictType;
import com.example.vvpweb.systemmanagement.systemmodel.model.SystemModel;
import com.example.vvpweb.systemmanagement.systemmodel.model.SystemModelResponse;
import com.example.vvpweb.systemmanagement.systemmodel.model.UpdateSystemModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zph
 * @description 系统模型
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/system_management/system_model")
@CrossOrigin
@Api(value = "系统管理-系统模型类", tags = {"系统管理-系统模型"})
public class SystemController {

    @Resource
    private SysDictTypeRepository systemRepository;
    @Resource
    private NodeRepository nodeRepository;
    @Value("${os.type}")
    private String osType;
    /**
     * 获取所有的系统
     */
    @ApiOperation("获取所有的系统")
    @UserLoginToken
    @RequestMapping(value = "systemList", method = {RequestMethod.POST})
    public ResponseResult<List<SystemModelResponse>> systemList() {
        try {
            List<SystemModelResponse> list = new ArrayList<>();

            List<SysDictType> systemList = systemRepository.findAll();
            if ("loadType".equals(osType)) {

                // 移除nodePostType为 pv，storageEnergy
                systemList = systemList.stream()
                        .filter(node -> !"guangfu".equals(node.getSystemId())
                                && !"chuneng".equals(node.getSystemId()))
                        .collect(Collectors.toList());
            }
            if (systemList != null && systemList.size() > 0) {
                systemList = systemList
                        .stream()
                        .sorted(Comparator.comparing(p -> p.getCreatedTime()))
                        .collect(Collectors.toList());
                systemList.stream().forEach(p -> {
                    SystemModelResponse systemModelResponse = new SystemModelResponse();
                    systemModelResponse.setSystemKey(p.getSystemId());
                    systemModelResponse.setSystemName(p.getSystemName());
                    systemModelResponse.setConfigType(p.getConfigType());
                    list.add(systemModelResponse);
                });
            }
            return ResponseResult.success(list);
        } catch (Exception ex) {
            return ResponseResult.error("获取所有的系统失败!");
        }
    }


    /**
     * 分页获取所有的系统
     */
    @ApiOperation("分页获取所有的系统")
    @UserLoginToken
    @RequestMapping(value = "queryAllSystemListPageable", method = {RequestMethod.POST})
    public ResponseResult<PageModel> queryAllSystemListPageable(@RequestParam("number") int number,
                                                                @RequestParam("pageSize") int pageSize) {
        try {
            if (pageSize <= 0 || number < 1) {
                return ResponseResult.error("参数范围有误!");
            }
            List<SystemModelResponse> list = new ArrayList<>();

            Pageable pageable = PageRequest.of(number - 1, pageSize);
            Page<SysDictType> datas = systemRepository.findAll(pageable);
            if (datas != null && datas.getContent() != null && datas.getContent().size() > 0) {

                List<SysDictType> systemList = datas.getContent();

                if (systemList != null && systemList.size() > 0) {
                    systemList = systemList.stream()
                            .sorted(Comparator.comparing(p -> p.getCreatedTime()))
                            .collect(Collectors.toList());
                    systemList.stream().forEach(p -> {
                        SystemModelResponse systemModelResponse = new SystemModelResponse();
                        systemModelResponse.setSystemKey(p.getSystemId());
                        systemModelResponse.setSystemName(p.getSystemName());
                        systemModelResponse.setConfigType(p.getConfigType());
                        list.add(systemModelResponse);
                    });
                }
            }

            PageModel pageModel = new PageModel();
            pageModel.setPageSize(pageSize);
            pageModel.setContent(list);
            pageModel.setTotalPages(datas.getTotalPages());
            pageModel.setTotalElements((int) datas.getTotalElements());
            pageModel.setNumber(datas.getNumber() + 1);

            return ResponseResult.success(pageModel);
        } catch (Exception ex) {
            return ResponseResult.error("获取所有的系统失败!");
        }
    }


    /**
     * 获取所有的系统
     */
    @ApiOperation("获取系统列表分页")
    @UserLoginToken
    @RequestMapping(value = "systemListByNodeTypeKey", method = {RequestMethod.POST})
    public ResponseResult<List<SystemModelResponse>> systemListByNodeTypeKey(@RequestParam("nodeTypeKey") String nodeTypeKey) {
        try {
            return systemList();
        } catch (Exception ex) {
            return ResponseResult.error("获取所有的系统失败!");
        }
    }


    /**
     * 获取光伏系统
     */
    @ApiOperation("获取光伏系统")
    @UserLoginToken
    @RequestMapping(value = "systemPVList", method = {RequestMethod.POST})
    public ResponseResult<List<SystemModelResponse>> systemPVList() {
        try {
            List<SystemModelResponse> list = new ArrayList<>();

            List<SysDictType> systemList = systemRepository.findAllBySystemIdIn(Arrays.asList("nengyuanzongbiao"));
            if (systemList != null && systemList.size() > 0) {
                systemList = systemList
                        .stream()
                        .sorted(Comparator.comparing(p -> p.getCreatedTime()))
                        .collect(Collectors.toList());

                systemList.stream().forEach(p -> {
                    SystemModelResponse systemModelResponse = new SystemModelResponse();
                    systemModelResponse.setSystemKey(p.getSystemId());
                    systemModelResponse.setSystemName(p.getSystemName());
                    systemModelResponse.setConfigType(p.getConfigType());
                    list.add(systemModelResponse);
                });
            }
            return ResponseResult.success(list);
        } catch (Exception ex) {
            return ResponseResult.error("获取所有的系统失败!");
        }
    }


    /**
     * 获取储能系统
     */
    @ApiOperation("获取储能系统")
    @UserLoginToken
    @RequestMapping(value = "systemStorageEnergyList", method = {RequestMethod.POST})
    public ResponseResult<List<SystemModelResponse>> systemStorageEnergyList() {
        try {
            List<SystemModelResponse> list = new ArrayList<>();

            List<SysDictType> systemList = systemRepository.findAllBySystemIdIn(Arrays.asList("nengyuanzongbiao"));
            if (systemList != null && systemList.size() > 0) {
                systemList = systemList
                        .stream()
                        .sorted(Comparator.comparing(p -> p.getCreatedTime()))
                        .collect(Collectors.toList());

                systemList.stream().forEach(p -> {
                    SystemModelResponse systemModelResponse = new SystemModelResponse();
                    systemModelResponse.setSystemKey(p.getSystemId());
                    systemModelResponse.setSystemName(p.getSystemName());
                    systemModelResponse.setConfigType(p.getConfigType());
                    list.add(systemModelResponse);
                });
            }
            return ResponseResult.success(list);
        } catch (Exception ex) {
            return ResponseResult.error("获取所有的系统失败!");
        }
    }

    /**
     * 根据系统名称获取所有的系统
     */
    @ApiOperation("根据系统名称获取所有的系统")
    @UserLoginToken
    @RequestMapping(value = "systemListBySystemName", method = {RequestMethod.POST})
    public ResponseResult<List<SystemModelResponse>> systemListBySystemName(@RequestParam("systemName") String systemName) {
        try {
            if (StringUtils.isNotEmpty(systemName)) {
                List<SystemModelResponse> list = new ArrayList<>();

                List<SysDictType> systemList = systemRepository.findAllBySystemNameContains(systemName);
                if (systemList != null && systemList.size() > 0) {

                    systemList = systemList
                            .stream()
                            .sorted(Comparator.comparing(p -> p.getCreatedTime()))
                            .collect(Collectors.toList());
                    systemList.stream().forEach(p -> {
                        SystemModelResponse systemModelResponse = new SystemModelResponse();
                        systemModelResponse.setSystemKey(p.getSystemId());
                        systemModelResponse.setSystemName(p.getSystemName());
                        systemModelResponse.setConfigType(p.getConfigType());
                        list.add(systemModelResponse);
                    });
                }
                return ResponseResult.success(list);
            }

        } catch (Exception ex) {
            return ResponseResult.error("根据系统名称获取所有的系统失败!");

        }
        return ResponseResult.error("根据系统名称获取所有的系统失败！");
    }

    /**
     * 新建系统
     */
    @ApiOperation("新建系统")
    @UserLoginToken
    @RequestMapping(value = "addSystemList", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult addSystemList(@RequestBody SystemModel systemModel) {
        try {
            if (systemModel == null) {
                return ResponseResult.error("参数异常，请检查！");
            }
            if (systemModel.getSystemName() == null || systemModel.getSystemName().size() == 0) {
                return ResponseResult.error("要添加的系统列表为空，请修改。");
            }

            if (DistinctUtil.distinctList(systemModel.getSystemName()).size() != systemModel.getSystemName().size()) {

                return ResponseResult.error("要添加的系统列表里面有重复系统名称，请修改。");
            }

            for (String sysName : systemModel.getSystemName()) {
                boolean isMatch = Pattern.matches(RegexUtils.patternName, sysName);
                if (!isMatch) {
                    return ResponseResult.error("系统名称不符合 6～12个汉字、字母或数字、_ 要求，请修改！");
                }
            }


            List<SysDictType> exitSystemList = systemRepository.findAllBySystemNameIn(systemModel.getSystemName());

            if (exitSystemList != null && exitSystemList.size() > 0) {
                return ResponseResult.error("系统列表里面已存在该系统名称，请修改。");
            }

            List<SysDictType> list = new ArrayList<>();
            systemModel.getSystemName().stream().forEach(p -> {

                String key = IdGenerator.md5Id(p);
                if (StringUtils.isNotEmpty(p)) {
                    SysDictType system = new SysDictType();
                    system.setSystemId(key);
                    system.setSystemName(p);
                    system.setConfigType("N");
                    list.add(system);
                }
            });
            systemRepository.saveAll(list);
            return ResponseResult.success();

        } catch (Exception ex) {
            return ResponseResult.error("新建系统失败!");
        }
    }

    /**
     * 修改系统
     */
    @ApiOperation("修改系统")
    @UserLoginToken
    @RequestMapping(value = "updateSystemList", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult updateSystemList(@RequestBody UpdateSystemModel sysModel) {
        try {
            if (sysModel == null) {
                return ResponseResult.error("参数异常，请检查！");
            }
            if (StringUtils.isEmpty(sysModel.getSystemKey())) {
                return ResponseResult.error("系统编号为空，请修改!");
            }
            if (StringUtils.isEmpty(sysModel.getNewSystemName())) {
                return ResponseResult.error("系统名称为空，请修改!");
            }
            boolean isMatch = Pattern.matches(RegexUtils.patternName, sysModel.getNewSystemName());
            if (!isMatch) {
                return ResponseResult.error("系统名称不符合 6～12个汉字、字母或数字、_ 要求，请修改！");
            }
            String newSystemName = sysModel.getNewSystemName();
            String systemId = sysModel.getSystemKey();

            SysDictType items = systemRepository.findById(systemId).orElse(null);
            if (items == null) {
                return ResponseResult.error("系统列表里面不存在该系统编号,请刷新页面。");
            }
            if (items.getConfigType().equals("Y")) {
                return ResponseResult.error("内置系统类型，不可修改。");
            }

            if (!newSystemName.equals(items.getSystemName())) {
                SysDictType bySystemName = systemRepository.findBySystemName(newSystemName);
                if (bySystemName != null) {
                    return ResponseResult.error("系统列表里面已存在该系统名称，请修改。");
                }
            }

            SysDictType system = new SysDictType();
            system.setSystemId(systemId);
            system.setSystemName(newSystemName);
            system.setConfigType(items.getConfigType());
            systemRepository.save(system);
            return ResponseResult.success();

        } catch (Exception ex) {
            return ResponseResult.error("修改系统失败!");

        }
    }

    /**
     * 删除系统
     */
    @ApiOperation("删除系统")
    @UserLoginToken
    @RequestMapping(value = "deleteSystem", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult deleteSystem(@RequestParam("systemKey") String systemKey) {
        try {
            if (StringUtils.isEmpty(systemKey)) {
                return ResponseResult.error("系统编号为空，请修改!");
            }
            SysDictType items = systemRepository.findById(systemKey).orElse(null);
            if (items != null && items.getConfigType().equals("Y")) {
                return ResponseResult.error("内置系统类型，不可删除。");
            }
            List<Node> nodes = nodeRepository.findAllBySystemIdsContains(systemKey);
            if (nodes != null && nodes.size() > 0) {

                StringBuilder sb = new StringBuilder();

                for (Node node : nodes) {
                    sb.append(node.getNodeName() + ",");
                }
                String str = sb.toString();
                str = str.substring(0, str.length() - 1);
                return ResponseResult.error("该系统已被节点 " + str + " 引用,删除失败！");
            }
            systemRepository.deleteById(systemKey);
            return ResponseResult.success();
        } catch (Exception ex) {
            return ResponseResult.error("该系统类型被关联使用，请删除前解除关联使用关系，删除系统失败!");
        }
    }
}