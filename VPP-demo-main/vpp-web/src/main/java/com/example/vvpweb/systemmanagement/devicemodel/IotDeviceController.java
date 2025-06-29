package com.example.vvpweb.systemmanagement.devicemodel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.*;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.Device;
import com.example.vvpdomain.entity.DevicePoint;
import com.example.vvpdomain.entity.ScheduleStrategyDevice;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;
import com.example.vvpservice.iotdata.model.IotDevice;
import com.example.vvpservice.iotdata.model.IotDeviceView;
import com.example.vvpservice.iotdata.service.IIotDeviceService;
import com.example.vvpservice.tree.model.StructTreeResponse;
import com.example.vvpservice.tree.service.ITreeLabelService;
import com.example.vvpservice.usernode.service.IPageableService;
import com.example.vvpweb.BaseExcelController;
import com.example.vvpweb.electricitybill.model.ElectricityBillStorageEnergyResponse;
import io.swagger.annotations.Api;
import net.dreamlu.mica.core.utils.StringPool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.Collator;
import java.util.*;

/**
 * 设备模型
 */
@RestController
@RequestMapping("/system_management/device_model")
@CrossOrigin
@Api(value = "系统管理-设备模型", tags = {"系统管理-设备模型"})
public class IotDeviceController extends BaseExcelController {

    @Autowired
    private IIotDeviceService iIotDeviceService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DevicePointRepository devicePointRepository;
    @Resource
    private ScheduleStrategyDeviceRepository scheduleStrategyDeviceRepository;

    @Autowired
    private ITreeLabelService iTreeLabelService;

    @Autowired
    private IPageableService pageableService;

    @Resource
    private IExcelOutPutService iExcelOutPutService;


    @Autowired
    private IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository;

    @Autowired
    private IotTsKvRepository iotTsKvRepository;


