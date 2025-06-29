package com.example.vvpweb.systemmanagement.systemparamer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.Enum.SysParamEnum;
import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.RequestHeaderContext;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.SysDictNodeRepository;
import com.example.vvpdomain.SysParamRepository;
import com.example.vvpdomain.UserRepository;
import com.example.vvpdomain.entity.SysDictNode;
import com.example.vvpdomain.entity.SysParam;
import com.example.vvpdomain.entity.User;
import com.example.vvpweb.systemmanagement.systemparamer.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Consumer;

/**
 * 系统参数
 */
@RestController
@CrossOrigin
@RequestMapping("/system_management/systemParam")
@Api(value = "系统管理-系统参数", tags = {"系统管理-系统参数"})
public class SysParamController {

    @Resource
    private SysParamRepository sysParamRepository;

    @Resource
    private SysDictNodeRepository sysDictNodeRepository;

    @Autowired
    private UserRepository userRepository;

    private static boolean checkSysParam(SysParam sysParam, SysParamEnum paramEnum) {
        if (sysParam != null) {
            return sysParam.getSysParamKeyDesc().equals(paramEnum.getDesc());
        }
        return true;
    }

    @ApiOperation("系统参数列表")
    @UserLoginToken
    @RequestMapping(value = "modelParameterListPageable", method = {RequestMethod.POST})
    public ResponseResult<PageModel> modelParameterListPageable(@RequestParam("number") int number,
                                                                @RequestParam("pageSize") int pageSize) {


        Pageable pageable = PageRequest.of(number - 1, pageSize);
        Specification<SysParam> spec = new Specification<SysParam>() {
            @Override
            public Predicate toPredicate(Root<SysParam> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();//使用集合可以应对多字段查询的情况
                predicates.add(cb.equal(root.get("status"), String.valueOf(0)));
                query.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

                //排序示例(先根据model_key排序)
                query.orderBy(cb.asc(root.get("id")));
                return query.getRestriction();//以and的形式拼接查询条件，也可以用.or()
            }
        };

        Page<SysParam> datas = sysParamRepository.findAll(spec, pageable);

        List<SystemParamModel> systemParamModelList = new ArrayList<>();

        datas.getContent().stream().forEach(param -> {
            SystemParamModel systemParamModel = new SystemParamModel();

            systemParamModel.setId(param.getId());
            systemParamModel.setKey(param.getSysParamKey());
            systemParamModel.setParamName(param.getSysParamName());
            systemParamModel.setParamContent(param.getSysParamContent());
            systemParamModelList.add(systemParamModel);
        });

        PageModel pageModel = new PageModel();
        pageModel.setPageSize(pageSize);
        pageModel.setContent(systemParamModelList);
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);
        return ResponseResult.success(pageModel);
    }

    @ApiOperation("获取资源概览节点类型排序")
    @UserLoginToken
    @PostMapping("/findResourceOverviewNodeTypeOrderSysParam")
    public ResponseResult<ResourceOverviewNodeTypeOrderModel> findResourceOverviewNodeTypeOrderSysParam(@RequestParam("id") String id) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.ResourceOverviewNodeTypeOrder.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.ResourceOverviewNodeTypeOrder)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            ResourceOverviewNodeTypeOrderModel model = new ResourceOverviewNodeTypeOrderModel();
            model.setNodeTypeIds(JSON.parseArray(sysParam.getSysParamValue(), String.class));
            model.setParamName(sysParam.getSysParamName());
            model.setId(sysParam.getId());
            return ResponseResult.success(model);
        }
        return ResponseResult.success();
    }

    @ApiOperation("编辑资源概览节点类型排序")
    @UserLoginToken
    @PostMapping("/updateResourceOverviewNodeTypeOrderSysParam")
    @Transactional
    public ResponseResult updateResourceOverviewNodeTypeOrderSysParam(@RequestBody  ResourceOverviewNodeTypeOrderModel model) {

        if (model == null || model.getNodeTypeIds() == null || model.getNodeTypeIds().size() < 4) {
            return ResponseResult.error("选中资源概览节点类型有且只有四个，请修改!");
        }
        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.ResourceOverviewNodeTypeOrder.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.ResourceOverviewNodeTypeOrder)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            sysParam.setSysParamName(model.getParamName());
            sysParam.setSysParamValue(JSON.toJSONString(model.getNodeTypeIds()));

            StringBuilder sb = new StringBuilder();

            model.getNodeTypeIds().stream().forEach(p -> {
                SysDictNode sysDictNode = sysDictNodeRepository.findById(p).orElse(null);
                if (sysDictNode != null) {
                    sb.append(sysDictNode.getNodeTypeName() + ",");
                }
            });
            String str = sb.toString();
            sysParam.setSysParamContent(str.substring(0, str.length() - 1));
            sysParam.setSysParamKey(SysParamEnum.ResourceOverviewNodeTypeOrder.getId());
            sysParam.setSysParamKeyDesc(SysParamEnum.ResourceOverviewNodeTypeOrder.getDesc());
            sysParamRepository.save(sysParam);
        }
        return ResponseResult.success();
    }

    @ApiOperation("编辑IOT平台")
    @UserLoginToken
    @PostMapping("/updateIOTParamSysParam")
    @Transactional
    public ResponseResult updateIOTParamSysParam(@RequestBody IOTParamModel model) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.IOTParam.getId())).orElse(null);
        if (sysParam != null) {

            if (!checkSysParam(sysParam, SysParamEnum.IOTParam)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            sysParam.setSysParamName(model.getParamName());
            TreeMap<String, Object> map = new TreeMap<>();
            map.put("iotAddress", model.getIotAddress());
            map.put("iotUserName", model.getIotUserName());
            map.put("iotUserPwd", model.getIotUserPwd());

            sysParam.setSysParamValue(JSON.toJSONString(map));
            sysParam.setSysParamContent("IOT平台地址:" + model.getIotAddress()
                    + ",账户名:" + model.getIotUserName()
                    + ",密码:" + model.getIotUserPwd());
            sysParam.setSysParamKey(SysParamEnum.IOTParam.getId());
            sysParam.setSysParamKeyDesc(SysParamEnum.IOTParam.getDesc());
            sysParamRepository.save(sysParam);
        }
        return ResponseResult.success();
    }

    @ApiOperation("获取IOT平台")
    @UserLoginToken
    @PostMapping("/findIOTParamSysParam")
    public ResponseResult<IOTParamModel> findIOTParamSysParam(@RequestParam("id") String id) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.IOTParam.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.IOTParam)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }
            IOTParamModel model = new IOTParamModel();
            JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
            model.setId(sysParam.getId());
            model.setParamName(sysParam.getSysParamName());
            model.setIotAddress(obj.get("iotAddress").toString());
            model.setIotUserName(obj.get("iotUserName").toString());
            model.setIotUserPwd(obj.get("iotUserPwd").toString());
            return ResponseResult.success(model);
        }
        return ResponseResult.error("获取IOT平台失败！");
    }

    @ApiOperation("获取节点标准坪效设定")
    @UserLoginToken
    @PostMapping("/findSalesPerSquareMeterSysParam")
    public ResponseResult<SalesPerSquareMeterModel> findSalesPerSquareMeterSysParam(@RequestParam("id") String id) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.SalesPerSquareMeter.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.SalesPerSquareMeter)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            SalesPerSquareMeterModel model = new SalesPerSquareMeterModel();
            JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
            model.setId(sysParam.getId());
            model.setParamName(sysParam.getSysParamName());
            model.setCommercialComplex(Double.parseDouble(obj.get("commercialComplex").toString()));
            model.setGovernmentOfficeGreaterThan20000(Double.parseDouble(obj.get("governmentOfficeGreaterThan20000").toString()));
            model.setGovernmentOfficeLessThan20000(Double.parseDouble(obj.get("governmentOfficeLessThan20000").toString()));
            return ResponseResult.success(model);
        }
        return ResponseResult.error("获取节点标准坪效设定失败！");
    }

    @ApiOperation("编辑节点标准坪效设定")
    @UserLoginToken
    @PostMapping("/updateSalesPerSquareMeterSysParam")
    @Transactional
    public ResponseResult updateSalesPerSquareMeterSysParam(@RequestBody SalesPerSquareMeterModel model) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.SalesPerSquareMeter.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.SalesPerSquareMeter)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            sysParam.setSysParamName(model.getParamName());

            TreeMap<String, Object> map = new TreeMap<>();
            map.put("commercialComplex", model.getCommercialComplex());
            map.put("governmentOfficeGreaterThan20000", model.getGovernmentOfficeGreaterThan20000());
            map.put("governmentOfficeLessThan20000", model.getGovernmentOfficeLessThan20000());

            sysParam.setSysParamValue(JSON.toJSONString(map));
            sysParam.setSysParamContent("商业综合体:" + model.getCommercialComplex()
                    + ",政府办公≥20000m²:" + model.getGovernmentOfficeGreaterThan20000()
                    + ",政府办公<20000m²:" + model.getGovernmentOfficeLessThan20000());
            sysParam.setSysParamKey(SysParamEnum.SalesPerSquareMeter.getId());
            sysParam.setSysParamKeyDesc(SysParamEnum.SalesPerSquareMeter.getDesc());
            sysParamRepository.save(sysParam);
        }
        return ResponseResult.success();
    }
    @ApiOperation("是否对接第三方能源平台")
    @UserLoginToken
    @PostMapping("/thirdPartyEnergyPlat")
    public ResponseResult<Map<String,Object>> thirdPartyEnergyPlat(@RequestParam("id") String id) throws JsonProcessingException {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.ThirdPartyEnergyPlat.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.ThirdPartyEnergyPlat)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }
            Map<String,Object> map = new HashMap<>();
            map.put("id", sysParam.getId());
            map.put("name", sysParam.getSysParamName());
            map.put("param", sysParam.getSysParamValue() == null ? null : new ObjectMapper().readValue(sysParam.getSysParamValue(), new TypeReference<List<ThirdPartyEnergyPlatParam>>() {}));
            map.put("status", sysParam.getSysParamContent());
            return ResponseResult.success(map);
        }
        return ResponseResult.error("获取第三方智慧能源平台信息失败！");
    }
    @ApiOperation("编辑是否对接第三方能源平台")
    @UserLoginToken
    @PostMapping("/updateThirdPartyEnergyPlat")
    @Transactional
    public ResponseResult updateThirdPartyEnergyPlat(@RequestBody SystemThirdPartyEnergyPlat model) throws JsonProcessingException {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.ThirdPartyEnergyPlat.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.ThirdPartyEnergyPlat)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }
            String param = new ObjectMapper().writeValueAsString(model.getParam());
            sysParam.setSysParamValue(param);
            sysParam.setSysParamContent(model.getStatus());
            sysParamRepository.save(sysParam);
        }
        return ResponseResult.success();
    }
    @ApiOperation("获取第三方智慧能源平台")
    @UserLoginToken
    @PostMapping("/findSmartEnergySysParam")
    public ResponseResult<SmartEnergyModel> findSmartEnergySysParam(@RequestParam("id") String id) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.SmartEnergy.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.SmartEnergy)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            SmartEnergyModel model = new SmartEnergyModel();
            model.setParamName(sysParam.getSysParamName());
            model.setId(sysParam.getId());
            model.setAddress(sysParam.getSysParamValue());
            return ResponseResult.success(model);
        }
        return ResponseResult.error("获取第三方智慧能源平台信息失败！");
    }

    @ApiOperation("编辑第三方智慧能源平台")
    @UserLoginToken
    @PostMapping("/updateSmartEnergySysParam")
    @Transactional
    public ResponseResult updateSmartEnergySysParam(@RequestBody SmartEnergyModel model) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.SmartEnergy.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.SmartEnergy)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }
            sysParam.setSysParamName(model.getParamName());
            sysParam.setSysParamValue(model.getAddress());
            sysParam.setSysParamContent(model.getAddress());
            sysParam.setSysParamKey(SysParamEnum.SmartEnergy.getId());
            sysParam.setSysParamKeyDesc(SysParamEnum.SmartEnergy.getDesc());
            sysParamRepository.save(sysParam);
        }
        return ResponseResult.success();
    }

    @ApiOperation("获取VPP需求响应Agent地址")
    @UserLoginToken
    @PostMapping("/findDemandResponseSysParam")
    public ResponseResult<DemandResponseModel> findDemandResponseSysParam(@RequestParam("id") String id) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.DemandResponse.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.DemandResponse)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            DemandResponseModel model = new DemandResponseModel();
            model.setAddress(sysParam.getSysParamValue());
            model.setId(sysParam.getId());
            model.setParamName(sysParam.getSysParamName());
            return ResponseResult.success(model);
        }
        return ResponseResult.error("获取电网省公司需求响应地址失败！");
    }

    @ApiOperation("编辑VPP需求响应Agent地址")
    @UserLoginToken
    @PostMapping("/updateDemandResponseSysParam")
    @Transactional
    public ResponseResult updateDemandResponseSysParam(@RequestBody DemandResponseModel model) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.DemandResponse.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.DemandResponse)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }
            sysParam.setSysParamName(model.getParamName());
            sysParam.setSysParamValue(model.getAddress());
            sysParam.setSysParamContent(model.getAddress());
            sysParam.setSysParamKey(SysParamEnum.DemandResponse.getId());
            sysParam.setSysParamKeyDesc(SysParamEnum.DemandResponse.getDesc());
            sysParamRepository.save(sysParam);
        }
        return ResponseResult.success();
    }

    @ApiOperation("获取顶峰能力参数配置")
    @UserLoginToken
    @PostMapping("/findPeakCapacityParamCfgSysParam")
    public ResponseResult<PeakCapacityParamCfgModel> findPeakCapacityParamCfgSysParam(@RequestParam("id") String id) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.PeakCapacityParamCfg.getId())).orElse(null);
        if (sysParam != null) {

            if (!checkSysParam(sysParam, SysParamEnum.PeakCapacityParamCfg)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            PeakCapacityParamCfgModel model = new PeakCapacityParamCfgModel();
            model.setId(sysParam.getId());
            model.setParamName(sysParam.getSysParamName());
            JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
            if (obj != null) {
                if (obj.get("pvRatedPower") != null) {
                    model.setPvRatedPower(Double.parseDouble(obj.get("pvRatedPower").toString()));
                }
                if (obj.get("storageEnergyRatedPower") != null) {
                    model.setStorageEnergyRatedPower(Double.parseDouble(obj.get("storageEnergyRatedPower").toString()));
                }
                if (obj.get("cdzRatedPower") != null) {
                    model.setCdzRatedPower(Double.parseDouble(obj.get("cdzRatedPower").toString()));
                }
                if (obj.get("ktRatedPower") != null) {
                    model.setKtRatedPower(Double.parseDouble(obj.get("ktRatedPower").toString()));
                }

            }

            return ResponseResult.success(model);
        }
        return ResponseResult.error("获取顶峰能力参数失败！");
    }

    @ApiOperation("编辑顶峰能力参数配置")
    @UserLoginToken
    @PostMapping("/updatePeakCapacityParamCfgSysParam")
    @Transactional
    public ResponseResult updatePeakCapacityParamCfgSysParam(@RequestBody  PeakCapacityParamCfgModel model) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.PeakCapacityParamCfg.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.PeakCapacityParamCfg)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            TreeMap<String, Object> map = new TreeMap<>();
            map.put("pvRatedPower", model.getPvRatedPower());
            map.put("storageEnergyRatedPower", model.getStorageEnergyRatedPower());
            map.put("cdzRatedPower", model.getCdzRatedPower());
            map.put("ktRatedPower", model.getKtRatedPower());
            sysParam.setSysParamValue(JSON.toJSONString(map));
            sysParam.setSysParamName(model.getParamName());
            sysParam.setSysParamKey(SysParamEnum.PeakCapacityParamCfg.getId());
            sysParam.setSysParamKeyDesc(SysParamEnum.PeakCapacityParamCfg.getDesc());
            sysParamRepository.save(sysParam);
        }
        return ResponseResult.success();
    }

    @ApiOperation("获取logo参数配置")
    @UserLoginToken
    @PostMapping("/findLogoSysParam")
    public ResponseResult<LogoModel> findLogoSysParam(@RequestParam("id") String id) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.LoGoCfg.getId())).orElse(null);
        if (sysParam != null) {

            if (!checkSysParam(sysParam, SysParamEnum.LoGoCfg)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            LogoModel model = new LogoModel();
            model.setId(sysParam.getId());
            model.setParamName(sysParam.getSysParamName());
            JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
            if (obj != null) {
                if (obj.get("sysLogo") != null) {
                    model.setSysLogo(obj.get("sysLogo").toString());
                }

                if (obj.get("mainLogo") != null) {
                    model.setMainLogo(obj.get("mainLogo").toString());
                }
            }

            return ResponseResult.success(model);
        }
        return ResponseResult.error("获取logo参数失败！");
    }

    @ApiOperation("v2获取logo参数配置V2")
    @UserLoginToken
    @PostMapping("/v2/findLogoSysParam")
    public ResponseResult<LogoModel> findLogoSysParamV2(@RequestParam("id") String id) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.LoGoCfg.getId())).orElse(null);
        if (sysParam != null) {

            if (!checkSysParam(sysParam, SysParamEnum.LoGoCfg)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            LogoModel model = new LogoModel();
            model.setId(sysParam.getId());
            model.setParamName(sysParam.getSysParamName());
            JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
            if (obj != null) {
                if (obj.get("sysLogo") != null) {
                    model.setSysLogo(obj.get("sysLogo").toString());
                }

                if (obj.get("platformName") != null) {
                    model.setPlatformName(obj.get("platformName").toString());
                }
            }

            return ResponseResult.success(model);
        }
        return ResponseResult.error("获取logo参数失败！");
    }


    @ApiOperation("编辑logo参数配置")
    @UserLoginToken
    @PostMapping("/updateLogoSysParam")
    @Transactional
    public ResponseResult updateLogoSysParam(@RequestBody LogoModel model) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.LoGoCfg.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.LoGoCfg)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            TreeMap<String, Object> map = new TreeMap<>();
            map.put("sysLogo", model.getSysLogo());
            map.put("mainLogo", model.getMainLogo());

            sysParam.setSysParamValue(JSON.toJSONString(map));
            sysParam.setSysParamName(model.getParamName());
            sysParam.setSysParamKey(SysParamEnum.LoGoCfg.getId());
            sysParam.setSysParamKeyDesc(SysParamEnum.LoGoCfg.getDesc());
            sysParamRepository.save(sysParam);
        }
        return ResponseResult.success();
    }


    @ApiOperation("编辑logo参数配置v2")
    @UserLoginToken
    @PostMapping("/v2/updateLogoSysParam")
    @Transactional
    public ResponseResult updateLogoSysParamV2(@RequestBody LogoModel model) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.LoGoCfg.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.LoGoCfg)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            TreeMap<String, Object> map = new TreeMap<>();
            map.put("sysLogo", model.getSysLogo());
            map.put("platformName", model.getPlatformName());

            sysParam.setSysParamValue(JSON.toJSONString(map));
            sysParam.setSysParamName(model.getParamName());
            sysParam.setSysParamKey(SysParamEnum.LoGoCfg.getId());
            sysParam.setSysParamKeyDesc(SysParamEnum.LoGoCfg.getDesc());
            sysParamRepository.save(sysParam);
        }
        return ResponseResult.success();
    }

    @ApiOperation("得到系统LOGO V2")
    @GetMapping("/v2/sysLogoSysParam")
    public ResponseResult sysLogoSysParamV2() {

        SysParam sysParam = sysParamRepository.findSysParamBySysParamKey(SysParamEnum.LoGoCfg.getId());
        if (sysParam != null) {

            JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
            if (obj != null) {
                if (obj.get("sysLogo") != null) {
                    return ResponseResult.success(obj.get("sysLogo").toString());
                }

            }
        }
        return ResponseResult.error("未配置系统LOGO");

    }

    @ApiOperation("得到平台名称 V2")
    @GetMapping("/v2/platformNameSysParam")
    public ResponseResult platformNameSysParam() {

        SysParam sysParam = sysParamRepository.findSysParamBySysParamKey(SysParamEnum.LoGoCfg.getId());
        if (sysParam != null) {

            JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
            if (obj != null) {
                if (obj.get("platformName") != null) {
                    return ResponseResult.success(obj.get("platformName").toString());
                }

            }
        }
        return ResponseResult.error("未配置平台名称");
    }

    @Value("${os.foot}")
    private String osFoot;

    @ApiOperation("得到首页公司信息 V2")
    @GetMapping("/v2/footInfo")
    public ResponseResult footInfo() {
        return ResponseResult.success(osFoot);
    }



    @ApiOperation("得到系统LOGO")
    @GetMapping("/sysLogoSysParam")
    public ResponseResult sysLogoSysParam() {

        SysParam sysParam = sysParamRepository.findSysParamBySysParamKey(SysParamEnum.LoGoCfg.getId());
        if (sysParam != null) {

            JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
            if (obj != null) {
                if (obj.get("mainLogo") != null) {
                    return ResponseResult.success(obj.get("mainLogo").toString());
                }

            }
        }
        return ResponseResult.error("未配置系统LOGO");

    }

    @ApiOperation("首页插画LOGO")
    @GetMapping("/mainLogoSysParam")
    public ResponseResult mainLogoSysParam() {

        SysParam sysParam = sysParamRepository.findSysParamBySysParamKey(SysParamEnum.LoGoCfg.getId());
        if (sysParam != null) {

            JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
            if (obj != null) {
                if (obj.get("sysLogo") != null) {
                    return ResponseResult.success(obj.get("sysLogo").toString());
                }

            }

        }
        return ResponseResult.error("未配置首页插画LOGO");
    }





    @ApiOperation("获取基线及预测算法配置")
    @UserLoginToken
    @PostMapping("/findBaseLineForecastParam")
    public ResponseResult<BaseLineForecastCfgModel> findBaseLineForecastParam(@RequestParam("id") String id) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.BaseLineForecastCfg.getId())).orElse(null);
        if (sysParam != null) {

            if (!checkSysParam(sysParam, SysParamEnum.BaseLineForecastCfg)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            BaseLineForecastCfgModel model = new BaseLineForecastCfgModel();
            model.setId(sysParam.getId());
            model.setParamName(sysParam.getSysParamName());
            JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
            if (obj != null) {
                if (obj.get("baseLineGetMethod") != null) {
                    model.setBaseLineGetMethod(obj.get("baseLineGetMethod").toString());
                }

                if (obj.get("loadForecastGetMethod") != null) {
                    model.setLoadForecastGetMethod(obj.get("loadForecastGetMethod").toString());
                }

                if (obj.get("pvForecastGetMethod") != null) {
                    model.setPvForecastGetMethod(obj.get("pvForecastGetMethod").toString());
                }
            }

            return ResponseResult.success(model);
        }
        return ResponseResult.error("获取logo参数失败！");
    }

    @ApiOperation("编辑基线及预测算法配置")
    @UserLoginToken
    @PostMapping("/updateBaseLineForecastSysParam")
    @Transactional
    public ResponseResult updateBaseLineForecastSysParam(@RequestBody  BaseLineForecastCfgModel model) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.BaseLineForecastCfg.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.BaseLineForecastCfg)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            TreeMap<String, Object> map = new TreeMap<>();
            map.put("baseLineGetMethod", model.getBaseLineGetMethod());
            map.put("loadForecastGetMethod", model.getLoadForecastGetMethod());
            map.put("pvForecastGetMethod", model.getPvForecastGetMethod());

            sysParam.setSysParamValue(JSON.toJSONString(map));
            sysParam.setSysParamName(model.getParamName());
            sysParam.setSysParamKey(SysParamEnum.BaseLineForecastCfg.getId());
            sysParam.setSysParamKeyDesc(SysParamEnum.BaseLineForecastCfg.getDesc());
            sysParamRepository.save(sysParam);
        }
        return ResponseResult.success();
    }

    @ApiOperation("基线及预测列表")
    @UserLoginToken
    @PostMapping("/baseLineForecastDataList")
    public ResponseResult<List<BaseLineForecastData>> baseLineForecastDataList() {
        List<BaseLineForecastData> baseLineForecastData = new ArrayList<>();

        BaseLineForecastData el = new BaseLineForecastData();
        el.setName("商汤");
        el.setId(el.getName().hashCode() + "");

        BaseLineForecastData el2 = new BaseLineForecastData();
        el2.setName("VPP");
        el2.setId(el2.getName().hashCode() + "");

        baseLineForecastData.add(el);
        baseLineForecastData.add(el2);

        return ResponseResult.success(baseLineForecastData);

    }




    @ApiOperation("各地电网省公司需求平台列表")
    @UserLoginToken
    @PostMapping("/demandResponsePlatFormDatalist")
    public ResponseResult<List<DemandResponsePlatFormData>> demandResponsePlatFormDatalist() {
        List<DemandResponsePlatFormData> responsePlatFormData = new ArrayList<>();
        List<DemandResponsePlatFormEnum> collect = Arrays.asList(DemandResponsePlatFormEnum.values());

        collect.forEach(new Consumer<DemandResponsePlatFormEnum>() {
            @Override
            public void accept(DemandResponsePlatFormEnum e) {
                DemandResponsePlatFormData response = new DemandResponsePlatFormData();
                response.setId(e.getId());
                response.setName(e.getDesc());
                responsePlatFormData.add(response);
            }
        });
        return ResponseResult.success(responsePlatFormData);

    }

    @ApiOperation("获取各地电网省公司需求平台配置")
    @UserLoginToken
    @PostMapping("/findDemandResponsePlatFormParam")
    public ResponseResult<DemandResponsePlatFormParamModel> findDemandResponsePlatFormParam(@RequestParam("id") String id) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.DemandResponsePlatFormCfg.getId())).orElse(null);
        if (sysParam != null) {

            if (!checkSysParam(sysParam, SysParamEnum.DemandResponsePlatFormCfg)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            DemandResponsePlatFormParamModel model = new DemandResponsePlatFormParamModel();
            model.setId(sysParam.getId());
            model.setParamName(sysParam.getSysParamName());
            model.setValue(sysParam.getSysParamValue());

            return ResponseResult.success(model);
        }
        return ResponseResult.error("获取logo参数失败！");
    }

    @ApiOperation("编辑各地电网省公司需求平台配置")
    @UserLoginToken
    @PostMapping("/updateDemandResponsePlatFormParam")
    @Transactional
    public ResponseResult updateDemandResponsePlatFormParam(@RequestParam("id") String id,
                                                            @RequestParam("value") String value) {
//        String userId = RequestHeaderContext.getInstance().getUserId();
        //修改useid的电网类型
        String powerGridStr = value.substring(0,2);
        int powerGrid;
        if (powerGridStr.equals("国网")) {
            powerGrid = 1;
        } else {
            powerGrid = 2;
        }
        userRepository.setPowerGrid(powerGrid);
        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.DemandResponsePlatFormCfg.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.DemandResponsePlatFormCfg)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }
            sysParam.setSysParamValue(value);
            sysParam.setSysParamContent(value);
            sysParam.setSysParamKey(SysParamEnum.DemandResponsePlatFormCfg.getId());
            sysParam.setSysParamKeyDesc(SysParamEnum.DemandResponsePlatFormCfg.getDesc());
            sysParamRepository.save(sysParam);
        }
        return ResponseResult.success();
    }

    @ApiOperation("自动需求响应价格配置")
    @UserLoginToken
    @PostMapping("/findDemandResponsePrice")
    public ResponseResult<DemandResponsePriceModel> findDemandResponsePrice(@RequestParam("id") String id) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.DemandResponsePriceCfg.getId())).orElse(null);
        if (sysParam != null) {

            if (!checkSysParam(sysParam, SysParamEnum.DemandResponsePriceCfg)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }

            DemandResponsePriceModel model = new DemandResponsePriceModel();
            model.setId(sysParam.getId());
            model.setParamName(sysParam.getSysParamName());
            model.setValue(sysParam.getSysParamValue());

            return ResponseResult.success(model);
        }
        return ResponseResult.error("获取logo参数失败！");
    }


    @ApiOperation("自动需求响应价格编辑")
    @UserLoginToken
    @PostMapping("/updateDemandResponsePrice")
    @Transactional
    public ResponseResult updateDemandResponsePrice(@RequestParam("id") String id,
                                                            @RequestParam("value") String value) {

        SysParam sysParam = sysParamRepository.findById(String.valueOf(SysParamEnum.DemandResponsePriceCfg.getId())).orElse(null);
        if (sysParam != null) {
            if (!checkSysParam(sysParam, SysParamEnum.DemandResponsePriceCfg)) {
                return ResponseResult.error("参数方法请求不匹配，请修改!");
            }
            sysParam.setSysParamValue(value);
            sysParam.setSysParamKey(SysParamEnum.DemandResponsePriceCfg.getId());
            sysParam.setSysParamKeyDesc(SysParamEnum.DemandResponsePriceCfg.getDesc());
            sysParam.setSysParamContent(value+"元、kwh");
            sysParamRepository.save(sysParam);
        }
        return ResponseResult.success();
    }


}
