package com.example.vvpweb.device;

import com.example.vvpcommom.*;
import com.example.vvpdomain.entity.Device;
import com.example.vvpdomain.entity.DevicePoint;
import com.example.vvpservice.device.DeviceService;
import com.example.vvpweb.device.model.DevicePageByNLCommand;
import com.example.vvpweb.device.model.DevicePageCommand;
import com.example.vvpweb.device.model.DeviceVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.Collator;
import java.util.*;

@EnableAsync
@Slf4j
@RestController
@RequestMapping("/device")
@CrossOrigin
@Api(value = "设备接口", tags = {"设备接口"})
public class DeviceController {

    @Resource
    DeviceService deviceService;

    @ApiOperation("根据节点id查设备分页")
    @UserLoginToken
    @RequestMapping(value = "devicePage", method = {RequestMethod.POST})
    public ResponseResult<PageModel> devicePage(@RequestBody DevicePageCommand command) {

        Page<Device> datas = deviceService.devicePageByNodeId(command.getNodeId(), command.getPageNum(), command.getPageSize());
        List<DeviceVO> list = new ArrayList<>();
        datas.getContent().forEach(e -> {
            DeviceVO view = new DeviceVO();
            view.setDeviceId(e.getDeviceId());
            view.setDeviceSn(e.getDeviceSn());
            view.setDeviceBrand(e.getDeviceBrand());
            view.setDeviceLabel(e.getDeviceLabel());
            view.setDeviceModel(e.getDeviceModel());
            view.setDeviceName(e.getDeviceName());
            view.setNodeName(e.getNode().getNodeName());
            view.setSystemName(e.getSystemType().getSystemName());
            view.setConfigKey(e.getConfigKey());
            view.setSystemId(e.getSystemType().getSystemId());
            view.setDeviceRatedPower(e.getDeviceRatedPower());
            view.setOnline(e.getOnline());
            view.setLoadType(FieldConvertUtil.convertLoadType(e.getLoadType()));
            view.setLoadProperties(FieldConvertUtil.convertLoadProperties(e.getLoadProperties()));

            view.setMecOnline(e.getMecOnline());
            view.setMecName(e.getMecName());

            List<DevicePoint> devicePointList = e.getDevicePointList();
            devicePointList.forEach(l -> {
                DeviceVO.DevicePointVO pv = new DeviceVO.DevicePointVO();
                pv.setPointId(l.getPointId());
                pv.setPointSn(l.getPointSn());
                pv.setPointDesc(l.getPointDesc());
                pv.setPointName(l.getPointName());
                pv.setOnline(view.getOnline());
                view.getPointViewList().add(pv);
            });

            list.add(view);
        });

        if (list != null && list.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(list, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getNodeName().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getNodeName().substring(0, 1)).toLowerCase()));

        }

        PageModel pageModel = new PageModel();
        pageModel.setPageSize(command.getPageSize());
        pageModel.setContent(list);
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);

    }
    @ApiOperation("根据节点id列表查设备分页")
    @UserLoginToken
    @RequestMapping(value = "devicePageByNL", method = {RequestMethod.POST})
    public ResponseResult<PageModel> devicePageByNL(@RequestBody DevicePageByNLCommand command) {

        Page<Device> datas = deviceService.devicePageByNodeIds(command.getNodeIds(), command.getPageNum(), command.getPageSize());
        List<DeviceVO> list = new ArrayList<>();
        datas.getContent().forEach(e -> {
            DeviceVO view = new DeviceVO();
            view.setDeviceId(e.getDeviceId());
            view.setDeviceSn(e.getDeviceSn());
            view.setDeviceBrand(e.getDeviceBrand());
            view.setDeviceLabel(e.getDeviceLabel());
            view.setDeviceModel(e.getDeviceModel());
            view.setDeviceName(e.getDeviceName());
            view.setNodeName(e.getNode().getNodeName());
            view.setSystemName(e.getSystemType().getSystemName());
            view.setConfigKey(e.getConfigKey());
            view.setSystemId(e.getSystemType().getSystemId());
            view.setDeviceRatedPower(e.getDeviceRatedPower());
            view.setOnline(e.getOnline());
            view.setLoadType(FieldConvertUtil.convertLoadType(e.getLoadType()));
            view.setLoadProperties(FieldConvertUtil.convertLoadProperties(e.getLoadProperties()));

            view.setMecOnline(e.getMecOnline());
            view.setMecName(e.getMecName());

            List<DevicePoint> devicePointList = e.getDevicePointList();
            devicePointList.forEach(l -> {
                DeviceVO.DevicePointVO pv = new DeviceVO.DevicePointVO();
                pv.setPointId(l.getPointId());
                pv.setPointSn(l.getPointSn());
                pv.setPointDesc(l.getPointDesc());
                pv.setPointName(l.getPointName());
                pv.setOnline(view.getOnline());
                view.getPointViewList().add(pv);
            });

            list.add(view);
        });

        if (list != null && list.size() > 0) {
            Comparator comparator = Collator.getInstance(Locale.CHINA);
            Collections.sort(list, (p1, p2) -> comparator.compare(
                    PinyinUtils.converterToFirstSpell(p1.getNodeName().substring(0, 1)).toLowerCase(),
                    PinyinUtils.converterToFirstSpell(p2.getNodeName().substring(0, 1)).toLowerCase()));

        }

        PageModel pageModel = new PageModel();
        pageModel.setPageSize(command.getPageSize());
        pageModel.setContent(list);
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);

    }
}
