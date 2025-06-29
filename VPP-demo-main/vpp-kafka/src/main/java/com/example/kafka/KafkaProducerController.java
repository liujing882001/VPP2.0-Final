package com.example.kafka;

import com.example.kafka.service.ProducerService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/v1")
@CrossOrigin
@Api(value = "kafka消息发送", tags = {"kafka消息发送"})
public class KafkaProducerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerController.class);

    private static final String demandResponseInvitation = "demand_response_invitation";
    private static final String demandResponseInvitationResult = "demand_response_invitation_result";
    private static final String demandResponseInvitationCancel = "demand_response_invitation_cancel";
    private static final String demandResponseInvitationStatinfo = "demand_response_invitation_statinfo";
    @Resource
    private ProducerService producerService;


    /**
     * 1、由虚拟电厂平台将数据不定期主动推送至约定kafka对应topic，能源平台消费该topic
     * 2、每条信息为数组形式，最多2000
     * 3、如果虚拟电厂平台需要更新该需求响应的时间段，可在响应日期前重复发送，相同需求唯一标识（即需求编码）能源平台则认为需要更新该需求信息
     * 4、如果虚拟电厂平台需要取消该需求响应则可将"取消标识"赋值"1"
     */
    @RequestMapping(value = "/demandResponseInvitation", method = {RequestMethod.POST})
    public void demandResponseInvitation(@RequestBody String msg) {
        LOGGER.info("[发送消息信息] topic:{}, data:{}", demandResponseInvitation, msg);
        producerService.sendMessage(demandResponseInvitation, msg);
    }

    /**
     * 交互序号3、发布邀约响应结果接口（Topic）名称
     * demand_response_invitation_result
     * <p>
     * 1、虚拟电厂平台收到能源平台的邀约响应后，将邀约中标情况推送至约定kafka对应topic，
     * 能源平台消费该topic
     */
    @RequestMapping(value = "/demandResponseInvitationResult", method = {RequestMethod.POST})
    public void demandResponseInvitationResult(@RequestBody String msg) {
        LOGGER.info("[发送消息信息] topic:{}, data:{}", demandResponseInvitationResult, msg);
        producerService.sendMessage(demandResponseInvitationResult, msg);
    }

    /**
     * 交互序号4、发布取消邀约接口（Topic）名称 预留接口
     * demand_response_invitation_cancel
     * <p>
     * 1、虚拟电厂平台需要取消邀约时，将信息推送至约定kafka对应topic，
     * 能源平台消费该topic
     */
    @RequestMapping(value = "/demandResponseInvitationCancel", method = {RequestMethod.POST})
    public void demandResponseInvitationCancel(@RequestBody String msg) {
        LOGGER.info("[发送消息信息] topic:{}, data:{}", demandResponseInvitationCancel, msg);
        producerService.sendMessage(demandResponseInvitationCancel, msg);
    }

    /**
     * 交互序号5、实际已响应结果统计（Topic）名称
     * demand_response_invitation_statinfo
     * <p>
     * 1、虚拟电厂平台在响应完成后，统计实际响应结果，将信息推送至约定kafka对应topic，
     * 能源平台消费该topic
     */
    @RequestMapping(value = "/demandResponseInvitationStatinfo", method = {RequestMethod.POST})
    public void demandResponseInvitationStatinfo(@RequestBody String msg) {
        LOGGER.info("[发送消息信息] topic:{}, data:{}", demandResponseInvitationStatinfo, msg);
        producerService.sendMessage(demandResponseInvitationStatinfo, msg);
    }

}
