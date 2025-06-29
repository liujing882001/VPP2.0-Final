package com.example.vvpweb.iotdata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.gateway.MeteorologicalDataService;
import com.example.gateway.model.MeteorologicalRequest;
import com.example.gateway.util.JsonUtils;
import com.example.vvpcommom.Enum.AlarmSeverityEnum;
import com.example.vvpcommom.Enum.AlarmStatusEnum;
import com.example.vvpcommom.*;
import com.example.vvpdomain.*;
import com.example.vvpdomain.alarm.IotTsAlarmInformationRepository;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;
import com.example.vvpservice.iotdata.model.IotDataModel;
import com.example.vvpservice.iotdata.model.IotDevicePointDataView;
import com.example.vvpservice.usernode.service.IPageableService;
import com.example.vvpweb.BaseExcelController;
import com.example.vvpweb.iotdata.model.MecModel;
import com.example.vvpweb.iotdata.model.PointDataExport;
import com.example.vvpweb.iotdata.model.PointModel;
import com.example.vvpweb.iotdata.model.TsKvModelRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/platFrom")
@CrossOrigin
@Slf4j
@Api(value = "接收IOT平台转发网关采集数据", tags = {"接收IOT平台转发网关采集数据API"})
public class IotDataController extends BaseExcelController {
    private static final Logger logger = LoggerFactory.getLogger(IotDataController.class);
    @Resource
    IPageableService pageableService;
    @Resource
    private IotTsKvLastRepository iotTsKvLastRepository;
    @Resource
    private NodeRepository nodeRepository;
    @Resource
    private IotTsKvRepository iotTsKvRepository;
    @Resource
    private DeviceRepository deviceRepository;
    @Resource
    private DevicePointRepository devicePointRepository;
    @Resource
    private IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository;
    @Resource
    private AlarmLogRepository alarmRepository;
    @Resource
    private IExcelOutPutService iExcelOutPutService;
    @Autowired
    private IotTsAlarmInformationRepository iotTsAlarmInformationRepository;

    @RequestMapping(value = "gatewayConnection", method = {RequestMethod.POST})
    @ApiOperation("网关连接(在线，离线)数据上报")
    public ResponseResult collectGatewayConnectionData(@RequestBody String json) {
        try {
            if (StringUtils.isNotEmpty(json)) {

                MecModel model  = JSONObject.parseObject(json, MecModel.class);

                //json schema 校验
                if (model != null && model.getSs_mecId() != null) {
                    List<Device> allByMecId = deviceRepository.findAllByMecId(model.getSs_mecId());
                    if(allByMecId!=null && !allByMecId.isEmpty()){
                        deviceRepository.updateDeviceMecOnlineAndMecName(model.isActive(),model.getSs_mecId(),model.getDeviceName());
                    }

                }else {
                    logger.error("网关连接(在线，离线)数据上报，数据异常"+ JsonUtils.jsonOutputObj(model));
                }


            }
            return ResponseResult.success();
        } catch (Exception e) {
            return ResponseResult.error(e.getMessage());
        }
    }



    @RequestMapping(value = "dataTransfer", method = {RequestMethod.POST})
    @ApiOperation("网关采集数据入库")
    public ResponseResult collectIotData(@RequestBody String json) {
        try {
            if (StringUtils.isNotEmpty(json)) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss.SS");
                timeFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));

                SimpleDateFormat timeFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
                timeFormat2.setTimeZone(TimeZone.getTimeZone("GMT+8"));

                SimpleDateFormat fmt_ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
                fmt_ymdhm.setTimeZone(TimeZone.getTimeZone("GMT+8"));

                SimpleDateFormat fmt_ymd_hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                fmt_ymd_hms.setTimeZone(TimeZone.getTimeZone("GMT+8"));

                SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
                fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

                SimpleDateFormat fmt_hms = new SimpleDateFormat("HH:mm:ss");
                fmt_hms.setTimeZone(TimeZone.getTimeZone("GMT+8"));

                SimpleDateFormat fmt_ym = new SimpleDateFormat("yyyy-MM");
                fmt_ym.setTimeZone(TimeZone.getTimeZone("GMT+8"));

