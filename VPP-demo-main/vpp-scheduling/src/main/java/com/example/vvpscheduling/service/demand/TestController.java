package com.example.vvpscheduling.service.demand;

import com.example.vvpscheduling.service.demand.service.IDemandRespTaskService;
import com.example.vvpservice.controlservice.DeviceControlImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Resource
    IDemandRespTaskService iDemandRespTaskService;
//
    @GetMapping("/test")
    public String runTest(@RequestParam("respType") Integer respType,
                          @RequestParam("dStatus") Integer dStatus,
                          @RequestParam("respId") String respId) {
        test(respType,dStatus,respId);
        return "Test method executed.";
    }

    @GetMapping("/test2")
    public String testtt(@RequestParam("respType") Integer respType,
                          @RequestParam("dStatus") Integer dStatus,
                          @RequestParam("respId") String respId) {
        test2(respType,dStatus,respId);
        return "Test method executed.";
    }


    public void test(Integer respType,Integer dStatus,String respId) {
        log.info("开始测试");
        iDemandRespTaskService.initRespTask(respId);
//        deviceControl.sendDeviceControlCommandMessageDemand(Arrays.asList("zhinengtuijian"),respType,dStatus,respId);
    }
    public void test2(Integer respType,Integer dStatus,String respId) {
        log.info("开始测试");
        iDemandRespTaskService.cancelRespTask(respId);
//        deviceControl.sendDeviceControlCommandMessageDemand(Arrays.asList("zhinengtuijian"),respType,dStatus,respId);
    }
}
