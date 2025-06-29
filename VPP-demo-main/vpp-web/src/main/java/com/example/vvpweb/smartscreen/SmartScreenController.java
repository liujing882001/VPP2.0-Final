package com.example.vvpweb.smartscreen;

import com.example.vvpcommom.EntityUtils;
import com.example.vvpcommom.PassToken;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.BiPvResources;
import com.example.vvpdomain.entity.BiStorageEnergyResources;
import com.example.vvpdomain.entity.CaEmissionFactor;
import com.example.vvpdomain.entity.IotTsKvMeteringDevice96;
import com.example.vvpweb.BaseExcelController;
import com.example.vvpweb.smartscreen.model.ElectricityLoadModel;
import com.example.vvpweb.smartscreen.model.GenerationLoadModel;
import com.example.vvpweb.smartscreen.model.NameValueModel;
import com.example.vvpweb.smartscreen.model.XYSModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zph
 * @description 智慧大屏
 *
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/smartscreen")
@CrossOrigin
@Api(value = "智慧大屏", tags = {"智慧大屏"})
public class SmartScreenController extends BaseExcelController {

    private static Logger LOGGER = LoggerFactory.getLogger(SmartScreenController.class);

    @Resource
    private AiPvRepository aiPvRepository;

    /**
     * 光伏发电负荷曲线
     */
    @ApiOperation("发电负荷曲线")
    @RequestMapping(value = "/generationLoadCurve", method = {RequestMethod.GET})
    @PassToken
    public List<XYSModel> generationLoadCurve() {

        List<XYSModel> xysModels = new ArrayList<>();

        List<XYSModel> xysModels预测 = new ArrayList<>();
        List<XYSModel> xysModels实际 = new ArrayList<>();

        try {
            List<Object[]> loadCurveList = aiPvRepository.findGenerationLoadCurve();
            if (loadCurveList != null && loadCurveList.size() > 0) {
                List<GenerationLoadModel> loadModels = EntityUtils.castEntity(loadCurveList, GenerationLoadModel.class, new GenerationLoadModel());
                if (loadModels != null && loadModels.size() > 0) {
                    // 创建一个BigDecimal对象，表示1000
                    BigDecimal divisor = new BigDecimal("1000");
                    for (GenerationLoadModel e : loadModels) {
                        XYSModel 预测 = new XYSModel();
                        预测.setS("预测");
                        预测.setY(String.valueOf(e.getPredictedValue().divide(divisor)));
                        预测.setX(e.getCountDataTime());
                        xysModels预测.add(预测);

                        XYSModel 实际 = new XYSModel();
                        实际.setS("实际");
                        实际.setY(String.valueOf(e.getActualValue().divide(divisor)));
                        实际.setX(e.getCountDataTime());
                        xysModels实际.add(实际);
                    }
                    xysModels.addAll(xysModels预测);
                    xysModels.addAll(xysModels实际);
                }
            }
        } catch (Exception ex) {
        }
        return xysModels;
    }

    @Resource
    private AiLoadRepository aiLoadRepository;

    /**
     * 用电负荷曲线
     */
    @ApiOperation("用电负荷曲线")
    @RequestMapping(value = "/electricityLoadCurve", method = {RequestMethod.GET})
    @PassToken
    public List<XYSModel> electricityLoadCurve() {

        List<XYSModel> xysModels = new ArrayList<>();

        List<XYSModel> xysModels预测 = new ArrayList<>();
        List<XYSModel> xysModels实际 = new ArrayList<>();

        try {
            List<Object[]> loadCurveList = aiLoadRepository.findElectricityLoadCurve();
            if (loadCurveList != null && loadCurveList.size() > 0) {
                List<ElectricityLoadModel> loadModels = EntityUtils.castEntity(loadCurveList, ElectricityLoadModel.class, new ElectricityLoadModel());
                if (loadModels != null && loadModels.size() > 0) {
                    // 创建一个BigDecimal对象，表示1000
                    BigDecimal divisor = new BigDecimal("1000");
                    for (ElectricityLoadModel e : loadModels) {

                        XYSModel 预测 = new XYSModel();
                        预测.setS("预测");
                        预测.setY(String.valueOf(e.getPredictedValue().divide(divisor)));
                        预测.setX(e.getCountDataTime());
                        xysModels预测.add(预测);

                        XYSModel 实际 = new XYSModel();
                        实际.setS("实际");
                        实际.setY(String.valueOf(e.getActualValue().divide(divisor)));
                        实际.setX(e.getCountDataTime());
                        xysModels实际.add(实际);
                    }
                    xysModels.addAll(xysModels预测);
                    xysModels.addAll(xysModels实际);
                }
            }
        } catch (Exception ex) {
        }
        return xysModels;
    }