                Optional<Object> opt = JSONObject.parseObject(json).values().stream().findFirst();
                if (opt.isPresent()) {
                    //json schema 校验
                    IotDataModel model = JSONObject.parseObject(opt.get().toString(), IotDataModel.class);
                    if (model != null && model.getData() != null) {
                        /*  报警   新需求 移除该数据上传 改为 VPP自行判定
                        if ("ALARM".equals(model.getType())) {//保存哪个设备信息。todo

                            String deviceSn = model.getDeviceId();
                            String devicePointSn = model.getDdccode();
                            Date singleTime = Date.from(LocalDateTime.ofInstant(Instant.ofEpochMilli(model.getDatatime()), ZoneOffset.of("+8")).toInstant(ZoneOffset.ofHoursMinutes(+8, 0)));
                            String alarmAdditionalInfo = String.valueOf(model.getData().getValue());

                            Device device = deviceRepository.findByDeviceId(deviceSn);
                            if (device != null) {
                                Node node = device.getNode();
                                AlarmLog alarmLog = new AlarmLog();
                                alarmLog.setNodeId(device.getNode().getNodeId());
                                alarmLog.setNodeName(device.getNode().getNodeName());
                                alarmLog.setSystemId(device.getSystemType().getSystemId());
                                alarmLog.setSystemName(device.getSystemType().getSystemName());
                                alarmLog.setDeviceId(device.getDeviceId());
                                alarmLog.setDeviceName(device.getDeviceName());

                                alarmLog.setPointId("-");
                                alarmLog.setPointName("-");

                                DevicePoint devicePoint = devicePointRepository.findDevicePointByDevice_DeviceSnAndAndPointSn(deviceSn, devicePointSn);
                                if (devicePoint != null) {
                                    alarmLog.setPointId(devicePoint.getPointId());
                                    alarmLog.setPointName(devicePoint.getPointName());
                                }

                                alarmLog.setSeverity(AlarmSeverityEnum.UNKNOWN_ERROR.getId());
                                alarmLog.setSeverityDesc(AlarmSeverityEnum.UNKNOWN_ERROR.getDesc());

                                alarmLog.setStatus(AlarmStatusEnum.FAIL.getId());
                                alarmLog.setStatusDesc(AlarmStatusEnum.FAIL.getDesc());

                                alarmLog.setIndexStartTs(fmt_ym.parse(fmt_ym.format(singleTime)));
                                alarmLog.setStartTs(timeFormat2.parse(timeFormat2.format(singleTime)));
                                alarmLog.setAdditionalInfo(alarmAdditionalInfo);
                                alarmLog.setAlarmId(alarmLog.getDeviceId() + "_" + alarmLog.getPointId() + "_" + alarmLog.getSeverity() + "_" + alarmLog.getStatus() + "_" + timeFormat.format(singleTime));

/*
                                //设备告警的需求需要把数据同步到IotTsAlarmInformation这个表中
                                IotTsAlarmInformation iotTsAlarmInformation = new IotTsAlarmInformation();
                                BeanUtils.copyProperties(alarmLog,iotTsAlarmInformation);
                                iotTsAlarmInformation.setId(alarmLog.getDeviceId() + "_" + alarmLog.getPointId() + "_" + alarmLog.getSeverity() + "_" + alarmLog.getStatus() + "_" + timeFormat.format(singleTime));

                                iotTsAlarmInformation.setNodeId(device.getNode().getNodeId());
                                iotTsAlarmInformation.setNodeName(device.getNode().getNodeName());
                                iotTsAlarmInformation.setSystemId(device.getSystemType().getSystemId());
                                iotTsAlarmInformation.setSystemName(device.getSystemType().getSystemName());
                                iotTsAlarmInformation.setDeviceId(device.getDeviceId());
                                iotTsAlarmInformation.setDeviceName(device.getDeviceName());
                                iotTsAlarmInformation.setAlarmType("设备告警");//默认设备告警
                                iotTsAlarmInformation.setStationName("长乐产投大楼");
                                iotTsAlarmInformation.setAlarmLevel("提示");
                                iotTsAlarmInformation.setAlarmInformation("BMS-单体欠压轻度告警-告警;BMS-SOC过低1级告警-告警;;");
                                iotTsAlarmInformation.setAlarmStatus("报警中");
                                long tsTimelongIot = new Date().getTime();
                                Timestamp updateTimeIotWin = new Timestamp(tsTimelongIot);
                                iotTsAlarmInformation.setTs(updateTimeIotWin);

                                IotTsAlarmInformation alarmInformation = iotTsAlarmInformationRepository.save(iotTsAlarmInformation);
                                logger.info("alarmInformation-数据保存成功: {}", alarmInformation);


                                //原来的保存指令
                                alarmRepository.save(alarmLog);


                                //如果节点有数据上来，就改已启用
                                nodeRepository.updateNodeStatus(node.getNodeId());
                                deviceRepository.updateDeviceStatus(node.getNodeId(), device.getDeviceId());

                            }
                        }
                        */
                        if ("MSG".equals(model.getType())) {//规则配置   todo
                            DevicePoint devicePoint = devicePointRepository.findDevicePointByDevice_DeviceSnAndAndPointSn(model.getDeviceId(), model.getDdccode());
                            if (devicePoint != null
                                    && devicePoint.getDevice() != null
                                    && devicePoint.getDevice().getSystemType() != null
                                    && devicePoint.getDevice().getNode() != null) {

                                Node node = devicePoint.getDevice().getNode();
                                Device device = devicePoint.getDevice();
                                SysDictType systemType = devicePoint.getDevice().getSystemType();
                                Date singleTime = Date.from(LocalDateTime.ofInstant(Instant.ofEpochMilli(model.getDatatime()), ZoneOffset.of("+8")).toInstant(ZoneOffset.ofHoursMinutes(+8, 0)));
                                String pointValue = String.valueOf(model.getData().getValue());
                                String pointDesc = devicePoint.getPointDesc();
                                //region 写入历史表
                                IotTsKv iotTsKv = new IotTsKv();
                                iotTsKv.setId(device.getDeviceSn() + "_" + devicePoint.getPointSn() + "_" + timeFormat.format(singleTime));
                                iotTsKv.setProvinceRegionId(node.getProvinceRegionId());
                                iotTsKv.setProvinceRegionName(node.getProvinceRegionName());
                                iotTsKv.setCityRegionId(node.getCityRegionId());
                                iotTsKv.setCityRegionName(node.getCityRegionName());
                                iotTsKv.setCountyRegionId(node.getCountyRegionId());
                                iotTsKv.setCountyRegionName(node.getCountyRegionName());
                                iotTsKv.setNodeId(node.getNodeId());
                                iotTsKv.setNodeName(node.getNodeName());
                                iotTsKv.setNodePostType(node.getNodePostType());
                                iotTsKv.setLongitude(node.getLongitude());
                                iotTsKv.setLatitude(node.getLatitude());
                                iotTsKv.setSystemId(systemType.getSystemId());
                                iotTsKv.setSystemName(systemType.getSystemName());
                                iotTsKv.setDeviceSn(device.getDeviceSn());
                                iotTsKv.setDeviceName(device.getDeviceName());
                                iotTsKv.setDeviceConfigKey(devicePoint.getDeviceConfigKey());
                                iotTsKv.setPointSn(devicePoint.getPointSn());
                                iotTsKv.setPointName(devicePoint.getPointName());
                                iotTsKv.setPointValue(pointValue);
                                iotTsKv.setPointDesc(pointDesc);
                                iotTsKv.setPointValueType(model.getData().getValuetype());
                                iotTsKv.setTs(timeFormat2.parse(timeFormat2.format(singleTime)));
                                iotTsKv.setMsgType(model.getType());
                                iotTsKv.setPointUnit(devicePoint.getPointUnit());
                                iotTsKvRepository.save(iotTsKv);
                                //endregion

                                //如果节点有数据上来，就改已启用
                                nodeRepository.updateNodeStatus(node.getNodeId());
                                deviceRepository.updateDeviceStatus(node.getNodeId(), device.getDeviceId());
                               
                            }
                        }
                    }
                }
            }
            return ResponseResult.success();
        } catch (Exception e) {
            return ResponseResult.error(e.getMessage());
        }
    }

    /**
     * 查询多个网关导入设备的最新采集设备信息
     */
    @RequestMapping(value = "latestCollectDevicesData", method = {RequestMethod.POST})
    @ApiOperation("系统或节点或设备下所有点位采集数据视图")
    @UserLoginToken
    public ResponseResult<PageModel> latestCollectDevicesData(@RequestBody TsKvModelRequest request) {


        Page<DevicePoint> pointByNodeIdOrSystemIdOrDeviceId = pageableService.getPointByNodeIdOrSystemIdOrDeviceId(request.getNodeId(), request.getSystemId(), request.getDeviceId(), request.getNumber(), request.getPageSize());

        List<DevicePoint> dps = pointByNodeIdOrSystemIdOrDeviceId.getContent();


        List<String> pointSns = new ArrayList<>();
        dps.forEach(e -> pointSns.add(e.getPointSn()));

        List<IotDevicePointDataView> ipdv = new ArrayList<>();
        List<DevicePoint> result = dps;

        if (result != null && !result.isEmpty()) {
            result.forEach(e -> {
                Device device = e.getDevice();
                Node node = device.getNode();
                SysDictType systemType = device.getSystemType();

                IotDevicePointDataView v = new IotDevicePointDataView();
                v.setDeviceName(device.getDeviceName());
                v.setNodeName(node.getNodeName());
                v.setPointName(e.getPointName());
                v.setSystemName(systemType.getSystemName());
                v.setPointSn(e.getPointSn());
                v.setPointUnit(e.getPointUnit());
                v.setProvinceRegionName(node.getProvinceRegionName());
                v.setCountyRegionName(node.getCountyRegionName());
                v.setCityRegionName(node.getCityRegionName());
                v.setPointDesc(e.getPointDesc());
                v.setOnline(device.getOnline());
                v.setLoadType(FieldConvertUtil.convertLoadType(device.getLoadType()));
                v.setLoadProperties(FieldConvertUtil.convertLoadProperties(device.getLoadProperties()));

                ipdv.add(v);
            });
        }

        Map<String, List<IotDevicePointDataView>> collect = ipdv.stream()
                .collect(Collectors.groupingBy(IotDevicePointDataView::getPointSn));

        List<IotTsKvLast> datas = iotTsKvLastRepository.findAllByPointSnIn(new ArrayList<>(collect.keySet()));

        PageModel pageModel = new PageModel();
        //封装到pageUtil

        datas.forEach(dd -> {
            List<IotDevicePointDataView> iotDevicePointDataViews = collect.get(dd.getPointSn());
            if (iotDevicePointDataViews != null && !iotDevicePointDataViews.isEmpty()) {
                IotDevicePointDataView iotDevicePointDataView = iotDevicePointDataViews.get(0);
                iotDevicePointDataView.setPointValue(dd.getPointValue());
                iotDevicePointDataView.setTs(TimeUtil.dateFormat(dd.getTs()));
                iotDevicePointDataView.setPointUnit(dd.getPointUnit());
            }
        });

        pageModel.setPageSize(request.getPageSize());
        pageModel.setContent(ipdv);
        pageModel.setTotalPages(pointByNodeIdOrSystemIdOrDeviceId.getTotalPages());
        pageModel.setTotalElements((int) pointByNodeIdOrSystemIdOrDeviceId.getTotalElements());
        pageModel.setNumber(pointByNodeIdOrSystemIdOrDeviceId.getNumber() + 1);

        return ResponseResult.success(pageModel);

    }

    /**
     * 查询点位的数据点图
     */
    @UserLoginToken
    @RequestMapping(value = "pointDataList", method = {RequestMethod.POST})
    @ApiOperation("数据点图")
    public ResponseResult<List<PointDataExport>> pointDataList(@RequestBody PointModel model) {

        try {
            List<IotTsKv> iotTsKvs = iotTsKvRepository.findAllByPointSnAndTsBetweenOrderByTsAsc(model.getPointSn(), model.getStartTs(), TimeUtil.dateAddSeconds(TimeUtil.getPreDay(model.getEndTs(), 1), -1));

            List<PointDataExport> exports = new ArrayList<>();
            iotTsKvs.forEach(e -> {
                PointDataExport pointDataExport = new PointDataExport();
                pointDataExport.setTimeStamp(e.getTs());
                pointDataExport.setValue(e.getPointValue());

                exports.add(pointDataExport);
            });
            return ResponseResult.success(exports);
        } catch (Exception e) {
            return ResponseResult.error(e.getMessage());
        }


    }

    /**
     * 点位的数据点图导出
     */
    @ApiOperation("运行调度-运行数据-点位的数据点图导出")
    @UserLoginToken
    @RequestMapping(value = "pointDataListExcel", method = {RequestMethod.POST})
    public void pointDataListExcel(HttpServletResponse response, @RequestBody PointModel model) {
        try {
            exec(response, pointDataList(model).getData(), PointDataExport.class, iExcelOutPutService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @PostMapping("/alarmInformation")
//    @ApiOperation(value = "告警信息上报")
//    public ResponseResult alarmInformation(@RequestBody AlarmInformation alarmInformation) {
//        try {
//            // 保存告警信息
//            AlarmInformation saved = alarmInformationRepository.save(alarmInformation);
//            logger.info("告警信息保存成功: {}", saved);
//            return ResponseResult.success("告警信息保存成功");
//        } catch (Exception e) {
//            logger.error("告警信息保存失败", e);
//            return ResponseResult.error("告警信息保存失败");
//        }
//    }

}
