package com.example.vvpweb.systemmanagement.energymodel;

import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.SysDictDataRepository;
import com.example.vvpdomain.entity.SysDictData;
import com.example.vvpweb.systemmanagement.energymodel.model.LoadTypeResponse;
import com.example.vvpweb.systemmanagement.energymodel.model.ModelParameterTypeResponse;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * 模型参数
 */
@RestController
@CrossOrigin
@RequestMapping("/system_management/energy_model/model_parameter")
@Api(value = "系统管理-能源模型-模型参数", tags = {"系统管理-能源模型-模型参数"})
public class ModelParameterController {

    @Autowired
    private SysDictDataRepository sysDictDataRepository;

    /**
     * 参数类型
     */
    @UserLoginToken
    @RequestMapping(value = "modelParameterTypeList", method = {RequestMethod.POST})
    public ResponseResult<List<ModelParameterTypeResponse>> modelParameterTypeList(@RequestParam(value = "systemId") String systemId) {

        List<SysDictData> sysConfigs = null;
        switch (systemId) {
            case "nengyuanzongbiao":
                sysConfigs = sysDictDataRepository.findAllByModelKeyTypeAndModelKey(1, "metering_device");
                break;
            default:
                sysConfigs = sysDictDataRepository.findAllByModelKeyType(1);
                break;
        }
        if (sysConfigs != null && sysConfigs.size() > 0) {

            List<ModelParameterTypeResponse> sTypes = new ArrayList<>();
            for (SysDictData sysConfig : sysConfigs) {
                ModelParameterTypeResponse sType = new ModelParameterTypeResponse();
                sType.setConfig_key(sysConfig.getModelKey());
                sType.setConfig_name(sysConfig.getModelName());
                sTypes.add(sType);
            }
            return ResponseResult.success(sTypes);
        }

        return ResponseResult.error("获取参数类型列表失败。");
    }

    /**
     * 获取符合性质列表
     */
    @UserLoginToken
    @RequestMapping(value = "loadTypeList", method = {RequestMethod.POST})
    public List<LoadTypeResponse> loadTypeList(@RequestParam(value = "configKey") String configKey){

        List<LoadTypeResponse> sTypes = new ArrayList<>();

        switch (configKey) {
            case "metering_device":
                LoadTypeResponse sType = new LoadTypeResponse();
                sType.setLoad_type("-");
                sType.setLoad_type_name("-");
                sType.setLoad_properties("-");
                sType.setLoad_properties_name("-");
                sTypes.add(sType);
                break;
            case "other":
            default:
                LoadTypeResponse airConditioning = new LoadTypeResponse();
                airConditioning.setLoad_type("air_conditioning");
                airConditioning.setLoad_type_name("空调");
                airConditioning.setLoad_properties("adjustable_load");
                airConditioning.setLoad_properties_name("可调节负荷");
                sTypes.add(airConditioning);

                LoadTypeResponse chargingPiles = new LoadTypeResponse();
                chargingPiles.setLoad_type("charging_piles");
                chargingPiles.setLoad_type_name("充电桩");
                chargingPiles.setLoad_properties("transferable_load");
                chargingPiles.setLoad_properties_name("可转移负荷");
                sTypes.add(chargingPiles);

                LoadTypeResponse lighting = new LoadTypeResponse();
                lighting.setLoad_type("lighting");
                lighting.setLoad_type_name("照明");
                lighting.setLoad_properties("interruptible_load");
                lighting.setLoad_properties_name("可中断负荷");
                sTypes.add(lighting);

                LoadTypeResponse others = new LoadTypeResponse();
                others.setLoad_type("others");
                others.setLoad_type_name("其他");
                others.setLoad_properties("other_loads");
                others.setLoad_properties_name("其他负荷");
                sTypes.add(others);
                break;
        }
        return sTypes;
    }


    /**
     * 系统参数列表
     */
    @UserLoginToken
    @RequestMapping(value = "modelParameterListPageable", method = {RequestMethod.POST})
    public ResponseResult<PageModel> modelParameterListPageable(@RequestParam("number") int number,
                                                                @RequestParam("pageSize") int pageSize) {


        Pageable pageable = PageRequest.of(number - 1, pageSize);
        Specification<SysDictData> spec = new Specification<SysDictData>() {
            @Override
            public Predicate toPredicate(Root<SysDictData> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder cb) {
                //排序示例(先根据model_key排序)
                query.orderBy(cb.asc(root.get("modelKeyType")),cb.asc(root.get("createdTime")));

                return query.getRestriction();//以and的形式拼接查询条件，也可以用.or()
            }
        };

        Page<SysDictData> datas = sysDictDataRepository.findAll(spec, pageable);

        PageModel pageModel = new PageModel();
        pageModel.setPageSize(pageSize);
        pageModel.setContent(datas.getContent());
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);
    }


}
