package com.example.vvpweb.Intelligentcabinet;

import com.example.vvpcommom.*;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpweb.Intelligentcabinet.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 *
 */
@RestController
@RequestMapping("/gicc")
@CrossOrigin
@Api(value = "绿色智能机柜", tags = {"绿色智能机柜-UPS状态及收益"})
public class EnergyStorageStatusrRetrieveController {

    @Resource
    private UserRepository userRepository;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private IotTsKvRepository iotTsKvRepository;
    @Autowired
    private IUserService userService;

    private static final List<String> MODEL_KEYS = Arrays.asList(
            "battery_current",
            "line_voltage",
            "input_frequency",
            "output_frequency",
            "output_voltage",
            "output_current",
            "output_load_percent",
            "battery_voltage",
            "ups_temperature",
            "ups_status",
            "battery_capacity"
    );

    private static final List<String> STATUS_KEYS = Arrays.asList(
            "ups_status"
    );

    private static final List<String> PUE_KEYS = Arrays.asList(
            "output_voltage",
            "output_current",
            "ups_load"
    );

    @Autowired
    private NodeProfitMonthForecastingRepository monthForecastingRepository;

    @Autowired
    private NodeProfitDayForecastingRepository dayForecastingRepository;

    @ApiOperation("UPS运行状态")
    @UserLoginToken
    @RequestMapping(value = "/ups/retrieveOperationalStatus", method = {RequestMethod.POST})
    public ResponseResult<List<UpsStatusResponse>> retrieveOperationalStatus(@RequestBody UpsStatusRequest model) {
        List<UpsStatusResponse> responses = new ArrayList<>();
        List<IotTsKv> pointDescList = iotTsKvRepository.findAllByPointDescInAndTsBetween(MODEL_KEYS, model.getStartTime(),TimeUtil.getPreDay(model.getEndTime(),1));

        Map<String, List<IotTsKv>> collect = pointDescList.stream().collect(groupingBy(el -> el.getDeviceSn() + TimeUtil.toYmdHHmmStr(el.getTs())));

        collect.keySet().forEach(l->{
            UpsStatusResponse usres = new UpsStatusResponse();
            List<IotTsKv> iotTsKvs = collect.get(l);
            if(iotTsKvs!=null && !iotTsKvs.isEmpty()){
                usres.setTimestamp(TimeUtil.toYmdHHmmStr(iotTsKvs.get(0).getTs()));
                iotTsKvs.forEach(el->{
                    if("battery_current".equals(el.getPointDesc())){
                        usres.setBatteryCurrent(el.getPointValue());
                    }
                    if("line_voltage".equals(el.getPointDesc())){
                        usres.setLineVoltage(el.getPointValue());
                    }
                    if("input_frequency".equals(el.getPointDesc())){
                        usres.setInputfrequency(el.getPointValue());
                    }
                    if("output_frequency".equals(el.getPointDesc())){
                        usres.setOutputfrequency(el.getPointValue());
                    }
                    if("output_voltage".equals(el.getPointDesc())){
                        usres.setOutputVoltage(el.getPointValue());
                    }
                    if("output_current".equals(el.getPointDesc())){
                        usres.setOutputCurrent(el.getPointValue());
                    }
                    if("output_load_percent".equals(el.getPointDesc())){
                        usres.setOutputLoadPercent(el.getPointValue());
                    }
                    if("battery_voltage".equals(el.getPointDesc())){
                        usres.setBatteryVoltage(el.getPointValue());
                    }
                    if("ups_temperature".equals(el.getPointDesc())){
                        usres.setTemperature(el.getPointValue());
                    }
                    if("ups_status".equals(el.getPointDesc())){
                        usres.setStatus(el.getPointValue());
                    }
                    if("battery_capacity".equals(el.getPointDesc())){
                        usres.setBatteryCapacity(el.getPointValue());
                    }
                });
            }

            responses.add(usres);
        });

        List<UpsStatusResponse> sortedList = responses.stream().sorted(Comparator.comparing(UpsStatusResponse::getTimestamp))
                .collect(Collectors.toList());
        return ResponseResult.success(sortedList);
    }

