package com.example.vvpscheduling;

import com.example.vvpcommom.EntityUtils;
import com.example.vvpcommom.Enum.NodePostTypeEnum;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.BiPvResourcesRepository;
import com.example.vvpdomain.IotTsKvMeteringDevice96Repository;
import com.example.vvpdomain.NodeInfoViewRepository;
import com.example.vvpdomain.entity.BiPvResources;
import com.example.vvpdomain.entity.IotTsKvMeteringDevice96;
import com.example.vvpdomain.view.NodeInfoView;
import com.example.vvpscheduling.model.LastSERInfoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.OptionalDouble;
import java.util.TimeZone;

/**
 * 负荷管理-光伏资源
 */
@Component("pvResourcesJob")
@EnableAsync
public class PVResourcesJob {

    private static Logger logger = LoggerFactory.getLogger(PVResourcesJob.class);

    @Resource
    IotTsKvMeteringDevice96Repository device96Repository;
    @Resource
    BiPvResourcesRepository biPvResourcesRepository;
    @Resource
    NodeInfoViewRepository nodeInfoViewRepository;

    @Scheduled(initialDelay = 1000 * 5, fixedDelay = 60 * 1000)
    @Async
    public void initPvStationInfo() {
        try {
            SimpleDateFormat fmt_ymd_hds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            fmt_ymd_hds.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
            fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            biPvResourcesRepository.deleteAllInvalidNode();

            List<NodeInfoView> pvNodeInfoViews = nodeInfoViewRepository.findAllByNodePostType(NodePostTypeEnum.pv.getNodePostType());
            if (pvNodeInfoViews != null && pvNodeInfoViews.size() > 0) {

                for (NodeInfoView node : pvNodeInfoViews) {
                    try {
                        String nodeId = node.getNodeId();
                        String nodeName = node.getNodeName();
                        //在线状态
                        boolean online = node.getOnline();
                        //建设中 已完成，默认false 为建设中
                        boolean isEnabled = node.getIsEnabled();
                        //光伏装机容量
                        double pvCapacity = node.getCapacity();
                        //瞬时功率
                        double load = 0;
                        //累计电量
                        double energy = 0;
                        //当日发电量
                        double now_energy = 0;
                        Date ts = TimeUtil.getStartOfDay(new Date());


                        List<Object[]> LastNodeInfo = device96Repository.findLastNodeInfo(nodeId);
                        if (LastNodeInfo != null && LastNodeInfo.size() > 0) {
                            List<LastSERInfoModel> model = EntityUtils.castEntity(LastNodeInfo, LastSERInfoModel.class, new LastSERInfoModel());
                            if (model != null && model.size() > 0) {
                                load = model.get(0).getLoad();
                                energy = model.get(0).getTotal_power_energy();
                                ts = model.get(0).getCount_data_time();
                            }
                        }
                        List<IotTsKvMeteringDevice96> device96s = device96Repository.findAllByNodeIdAndSystemIdAndConfigKeyAndPointDescAndCountDate(nodeId
                                , "nengyuanzongbiao"
                                , "metering_device"
                                , "energy"
                                , fmt_ymd.parse(fmt_ymd.format(ts)));


                        if (device96s != null && device96s.size() > 0) {
                            now_energy = device96s.stream().mapToDouble(c -> c.getHTotalUse()).sum();

                            OptionalDouble optionalMax = device96s.stream().mapToDouble(d -> d.getTotalPowerEnergy()).max();
                            energy = optionalMax.isPresent() ? optionalMax.getAsDouble() : null;
                        }

                        BiPvResources resources = new BiPvResources();
                        resources.setId(nodeId);
                        resources.setNodeId(nodeId);
                        resources.setNodeName(nodeName);
                        resources.setCapacity(pvCapacity);
                        resources.setLoad(load);
                        resources.setEnergy(energy);
                        resources.setNowEnergy(now_energy);
                        resources.setOnline(online);
                        resources.setIsEnabled(isEnabled);
                        resources.setTs(ts.getTime() >= (new Date()).getTime() ? new Date() : ts);
                        biPvResourcesRepository.save(resources);
                    } catch (Exception ex) {
                    }
                }
            }
        } catch (Exception ex) {
        }
    }
}
