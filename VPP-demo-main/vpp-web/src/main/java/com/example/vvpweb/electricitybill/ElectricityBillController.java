package com.example.vvpweb.electricitybill;


import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.Enum.ElectricityBillNodeEnum;
import com.example.vvpcommom.Enum.NodePostTypeEnum;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpcommom.UserLoginToken;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.exceloutput.service.IExcelOutPutService;
import com.example.vvpservice.nodeprofit.model.BillNodeProfit;
import com.example.vvpservice.nodeprofit.service.INodeProfitService;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.tree.model.StructTreeResponse;
import com.example.vvpservice.tree.service.ITreeLabelService;
import com.example.vvpweb.BaseExcelController;
import com.example.vvpweb.electricitybill.model.ElectricityBillChargingPileResponse;
import com.example.vvpweb.electricitybill.model.ElectricityBillPvResponse;
import com.example.vvpweb.electricitybill.model.ElectricityBillRequest;
import com.example.vvpweb.electricitybill.model.ElectricityBillStorageEnergyResponse;
import com.example.vvpweb.systemmanagement.nodemodel.model.NodeTypeResponse;
import io.swagger.annotations.Api;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/electricity_bill_management/electricity_bill")
@CrossOrigin
@Api(value = "电费账单", tags = {"电费账单"})
public class ElectricityBillController extends BaseExcelController {
    private static final String SYSTEMID = "nengyuanzongbiao";
    private static final String POINTDESC = "energy";


    @Autowired
    private CfgPhotovoltaicTouPriceRepository cfgPhotovoltaicTouPriceRepository;

    @Autowired
    private CfgStorageEnergyStrategyRepository cfgStorageEnergyStrategyRepository;

    @Autowired
    private CfgStorageEnergyShareProportionRepository shareProportionRepository;

    @Autowired
    private CfgPhotovoltaicDiscountRateRepository cfgPhotovoltaicDiscountRateRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private CfgStorageEnergyBaseInfoRepository cfgStorageEnergyBaseInfoRepository;

    @Autowired
    private DevicePointRepository devicePointRepository;

    @Autowired
    private ITreeLabelService iTreeLabelService;

    @Autowired
    private IUserService userService;


    @Autowired
    private INodeProfitService nodeProfitService;

    @Resource
    private NodeRepository nodeRepository;

    @Resource
    private IExcelOutPutService iExcelOutPutService;