    @ApiOperation("获取储能收益")
    @UserLoginToken
    @RequestMapping(value = "/ups/retrieveEnergyStorageReturns", method = {RequestMethod.POST})
    public ResponseResult<List<RetrieveResponse>> retrieveEnergyStorageReturns(@RequestBody RetrieveRequest model) {
        List<String> allowStorageEnergyNodeIds = userService.getAllowStorageEnergyNodeIds();
        ;
        if(model.getTimeType()==0){ //本月
            List<NodeProfitDayForecasting> allByNodeIdIndAndProfitDateDay = dayForecastingRepository.findAllByNodeIdInAndProfitDateDayBetween(allowStorageEnergyNodeIds, TimeUtil.getMonthStart(new Date()), TimeUtil.getMonthEnd(new Date()));
            List<RetrieveResponse> responses = new ArrayList<>();

            Map<Date, List<NodeProfitDayForecasting>> collect = allByNodeIdIndAndProfitDateDay.stream().collect(groupingBy(NodeProfitDayForecasting::getProfitDateDay));
            collect.keySet().forEach(e->{
                RetrieveResponse real = new RetrieveResponse();

                real.setTime(TimeUtil.toYmdStr(e));
                real.setName("实际收益");

                real.setValue(collect.get(e).stream()
                        .mapToDouble(c -> c.getProfitValue()).sum()+"");


                RetrieveResponse forecast = new RetrieveResponse();
                forecast.setTime(TimeUtil.toYmdStr(e));
                forecast.setName("预测收益");
                forecast.setValue(collect.get(e).stream()
                        .mapToDouble(c -> c.getProfitForecastValue()).sum()+"");

                responses.add(real);
                responses.add(forecast);
            });
            return ResponseResult.success(responses);

        }else if(model.getTimeType() == 1){//本年
            List<NodeProfitMonthForecasting> allByNodeIdIndAndProfitDateMonth = monthForecastingRepository.findAllByNodeIdInAndProfitDateMonthBetween(allowStorageEnergyNodeIds,TimeUtil.getYearStart(new Date()),TimeUtil.getYearEnd(new Date()));
            List<RetrieveResponse> responses = new ArrayList<>();

            Map<Date, List<NodeProfitMonthForecasting>> collect = allByNodeIdIndAndProfitDateMonth.stream().collect(groupingBy(NodeProfitMonthForecasting::getProfitDateMonth));
            collect.keySet().forEach(e->{
                RetrieveResponse real = new RetrieveResponse();

                real.setTime(TimeUtil.toYmStr(e));
                real.setName("实际收益");

                real.setValue(collect.get(e).stream()
                        .mapToDouble(c -> c.getProfitValue()).sum()+"");


                RetrieveResponse forecast = new RetrieveResponse();
                forecast.setTime(TimeUtil.toYmStr(e));
                forecast.setName("预测收益");
                forecast.setValue(collect.get(e).stream()
                        .mapToDouble(c -> c.getProfitForecastValue()).sum()+"");

                responses.add(real);
                responses.add(forecast);
            });
            return ResponseResult.success(responses);

        }else {
            return ResponseResult.error("错误的请求参数");
        }



    }

    @ApiOperation("登录")
    @RequestMapping(value = "login", method = {RequestMethod.POST})
    public ResponseResult<LoginResponse> login(@RequestBody LoginRequest authLogin) {
        try {
            if (authLogin == null) {
                return ResponseResult.error("参数异常，请检查！");
            }
            if (StringUtils.isEmpty(authLogin.getGiccName())) {
                return ResponseResult.error("登陆用户名不能为空！");
            }
            if (StringUtils.isEmpty(authLogin.getGiccPwd())) {
                return ResponseResult.error("密码不能为空！");
            }

            String username = authLogin.getGiccName();
            String passWord = authLogin.getGiccPwd();

            String md5Pwd = IdGenerator.md5Id(passWord);

            User user = userRepository.findUserByUserNameAndUserPassword(username, md5Pwd);

            if (user != null) {

                String token = Md5TokenGenerator.generate(username, passWord);
                String userId = user.getUserId();
                String tokenKey = "TOKEN_EXPIRE_" + userId;

                LoginResponse response = new LoginResponse();
                response.setUserId(userId);
                response.setUsername(user.getUserName());
                response.setToken(token);

                //如果是“记住我”，则Token有效期是7天，反之则是24个小时
                redisUtils.add(tokenKey, token,  24, TimeUnit.HOURS);
                return ResponseResult.success(response);
            }
        } catch (Exception ex) {
            return ResponseResult.error("登陆失败,请检查输入信息是否正确！");
        }
        return ResponseResult.error("登陆失败,请检查输入信息是否正确！");
    }


