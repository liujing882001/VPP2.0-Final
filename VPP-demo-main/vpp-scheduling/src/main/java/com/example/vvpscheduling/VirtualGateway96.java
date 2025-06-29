package com.example.vvpscheduling;
import com.alibaba.fastjson.JSON;
import com.example.vvpcommom.Enum.SysParamEnum;
import com.example.vvpcommom.HttpUtil;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpcommom.StringUtils;
import com.example.vvpdomain.SysParamRepository;
import com.example.vvpdomain.entity.SysParam;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.tunableload.ITunableLoadService;
import com.example.vvpservice.tunableload.model.RTLoadModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * sk_vpp在线心跳 厦门移动发送心跳
 * */
@Component("virtualGateway96")
@EnableAsync
public class VirtualGateway96 implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualGateway96.class);

    @Resource
    private SysParamRepository sysParamRepository;


   // @Scheduled(initialDelay = 1000 * 5, fixedDelay = 60 * 1000* 5)
   // @Async
    public void heartbeat() {
        try {
            SysParam sysParam = sysParamRepository.findSysParamBySysParamKey(SysParamEnum.DemandResponse.getId());
            if (sysParam != null
                    && StringUtils.isEmpty(sysParam.getSysParamValue())==false) {
                String url = sysParam.getSysParamValue()+"/v1/heartbeat";
                HttpUtil.okHttpPost(url,"");
            }
        } catch (Exception ex) {
            System.out.println("vpp Exception = " + ex.getMessage());
        }
    }
}