    @UserLoginToken
    @RequestMapping(value = "electricityBillStorageEnergyExcel", method = {RequestMethod.POST})
    public void electricityBillStorageEnergyExcel(HttpServletResponse response, @RequestBody ElectricityBillRequest request) {

        try {
            ResponseResult<ElectricityBillStorageEnergyResponse> result = electricityBillStorageEnergy(request);

            InputStream inputStream = new ClassPathResource("/excel/储能.xlsx").getInputStream();

            execTemplate(response, inputStream, JSONObject.parseObject(JSONObject.toJSONString(result.getData()),Map.class), iExcelOutPutService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UserLoginToken
    @RequestMapping(value = "electricityBillPvExcel", method = {RequestMethod.POST})
    public void electricityBillPvExcel(HttpServletResponse response, @RequestBody ElectricityBillRequest request) {

        try {
            ResponseResult<ElectricityBillPvResponse> result = electricityBillPv(request);

            InputStream inputStream = new ClassPathResource("/excel/光伏.xlsx").getInputStream();

            execTemplate(response, inputStream, JSONObject.parseObject(JSONObject.toJSONString(result.getData()),Map.class), iExcelOutPutService);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 电费账单--储能
     */
    @UserLoginToken
    @RequestMapping(value = "electricityBillStorageEnergy", method = {RequestMethod.POST})
    public ResponseResult<ElectricityBillStorageEnergyResponse> electricityBillStorageEnergy(@RequestBody ElectricityBillRequest request) {

        String forwardEnergyKey = "forward_active_energy";
        String backwardEnergyKey = "backward_active_energy";

        ElectricityBillStorageEnergyResponse response = new ElectricityBillStorageEnergyResponse();
        String nodeId = request.getNodeId();

        List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
        if (!allowStorageEnergyNodeIds.contains(nodeId)) {
            return ResponseResult.error("储能节点不存在或者没有该节点权限");
        }

        List<CfgStorageEnergyBaseInfo> cbinfo = cfgStorageEnergyBaseInfoRepository.findAllByNodeIdIn(Arrays.asList(nodeId));

        if (cbinfo == null || cbinfo.isEmpty()) {
            return ResponseResult.error("当前节点未绑定充放电设备");
        }

        CfgStorageEnergyBaseInfo baseInfo = cbinfo.get(0);
        String chargingDeviceSn = baseInfo.getChargingDeviceSn();
        String dischargingDeviceSn = baseInfo.getDischargingDeviceSn();

        DevicePoint inDevicePoint = devicePointRepository.findByDeviceSnAndPointKey(chargingDeviceSn, forwardEnergyKey);
        if (inDevicePoint == null) {
            try {
                inDevicePoint = devicePointRepository.findByDeviceSnAndPointDesc(chargingDeviceSn, POINTDESC);
            } catch (Exception ignore) {
            }
        }
        DevicePoint outDevicePoint = devicePointRepository.findByDeviceSnAndPointKey(dischargingDeviceSn, backwardEnergyKey);
        if (outDevicePoint == null) {
            try {
                outDevicePoint = devicePointRepository.findByDeviceSnAndPointDesc(dischargingDeviceSn, POINTDESC);
            } catch (Exception ignore) {
            }
        }

        if (inDevicePoint != null) {
            Device device = inDevicePoint.getDevice();
            String deviceName = "";
            if (device != null) {
                deviceName = device.getDeviceName();
            }
            response.setInMeterDeviceNum(chargingDeviceSn);
            response.setInMeterDeviceName(deviceName);
        }

        if (outDevicePoint != null) {
            try {
                outDevicePoint = devicePointRepository.findByDeviceSnAndPointDesc(dischargingDeviceSn, POINTDESC);
            } catch (Exception ignore) {
            }
            Device device = outDevicePoint.getDevice();
            String deviceName = "";
            if (device != null) {
                deviceName = device.getDeviceName();
            }

            response.setOutMeterDeviceNum(dischargingDeviceSn);
            response.setOutMeterDeviceName(deviceName);
        }

        Date start ;
        Date end ;

        if(!ElectricityBillRequest.DateType.YEAR.equals(request.getType())){
            List<CfgStorageEnergyStrategy> cfgStorageEnergyStrategies = cfgStorageEnergyStrategyRepository.findAllByNodeIdAndSystemIdAndEffectiveDate(nodeId, SYSTEMID, TimeUtil.getMonthStart(request.getDate()));

            if (cfgStorageEnergyStrategies == null || cfgStorageEnergyStrategies.isEmpty()) {
                return ResponseResult.error("未配置该节点的这个月的电价策略");
            }

            if(ElectricityBillRequest.DateType.MONTH.equals(request.getType())){
                start = TimeUtil.getMonthStart(request.getDate());
                end = TimeUtil.getMonthEnd(request.getDate());
            }else {
                start = request.getDate();
                end = request.getDate();
            }

            BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(nodeId, start, end);

            response.setPriceHigh(BigDecimal.valueOf(billNodeProfit.getPriceHigh()));
            response.setPricePeak(BigDecimal.valueOf(billNodeProfit.getPricePeak()));
            response.setPriceStable(BigDecimal.valueOf(billNodeProfit.getPriceStable()));
            response.setPriceLow(BigDecimal.valueOf(billNodeProfit.getPriceLow()));
//            response.setPriceRavine(BigDecimal.valueOf(billNodeProfit.getPriceRavine()));

            response.setInElectricityHigh(BigDecimal.valueOf(billNodeProfit.getInElectricityHigh()));
            response.setInElectricityPeak(BigDecimal.valueOf(billNodeProfit.getInElectricityPeak()));
            response.setInElectricityLow(BigDecimal.valueOf(billNodeProfit.getInElectricityLow()));
            response.setInElectricityStable(BigDecimal.valueOf(billNodeProfit.getInElectricityStable()));
//            response.setInElectricityRavine(BigDecimal.valueOf(billNodeProfit.getInElectricityRavine()));

            response.setOutElectricityHigh(BigDecimal.valueOf(billNodeProfit.getOutElectricityHigh()));
            response.setOutElectricityPeak(BigDecimal.valueOf(billNodeProfit.getOutElectricityPeak()));
            response.setOutElectricityLow(BigDecimal.valueOf(billNodeProfit.getOutElectricityLow()));
            response.setOutElectricityStable(BigDecimal.valueOf(billNodeProfit.getOutElectricityStable()));
//            response.setOutElectricityRavine(BigDecimal.valueOf(billNodeProfit.getOutElectricityRavine()));

            response.setInElectricityHighPrice(BigDecimal.valueOf(billNodeProfit.getInElectricityHighPrice()));
            response.setInElectricityPeakPrice(BigDecimal.valueOf(billNodeProfit.getInElectricityPeakPrice()));
            response.setInElectricityLowPrice(BigDecimal.valueOf(billNodeProfit.getInElectricityLowPrice()));
            response.setInElectricityStablePrice(BigDecimal.valueOf(billNodeProfit.getInElectricityStablePrice()));
//            response.setInElectricityRavinePrice(BigDecimal.valueOf(billNodeProfit.getInElectricityRavinePrice()));

            response.setOutElectricityHighPrice(BigDecimal.valueOf(billNodeProfit.getOutElectricityHighPrice()));
            response.setOutElectricityPeakPrice(BigDecimal.valueOf(billNodeProfit.getOutElectricityPeakPrice()));
            response.setOutElectricityLowPrice(BigDecimal.valueOf(billNodeProfit.getOutElectricityLowPrice()));
            response.setOutElectricityStablePrice(BigDecimal.valueOf(billNodeProfit.getOutElectricityStablePrice()));
//            response.setOutElectricityRavinePrice(BigDecimal.valueOf(billNodeProfit.getOutElectricityRavinePrice()));


            response.setProfitValue(response.getOutElectricityPeakPrice().
                    add(response.getOutElectricityLowPrice()).
                    add(response.getOutElectricityStablePrice()).
                    add(response.getOutElectricityHighPrice()).
//                    add(response.getOutElectricityRavinePrice()).
                    subtract(response.getInElectricityHighPrice()).
                    subtract(response.getInElectricityPeakPrice()).
                    subtract(response.getInElectricityStablePrice()).
                    subtract(response.getInElectricityLowPrice())
//                    subtract(response.getInElectricityRavine())
            );

        }else {
            start = TimeUtil.getYearStart(request.getDate());
            end = TimeUtil.getYearEnd(request.getDate());

            BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(nodeId, start, end);

            response.setProfitValue(BigDecimal.valueOf(billNodeProfit.getOutElectricityPeakPrice()).
                    add(BigDecimal.valueOf(billNodeProfit.getOutElectricityLowPrice())).
                    add(BigDecimal.valueOf(billNodeProfit.getOutElectricityStablePrice())).
                    add(BigDecimal.valueOf(billNodeProfit.getOutElectricityHighPrice())).
//                    add(BigDecimal.valueOf(billNodeProfit.getOutElectricityRavinePrice())).
                    subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityHighPrice())).
                    subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityPeakPrice())).
                    subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityStablePrice())).
                    subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityLowPrice()))
//                    subtract(BigDecimal.valueOf(billNodeProfit.getInElectricityRavinePrice()))
            );
        }




        CfgStorageEnergyShareProportion share = shareProportionRepository.findByNodeIdAndSystemIdAndOrder(nodeId, "nengyuanzongbiao", Integer.valueOf(TimeUtil.toYmNumberStr(request.getDate())));