    @ApiOperation("UPS最新状态")
    @UserLoginToken
    @RequestMapping(value = "/ups/latestUpsStatus", method = {RequestMethod.POST})
    public ResponseResult<LatestStatusResponse> latestUpsStatus() {
        LatestStatusResponse response = new LatestStatusResponse();
        List<IotTsKv> pointDescList = iotTsKvRepository.findAllByPointDescInAndTsBetweenOrderByTsDesc(STATUS_KEYS,
                TimeUtil.stringToDate(TimeUtil.getPreTime(TimeUtil.dateFormat(new Date()),"-60")),
                new Date());
        if(pointDescList == null || pointDescList.isEmpty()){
            response.setTimestamp(TimeUtil.toYmdHHmmStr(new Date()));
            response.setStatus(false);
        }else {
            IotTsKv iotTsKv = pointDescList.get(0);
            response.setStatus("0".equals(iotTsKv.getPointValue())?true:false);
            response.setTimestamp(TimeUtil.toYmdHHmmStr(iotTsKv.getTs()));
        }
        return ResponseResult.success(response);
    }

    @ApiOperation("PUE最新数据")
    @UserLoginToken
    @RequestMapping(value = "/ups/pueValue", method = {RequestMethod.POST})
    public ResponseResult<PueValueResponse> pueValue() {
        PueValueResponse response = new PueValueResponse();
        List<IotTsKv> pointDescList = iotTsKvRepository.findAllByPointDescInAndTsBetweenOrderByTsDesc(PUE_KEYS,
                TimeUtil.stringToDate(TimeUtil.getPreTime(TimeUtil.dateFormat(new Date()),"-60")),
                new Date());
        if(pointDescList == null || pointDescList.isEmpty()){
            response.setTimestamp(TimeUtil.toYmdHHmmStr(new Date()));
            response.setValue(0);
        }else {
            //电压
            String voltage = null;
            //电流
            String current = null;
            // 视在功率
            String apparentPower = null;

            String timeStr = null;
            for (String l : PUE_KEYS) {
                IotTsKv iotTsKv = pointDescList.stream().filter(d -> l.equals(d.getPointDesc())).findFirst().orElse(null);

                if (iotTsKv != null) {
                    if(timeStr == null || "".equals(timeStr.trim())){
                        timeStr = TimeUtil.toYmdHHmmStr(iotTsKv.getTs());
                    }
                    //同一时间采集的数据计算才能得到正确PUE值
                    if (l.contains("voltage") && (timeStr!=null && timeStr.equals(TimeUtil.toYmdHHmmStr(iotTsKv.getTs()))) ) {
                        voltage = iotTsKv.getPointValue();
                    }
                    if (l.contains("current")&& (timeStr!=null && timeStr.equals(TimeUtil.toYmdHHmmStr(iotTsKv.getTs()))) ) {
                        current = iotTsKv.getPointValue();
                    }
                    if (l.contains("ups_load")&& (timeStr!=null && timeStr.equals(TimeUtil.toYmdHHmmStr(iotTsKv.getTs()))) ) {
                        apparentPower = iotTsKv.getPointValue();
                    }
                }
            }
            // PUE=配电单元视在功率/UPS视在功率=（配电单元电流×配电单元电压）/（UPS电流×UPS电压）
            if(voltage!=null && current!=null && apparentPower!=null){
                response.setValue(Double.valueOf(apparentPower)*1000/(Double.valueOf(voltage)*Double.valueOf(current)));
            }
            response.setTimestamp(timeStr);
        }
        return ResponseResult.success(response);
    }
}