    @UserLoginToken
    @RequestMapping(value = "typeDeviceShortView", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> typeDeviceShortView() {
        return ResponseResult.success(iTreeLabelService.typeDeviceShortView());
    }

    @UserLoginToken
    @RequestMapping(value = "areaDeviceShortView", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> areaDeviceShortView() {
        return ResponseResult.success(iTreeLabelService.areaDeviceShortView());
    }

    /**
     * 网关设备模型excel导入V2
     *  设备类型configKey：      计量设备metering_device ，  非计量设备 other
     *  负荷类型loadType：           -                   ，   空调（air_conditioning），充电桩（charging_piles），照明（lighting），其它（others）
     *  符合性质loadProperties：     -                   ，   可调节负荷(adjustable_load)，可转移负荷(transferable_load)，可中断负荷(interruptible_load)，其它负荷(other_loads)
     */
    @UserLoginToken
    @RequestMapping(value = "v2/deviceExcelUpload", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult deviceExcelUpload(@RequestParam("configKey") String configKey,
                                            @RequestParam("nodeId") String nodeId,
                                            @RequestParam("systemId") String systemId,
                                            @RequestParam("loadType") String loadType,
                                            @RequestParam("loadProperties") String loadProperties,
                                            @RequestParam("device_rated_power") double device_rated_power,
                                            @RequestParam("file") MultipartFile file) {
        if (StringUtils.isEmpty(loadType)) {
            return deviceExcelUpload(configKey, nodeId, systemId, device_rated_power, file);
        }

        if (StringUtils.isEmpty(nodeId)) {
            return ResponseResult.error("归属节点编号为空，请修改!");
        }
        if (StringUtils.isEmpty(systemId)) {
            return ResponseResult.error("系统编号为空，请修改!");
        }
        if (StringUtils.isEmpty(configKey)) {
            return ResponseResult.error("导入设备类型编号为空，请修改!");
        }
        if (StringUtils.isEmpty(loadType)) {
            return ResponseResult.error("导入设备时，不能为空，请修改!");
        }
        if (StringUtils.isEmpty(loadProperties)) {
            return ResponseResult.error("导入设备时，负荷性质不能为空，请修改!");
        }
        if (device_rated_power < 0) {
            return ResponseResult.error("额定功率小于0，请修改!");
        }
        if (file.isEmpty() || file.getSize() == 0) {
            return ResponseResult.error("导入文件为空，请修改!");
        }

        if (StringUtils.isNotEmpty(configKey)
                && StringUtils.isNotEmpty(nodeId)
                && StringUtils.isNotEmpty(systemId)
                && file != null) {
            try {

                // 获取文件名
                String fileName = file.getOriginalFilename();
                // 获取文件后缀名，截取.后面的类容
                String suffix = fileName.substring(fileName.lastIndexOf(StringPool.DOT));
                if (!ExcelTypeEnum.XLSX.getValue().equals(suffix)) {
                    ResponseResult.error("设备导入文件格式必须为 xlsx！");
                }
                iIotDeviceService.deviceImportByExcel(file.getInputStream(), configKey, nodeId, systemId, device_rated_power, loadType, loadProperties);
                return ResponseResult.success();
            } catch (Exception e) {
                return ResponseResult.error("设备导入失败" + e.getMessage());
            }
        }
        return ResponseResult.error("设备导入失败");
    }

    /**
     * 网关设备模型excel导入
     */
    @UserLoginToken
    @RequestMapping(value = "deviceExcelUpload", method = {RequestMethod.POST})
    @Transactional
    public ResponseResult deviceExcelUpload(@RequestParam("configKey") String configKey,
                                            @RequestParam("nodeId") String nodeId,
                                            @RequestParam("systemId") String systemId,
                                            @RequestParam("device_rated_power") double device_rated_power,
                                            @RequestParam("file") MultipartFile file) {


        if (StringUtils.isEmpty(nodeId)) {
            return ResponseResult.error("归属节点编号为空，请修改!");
        }
        if (StringUtils.isEmpty(systemId)) {
            return ResponseResult.error("系统编号为空，请修改!");
        }
        if (StringUtils.isEmpty(configKey)) {
            return ResponseResult.error("导入设备类型编号为空，请修改!");
        }
        if (device_rated_power < 0) {
            return ResponseResult.error("额定功率小于0，请修改!");
        }
        if (file.isEmpty() || file.getSize() == 0) {
            return ResponseResult.error("导入文件为空，请修改!");
        }

        if (StringUtils.isNotEmpty(configKey)
                && StringUtils.isNotEmpty(nodeId)
                && StringUtils.isNotEmpty(systemId)
                && file != null) {
            try {

                // 获取文件名
                String fileName = file.getOriginalFilename();
                // 获取文件后缀名，截取.后面的类容
                String suffix = fileName.substring(fileName.lastIndexOf(StringPool.DOT));
                if (!ExcelTypeEnum.XLSX.getValue().equals(suffix)) {
                    ResponseResult.error("设备导入文件格式必须为 xlsx！");
                }
                iIotDeviceService.deviceImportByExcel(file.getInputStream(), configKey, nodeId, systemId, device_rated_power,null,null);
                return ResponseResult.success();
            } catch (Exception e) {
                return ResponseResult.error("设备导入失败" + e.getMessage());
            }
        }
        return ResponseResult.error("设备导入失败");
    }

    @PassToken
    @GetMapping("/download/deviceTemplate")
    public void deviceTemplate(HttpServletResponse response) throws IOException {
        try {

            InputStream inputStream = new ClassPathResource("/excel/设备导入模板.xlsx").getInputStream();
            String fileName = URLEncoder.encode("设备模版", "UTF-8").replaceAll("\\+", "%20");

            execTemplate(response, inputStream, new HashMap<>(), iExcelOutPutService, fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 节点下删除某设备
     */
    @Transactional
    @UserLoginToken
    @RequestMapping(value = "deviceDelete", method = {RequestMethod.POST})
    public ResponseResult deviceDelete(@RequestParam("nodeId") String nodeId,
                                       @RequestParam("systemId") String systemId,
                                       @RequestParam("deviceId") String deviceId) {

        if (StringUtils.isEmpty(nodeId)) {
            return ResponseResult.error("节点编号为空，请修改!");
        }
        if (StringUtils.isEmpty(systemId)) {
            return ResponseResult.error("系统编号为空，请修改!");
        }
        if (StringUtils.isEmpty(deviceId)) {
            return ResponseResult.error("设备编号为空，请修改!");
        }

        if (StringUtils.isNotEmpty(nodeId)
                && StringUtils.isNotEmpty(systemId)
                && StringUtils.isNotEmpty(deviceId)) {
            try {
                Device byDeviceId = deviceRepository.findByDeviceId(deviceId);
                if (byDeviceId == null) {
                    return ResponseResult.error("该设备不存在！");
                }

                List<ScheduleStrategyDevice> scheduleStrategyDevices = scheduleStrategyDeviceRepository.findAllByDeviceId(deviceId);
                if (scheduleStrategyDevices != null && scheduleStrategyDevices.size() > 0) {
                    return ResponseResult.error("该设备被策略引用,删除设备失败！");
                }


                iotTsKvMeteringDevice96Repository.deleteByDeviceSn(byDeviceId.getDeviceSn());
                iotTsKvRepository.deleteByDeviceSn(byDeviceId.getDeviceSn());
                devicePointRepository.deleteAllByDevice_DeviceId(deviceId);
                deviceRepository.deleteByDeviceIdAndNode_NodeIdAndSystemType_SystemId(deviceId, nodeId, systemId);
                return ResponseResult.success();
            } catch (Exception ex) {
                return ResponseResult.error("该设备被引用,删除设备失败！");
            }
        }
        return ResponseResult.error("节点下删除设备失败！");
    }


    /**
     * 节点列表
     */
    @UserLoginToken
    @RequestMapping(value = "deviceListPageable", method = {RequestMethod.POST})
    public ResponseResult<PageModel> deviceListPageable(@RequestParam("number") int number,
                                                        @RequestParam("pageSize") int pageSize,
                                                        @RequestParam("nodeId") String nodeId,
                                                        @RequestParam(value = "systemId", required = false) String systemId) {

        if (StringUtils.isEmpty(nodeId)) {
            return ResponseResult.error("节点编号为空，请修改!");
        }

        Page<Device> datas = pageableService.getDeviceByNodeIdAndSystemId(nodeId, systemId, number, pageSize);
        List<IotDeviceView> list = new ArrayList<>();
        datas.getContent().forEach(e -> {
            IotDeviceView view = new IotDeviceView();
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
            view.setDeviceRatedPower(e.getDeviceRatedPower());//add by maoyating
            view.setOnline(e.getOnline());
            view.setLoadType(FieldConvertUtil.convertLoadType(e.getLoadType()));
            view.setLoadProperties(FieldConvertUtil.convertLoadProperties(e.getLoadProperties()));

            view.setMecOnline(e.getMecOnline());
            view.setMecName(e.getMecName());

            List<DevicePoint> devicePointList = e.getDevicePointList();
            devicePointList.forEach(l -> {
                IotDeviceView.IotDevicePointView pv = new IotDeviceView.IotDevicePointView();
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
        pageModel.setPageSize(pageSize);
        pageModel.setContent(list);
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);

    }


    /**
     * 节点列表
     */
    @UserLoginToken
    @RequestMapping(value = "deviceLikeNameListPageable", method = {RequestMethod.POST})
    public ResponseResult<PageModel> deviceLikeNameListPageable(@RequestParam("number") int number,
                                                                @RequestParam("pageSize") int pageSize,
                                                                @RequestParam("deviceName") String deviceName) {
        Page<Device> datas = pageableService.getDeviceLikeDeviceName(deviceName, number, pageSize);
        List<IotDeviceView> list = new ArrayList<>();
        datas.getContent().forEach(e -> {
            IotDeviceView view = new IotDeviceView();
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
            view.setDeviceRatedPower(e.getDeviceRatedPower());//add by maoyating
            view.setOnline(e.getOnline());
            view.setLoadType(FieldConvertUtil.convertLoadType(e.getLoadType()));
            view.setLoadProperties(FieldConvertUtil.convertLoadProperties(e.getLoadProperties()));

            view.setMecOnline(e.getMecOnline());
            view.setMecName(e.getMecName());

            List<DevicePoint> devicePointList = e.getDevicePointList();
            devicePointList.forEach(l -> {
                IotDeviceView.IotDevicePointView pv = new IotDeviceView.IotDevicePointView();
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
        pageModel.setPageSize(pageSize);
        pageModel.setContent(list);
        pageModel.setTotalPages(datas.getTotalPages());
        pageModel.setTotalElements((int) datas.getTotalElements());
        pageModel.setNumber(datas.getNumber() + 1);

        return ResponseResult.success(pageModel);
    }
}