/*  电费账单 小于零取零
        if (response.getProfitValue().compareTo(BigDecimal.ZERO) < 0) {
            response.setProfitValue(BigDecimal.ZERO);
        }*/

        if (share != null) {
            response.setLoadProfitValue(new BigDecimal(share.getLoadProp()).multiply(response.getProfitValue()));
            response.setConsumerProfitValue(new BigDecimal(share.getPowerUserProp()).multiply(response.getProfitValue()));
        }


        return ResponseResult.success(response);
    }


    /**
     * 电费账单--光伏
     */
    @UserLoginToken
    @RequestMapping(value = "electricityBillPv", method = {RequestMethod.POST})
    public ResponseResult<ElectricityBillPvResponse> electricityBillPv(@RequestBody ElectricityBillRequest request) {
        ElectricityBillPvResponse response = new ElectricityBillPvResponse();
        String nodeId = request.getNodeId();

        List<String> allowPvNodeIds = userService.getAllowPvNodeIds();
        if (!allowPvNodeIds.contains(nodeId)) {
            return ResponseResult.error("光伏节点不存在或者没有该节点权限");
        }

        List<Device> meteringDevices = deviceRepository.findAllByNode_NodeIdAndSystemType_SystemIdAndConfigKey(nodeId, SYSTEMID, "metering_device");

        if (meteringDevices == null || meteringDevices.isEmpty()) {
            return ResponseResult.error("当前节点未绑定光伏电表电设备");
        }
        if (meteringDevices.size() > 1) {
            return ResponseResult.error("当前节点绑定多个光伏电表电设备,有且只能有一个设备");
        }

        String deviceSn = meteringDevices.get(0).getDeviceSn();


        response.setMeterDeviceName(meteringDevices.get(0).getDeviceName());
        response.setMeterDeviceNum(deviceSn);

        Date start ;
        Date end ;

        if(!ElectricityBillRequest.DateType.YEAR.equals(request.getType())){
            List<CfgPhotovoltaicTouPrice> cfgPhotovoltaicTouPrices = cfgPhotovoltaicTouPriceRepository.findAllByNodeIdAndSystemIdAndEffectiveDate(nodeId, SYSTEMID, TimeUtil.getMonthStart(request.getDate()));

            if (cfgPhotovoltaicTouPrices == null || cfgPhotovoltaicTouPrices.isEmpty()) {
                return ResponseResult.error("未配置该节点的这个月的电价策略");
            }


            if(ElectricityBillRequest.DateType.MONTH.equals(request.getType())){
                start = TimeUtil.getMonthStart(request.getDate());
                end = TimeUtil.getMonthEnd(request.getDate());
            }else {
                start = request.getDate();
                end = request.getDate();
            }

            BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(nodeId, start, end);

            response.setPriceHigh(BigDecimal.valueOf(billNodeProfit.getPriceHigh()));
            response.setPricePeak(BigDecimal.valueOf(billNodeProfit.getPricePeak()));
            response.setPriceStable(BigDecimal.valueOf(billNodeProfit.getPriceStable()));
            response.setPriceLow(BigDecimal.valueOf(billNodeProfit.getPriceLow()));
//            response.setPriceRavine(BigDecimal.valueOf(billNodeProfit.getPriceRavine()));

            response.setElectricityHigh(BigDecimal.valueOf(billNodeProfit.getOutElectricityHigh()));
            response.setElectricityPeak(BigDecimal.valueOf(billNodeProfit.getOutElectricityPeak()));
            response.setElectricityLow(BigDecimal.valueOf(billNodeProfit.getOutElectricityLow()));
            response.setElectricityStable(BigDecimal.valueOf(billNodeProfit.getOutElectricityStable()));
//            response.setElectricityRavine(BigDecimal.valueOf(billNodeProfit.getOutElectricityRavine()));

            response.setElectricityHighPrice(BigDecimal.valueOf(billNodeProfit.getOutElectricityHighPrice()));
            response.setElectricityPeakPrice(BigDecimal.valueOf(billNodeProfit.getOutElectricityPeakPrice()));
            response.setElectricityLowPrice(BigDecimal.valueOf(billNodeProfit.getOutElectricityLowPrice()));
            response.setElectricityStablePrice(BigDecimal.valueOf(billNodeProfit.getOutElectricityStablePrice()));
//            response.setElectricityRavinePrice(BigDecimal.valueOf(billNodeProfit.getOutElectricityRavinePrice()));


            response.setProfitValue(
                    response.getElectricityPeakPrice().
                            add(response.getElectricityLowPrice()).
                            add(response.getElectricityStablePrice()).
                            add(response.getElectricityHighPrice())
//                            add(response.getElectricityRavinePrice())
            );

        }else {
            start = TimeUtil.getYearStart(request.getDate());
            end = TimeUtil.getYearEnd(request.getDate());

            BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(nodeId, start, end);


            response.setProfitValue(
                    BigDecimal.valueOf(billNodeProfit.getOutElectricityPeakPrice()).
                            add(BigDecimal.valueOf(billNodeProfit.getOutElectricityLowPrice())).
                            add(BigDecimal.valueOf(billNodeProfit.getOutElectricityStablePrice())).
                            add(BigDecimal.valueOf(billNodeProfit.getOutElectricityHighPrice()))
//                            add(BigDecimal.valueOf(billNodeProfit.getOutElectricityRavinePrice()))
            );
        }


        CfgPhotovoltaicDiscountRate share = cfgPhotovoltaicDiscountRateRepository.findByNodeIdAndSystemIdAndOrder(nodeId, "nengyuanzongbiao", Integer.valueOf(TimeUtil.toYmNumberStr(request.getDate())));
/*  电费账单 小于零取零
        if (response.getProfitValue().compareTo(BigDecimal.ZERO) < 0) {
            response.setProfitValue(BigDecimal.ZERO);
        }*/

        if (share != null) {
            response.setLoadProfitValue(new BigDecimal(share.getLoadProp()).multiply(response.getProfitValue()));
            response.setConsumerProfitValue(new BigDecimal(share.getPowerUserProp()).multiply(response.getProfitValue()));
        }


        return ResponseResult.success(response);
    }

    /**
     * 电费账单--充电桩
     */
    @UserLoginToken
    @RequestMapping(value = "electricityBillChargingPile", method = {RequestMethod.POST})
    public ResponseResult<ElectricityBillChargingPileResponse> electricityBillChargingPile(@RequestBody ElectricityBillRequest request) {
        ElectricityBillChargingPileResponse response = new ElectricityBillChargingPileResponse();
        String nodeId = request.getNodeId();

        List<String> allChargingPileLoadNodeIds = userService.getAllChargingPileLoadNodeIds();
        if (!allChargingPileLoadNodeIds.contains(nodeId)) {
            return ResponseResult.error("充电桩节点不存在或者没有该节点权限");
        }
        Node node = nodeRepository.findByNodeId(nodeId);
        response.setPosition(node.getNodeName());

        List<Device> meteringDevices = deviceRepository.findAllByNodeNodeIdAndConfigKey(nodeId, "metering_device");
        if (null == meteringDevices || meteringDevices.isEmpty()) {
            return ResponseResult.success(response);
        }
        response.setMeterDeviceName(meteringDevices.get(0).getDeviceName());
        response.setMeterDeviceNum(meteringDevices.get(0).getDeviceSn());

        Date start = null;
        Date end = null;
        switch (request.getType()){
            case YEAR:
                start = TimeUtil.getYearStart(request.getDate());
                end = TimeUtil.getYearEnd(request.getDate());
                break;
            case MONTH:
                start = TimeUtil.getMonthStart(request.getDate());
                end = TimeUtil.getMonthEnd(request.getDate());
                break;
            case DAY:
                start = request.getDate();
                end = request.getDate();
                break;
            default:
        }


        BillNodeProfit billNodeProfit = nodeProfitService.getBillNodeProfit(nodeId, start, end);
        response.setPriceHigh(BigDecimal.valueOf(billNodeProfit.getPriceHigh()));
        response.setPricePeak(BigDecimal.valueOf(billNodeProfit.getPricePeak()));
        response.setPriceStable(BigDecimal.valueOf(billNodeProfit.getPriceStable()));
        response.setPriceLow(BigDecimal.valueOf(billNodeProfit.getPriceLow()));
//        response.setPriceRavine(BigDecimal.valueOf(billNodeProfit.getPriceRavine()));

        response.setElectricitySharp(BigDecimal.valueOf(billNodeProfit.getOutElectricityHigh()));
        response.setElectricityPeak(BigDecimal.valueOf(billNodeProfit.getOutElectricityPeak()));
        response.setElectricityShoulder(BigDecimal.valueOf(billNodeProfit.getOutElectricityStable()));
        response.setElectricityOffPeak(BigDecimal.valueOf(billNodeProfit.getOutElectricityLow()));
//        response.setElectricityRavine(BigDecimal.valueOf(billNodeProfit.getOutElectricityRavine()));

        response.setSharpEnergyCharge(BigDecimal.valueOf(billNodeProfit.getOutElectricityHighPrice()));
        response.setPeakEnergyCharge(BigDecimal.valueOf(billNodeProfit.getOutElectricityPeakPrice()));
        response.setShoulderEnergyCharge(BigDecimal.valueOf(billNodeProfit.getOutElectricityStablePrice()));
        response.setOffPeakEnergyCharge(BigDecimal.valueOf(billNodeProfit.getOutElectricityLowPrice()));
//        response.setRavineEnergyCharge(BigDecimal.valueOf(billNodeProfit.getOutElectricityRavinePrice()));

        response.setProfitValue(
                response.getPeakEnergyCharge().
                        add(response.getSharpEnergyCharge()).
                        add(response.getShoulderEnergyCharge()).
                        add(response.getOffPeakEnergyCharge())
//                        add(response.getRavineEnergyCharge())
        );

//        CfgStorageEnergyShareProportion share = shareProportionRepository.findByNodeIdAndSystemIdAndOrder(nodeId, "nengyuanzongbiao", Integer.valueOf(TimeUtil.toYmNumberStr(request.getDate())));

        response.setLoadProfitValue(new BigDecimal(0.9).multiply(response.getProfitValue()));
        response.setConsumerProfitValue(new BigDecimal(0.1).multiply(response.getProfitValue()));

        return ResponseResult.success(response);
    }


    @UserLoginToken
    @RequestMapping(value = "billNodeTypeList", method = {RequestMethod.POST})
    public ResponseResult<List<NodeTypeResponse>> nodeTypeList() {
        List<ElectricityBillNodeEnum> billTypes = Arrays.stream(ElectricityBillNodeEnum.values()).collect(Collectors.toList());
        List<NodeTypeResponse> nodeTypeResponses = new ArrayList<>();

        billTypes.forEach(o->{
            NodeTypeResponse response = new NodeTypeResponse();
            response.setNodeTypeId(o.getNodeTypeId());
            response.setNodeTypeKey(o.getNodePostType());
            response.setNodeTypeName(o.getNodeTypeName());
            nodeTypeResponses.add(response);
        });
        return ResponseResult.success(nodeTypeResponses);
    }

    @UserLoginToken
    @RequestMapping(value = "billNodeTree", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> billNodeTree(@RequestParam(value = "billNodeType",required = false) String billNodeType , @RequestParam(value = "billNodeId" ,required = false) String billNodeId) {
        List<StructTreeResponse> treeResponses;
        if (Strings.isEmpty(billNodeType) && Strings.isEmpty(billNodeId)){
            return ResponseResult.error(HttpServletResponse.SC_BAD_REQUEST,"节点类型与节点ID不能都为空",null);
        }

        try {
            NodePostTypeEnum nodePostTypeEnum = NodePostTypeEnum.load;
            if (!Strings.isEmpty(billNodeType)){
                nodePostTypeEnum = Arrays.stream(NodePostTypeEnum.values()).filter(l -> l.getNodePostType().equals(billNodeType))
                        .findAny().orElseThrow(() -> new RuntimeException("非法的账单节点类型"));
            }

            if (NodePostTypeEnum.pv.equals(nodePostTypeEnum)) {
                treeResponses = iTreeLabelService.pvNodeTree();
            } else if (NodePostTypeEnum.storageEnergy.equals(nodePostTypeEnum)) {
                treeResponses = iTreeLabelService.storageEnergyNodeTree();
            } else{
                treeResponses = iTreeLabelService.chargingPileNodeTree();
            }
        } catch (Exception e) {
            return ResponseResult.error(e.getMessage());
        }

        return ResponseResult.success(treeResponses);
    }

    @UserLoginToken
    @RequestMapping(value = "runBillNodeTree", method = {RequestMethod.POST})
    public ResponseResult<List<StructTreeResponse>> runBillNodeTree(@RequestParam(value = "billNodeType",required = false) String billNodeType , @RequestParam(value = "billNodeId" ,required = false) String billNodeId) {
        List<StructTreeResponse> treeResponses;
        if (Strings.isEmpty(billNodeType) && Strings.isEmpty(billNodeId)){
            return ResponseResult.error(HttpServletResponse.SC_BAD_REQUEST,"节点类型与节点ID不能都为空",null);
        }

        try {
            NodePostTypeEnum nodePostTypeEnum = NodePostTypeEnum.load;
            if (!Strings.isEmpty(billNodeType)){
                nodePostTypeEnum = Arrays.stream(NodePostTypeEnum.values()).filter(l -> l.getNodePostType().equals(billNodeType))
                        .findAny().orElseThrow(() -> new RuntimeException("非法的账单节点类型"));
            }

            if (NodePostTypeEnum.pv.equals(nodePostTypeEnum)) {
                treeResponses = iTreeLabelService.runPvNodeTree();
            } else if (NodePostTypeEnum.storageEnergy.equals(nodePostTypeEnum)) {
                treeResponses = iTreeLabelService.runStorageEnergyNodeTree();
            } else{
                treeResponses = iTreeLabelService.runChargingPileNodeTree();
            }
        } catch (Exception e) {
            return ResponseResult.error(e.getMessage());
        }

        return ResponseResult.success(treeResponses);
    }

}