    @Resource
    BiStorageEnergyResourcesRepository biStorageEnergyResourcesRepository;

    /**
     * 储能电站
     */
    @ApiOperation("储能电站")
    @RequestMapping(value = "/energyStoragePowerStation/outCapacity", method = {RequestMethod.GET})
    @PassToken
    public NameValueModel energyStoragePowerStationOutCapacity() {

        NameValueModel model = new NameValueModel();
        //当前可放容量kwh
        Double outCapacity = (double) 0;
        try {
            List<BiStorageEnergyResources> resourcesItems = biStorageEnergyResourcesRepository.findAll();
            if (resourcesItems != null && resourcesItems.size() > 0) {
                List<BiStorageEnergyResources> resourcesList = resourcesItems
                        .stream()
                        .filter(p -> p.getIsEnabled() == true && p.getOnline() == true)
                        .collect(Collectors.toList());
                if (resourcesList != null && resourcesList.size() > 0) {

                    Double capacity = resourcesList.stream().mapToDouble(c -> c.getCapacity()).sum();

                    Double soc_权 = resourcesList.stream()
                            .reduce(0.0, (x, y) -> x + (y.getCapacity() * y.getSoc()), Double::sum);

                    double soc = capacity != 0 ? soc_权 / capacity : capacity;
                    outCapacity = capacity * soc;
                }
            }
        } catch (Exception ex) {
        }
        model.setName("实时可放容");
        model.setValue(outCapacity <= 0 ? String.valueOf(0) : String.format("%.4f", outCapacity / 1000));
        return model;
    }


    /**
     * 储能电站
     */
    @ApiOperation("储能电站")
    @RequestMapping(value = "/energyStoragePowerStation/soc", method = {RequestMethod.GET})
    @PassToken
    public NameValueModel energyStoragePowerStationSoc() {

        NameValueModel model = new NameValueModel();
        // soc
        double soc = (double) 0;

        try {
            List<BiStorageEnergyResources> resourcesItems = biStorageEnergyResourcesRepository.findAll();
            if (resourcesItems != null && resourcesItems.size() > 0) {
                List<BiStorageEnergyResources> resourcesList = resourcesItems
                        .stream()
                        .filter(p -> p.getIsEnabled() == true && p.getOnline() == true)
                        .collect(Collectors.toList());
                if (resourcesList != null && resourcesList.size() > 0) {

                    Double capacity = resourcesList.stream().mapToDouble(c -> c.getCapacity()).sum();

                    Double soc_权 = resourcesList.stream()
                            .reduce(0.0, (x, y) -> x + (y.getCapacity() * y.getSoc()), Double::sum);
                    soc = capacity != 0 ? soc_权 / capacity : capacity;

                }
            }
        } catch (Exception ex) {
        }
        model.setName("SOC");
        model.setValue(String.valueOf(soc * 100 ));
        return model;
    }


    /**
     * 储能电站
     */
    @ApiOperation("储能电站")
    @RequestMapping(value = "/energyStoragePowerStation/soh", method = {RequestMethod.GET})
    @PassToken
    public NameValueModel energyStoragePowerStationSoh() {

        NameValueModel model = new NameValueModel();
        //soh
        double soh = (double) 0;
        try {
            List<BiStorageEnergyResources> resourcesItems = biStorageEnergyResourcesRepository.findAll();
            if (resourcesItems != null && resourcesItems.size() > 0) {
                List<BiStorageEnergyResources> resourcesList = resourcesItems
                        .stream()
                        .filter(p -> p.getIsEnabled() == true && p.getOnline() == true)
                        .collect(Collectors.toList());
                if (resourcesList != null && resourcesList.size() > 0) {

                    Double capacity = resourcesList.stream().mapToDouble(c -> c.getCapacity()).sum();
                    Double soh_权 = resourcesList.stream()
                            .reduce(0.0, (x, y) -> x + (y.getCapacity() * y.getSoh()), Double::sum);
                    soh = capacity != 0 ? soh_权 / capacity : capacity;

                }
            }
        } catch (Exception ex) {
        }
        model.setName("SOH");
        model.setValue(String.valueOf(soh * 100));
        return model;
    }


