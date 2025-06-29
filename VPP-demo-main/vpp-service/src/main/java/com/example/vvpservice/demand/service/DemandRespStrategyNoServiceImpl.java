package com.example.vvpservice.demand.service;

import com.alibaba.fastjson.JSON;
import com.example.vvpservice.demand.model.DemandRespStrategyNoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class DemandRespStrategyNoServiceImpl implements DemandRespStrategyNoService {

    private static Logger logger = LoggerFactory.getLogger(DemandRespStrategyNoServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private String toString(Object object) {
        return object == null ? "" : object.toString();
    }

    private Integer toInt(Object object) {
        return object == null ? null : Integer.valueOf(object.toString());
    }

    @Override
    @Transactional
    public void batchInsert(List<DemandRespStrategyNoModel> list) {
        StringBuilder insert = new StringBuilder("INSERT INTO demand_resp_strategy_no (drs_id," +
                "s_id,no_households,drs_status,declare_load,node_name,node_id,is_platform,resp_id) VALUES ");
        for (int i = 0; i < list.size(); i++) {
            DemandRespStrategyNoModel model = list.get(i);
            insert.append("('").append(model.getDrsId()).append("','").append(model.getSId()).append("','").
                    append(model.getNoHouseholds()).append("',").append(model.getDrsStatus())
                    .append(",").append(model.getDeclareLoad()).append(",'").append(model.getNodeName())
                    .append("','").append(model.getNodeId()).append("',").append(model.getIsPlatform())
                    .append(",'").append(model.getRespId()).append("')");
            if (i < list.size() - 1) {
                insert.append(",");
            }
        }
        insert.append(" ON CONFLICT (drs_id) ");
        insert.append(" DO UPDATE SET s_id = EXCLUDED.s_id, no_households=EXCLUDED.no_households,drs_status=EXCLUDED.drs_status," +
                "declare_load=EXCLUDED.declare_load,node_name=EXCLUDED.node_name,node_id=EXCLUDED.node_id," +
                "is_platform=EXCLUDED.is_platform,resp_id=EXCLUDED.resp_id");
        logger.info("批量添加中间表的SQL语句:{}", JSON.toJSON(insert));
        try {
            entityManager.createNativeQuery(insert.toString()).executeUpdate();
            ;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("需求响应户号详情表出错了！" + e.getMessage());
        }

    }
}