    @Resource
    BiPvResourcesRepository biPvResourcesRepository;
    @Resource
    IotTsKvMeteringDevice96Repository device96Repository;

    /**
     * 发电信息统计
     */
    @ApiOperation("发电信息统计")
    @RequestMapping(value = "/powerGenerationInformationStatistics/todayPowerGeneration", method = {RequestMethod.GET})
    @PassToken
    public NameValueModel powerGenerationInformationStatisticsTodayPowerGeneration() {

        NameValueModel model = new NameValueModel();

        SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fmt_y = new SimpleDateFormat("yyyy");
        //当日发电量
        double now_energy = 0;
        String ts = fmt_ymd.format(new Date());
        int year = Integer.parseInt(fmt_y.format(new Date()));

        List<BiPvResources> resourcesItems = biPvResourcesRepository.findAll();
        if (resourcesItems != null && resourcesItems.size() > 0) {

            List<BiPvResources> resourcesList = resourcesItems
                    .stream()
                    .filter(p -> p.getIsEnabled() == true && p.getOnline() == true)
                    .collect(Collectors.toList());
            if (resourcesList != null && resourcesList.size() > 0) {
                now_energy = resourcesList.stream().filter(c -> fmt_ymd.format(c.getTs()).equals(ts)).mapToDouble(c -> c.getNowEnergy()).sum();
            }
        }
        model.setValue(now_energy <= 0 ?String.valueOf( 0) : String.format("%.4f", now_energy / 1000));
        model.setName("今日发电量");
        return model;
    }

    /**
     * 发电信息统计
     */
    @ApiOperation("发电信息统计")
    @RequestMapping(value = "/powerGenerationInformationStatistics/thisYearPowerGeneration", method = {RequestMethod.GET})
    @PassToken
    public NameValueModel powerGenerationInformationStatisticsThisYearPowerGeneration() {

        NameValueModel model = new NameValueModel();

        SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fmt_y = new SimpleDateFormat("yyyy");

        //今年发电量
        double now_year_energy = 0;
        int year = Integer.parseInt(fmt_y.format(new Date()));

        try {
            Date firstDay = fmt_ymd.parse(fmt_ymd.format(TimeUtil.getYearFirstDay(year)));
            List<IotTsKvMeteringDevice96> device96s = device96Repository.findNowYearPVEnergyPower(firstDay);
            if (device96s != null && device96s.size() > 0) {
                now_year_energy = device96s.stream().mapToDouble(c -> c.getHTotalUse()).sum();
            }
        } catch (Exception ex) {
        }
        model.setName("今年发电量");
        model.setValue(now_year_energy <= 0 ? String.valueOf(0) : String.format("%.4f", now_year_energy / 1000));
        return model;
    }


    @Resource
    private CaEmissionFactorRepository caEmissionFactorRepository;
    @Resource
    private IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository;

    /**
     * 碳减排量T
     */
    @ApiOperation("碳减排量T")
    @RequestMapping(value = "/carbonEmissionsReduction", method = {RequestMethod.GET})
    @PassToken
    public NameValueModel carbonEmissionsReduction() {

        NameValueModel model = new NameValueModel();

        SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
        double 碳排放因子 = (double) 0;
        double 光伏累计发电量 = 0;

        try {
            //碳减排量（t）
            List<CaEmissionFactor> caEmissionFactors = caEmissionFactorRepository.findAll();
            if (caEmissionFactors != null && caEmissionFactors.size() > 0) {
                caEmissionFactors = caEmissionFactors.stream()
                        .filter(p -> p.getProvince().equals("浙江省") && p.getEmissionFactorName().equals("外购电力") && p.getSStatus() == 1).collect(Collectors.toList());
                if (caEmissionFactors != null && caEmissionFactors.size() > 0) {
                    CaEmissionFactor caEmissionFactor = caEmissionFactors.get(0);
                    碳排放因子 = caEmissionFactor == null ? (double) 0 : caEmissionFactor.getCo2();
                }
            }
        } catch (Exception ex) {
        }

        try {
            Date dt = fmt_ymd.parse(fmt_ymd.format(new Date()));
            光伏累计发电量 = iotTsKvMeteringDevice96Repository.findALLPVNodeTotalEnergyCountByDate(dt);
        } catch (Exception e) {
        }


        model.setName("碳减排");
        model.setValue(String.valueOf(Double.parseDouble(String.format("%.2f", (光伏累计发电量 * 碳排放因子) / 1000))));
        return model;
    }
}