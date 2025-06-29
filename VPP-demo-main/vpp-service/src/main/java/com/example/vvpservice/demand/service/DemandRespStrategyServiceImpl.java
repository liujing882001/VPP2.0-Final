package com.example.vvpservice.demand.service;

import com.alibaba.fastjson.JSON;
import com.example.vvpservice.demand.model.DemandRespStrategyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class DemandRespStrategyServiceImpl implements DemandRespStrategyService {

    private static Logger logger = LoggerFactory.getLogger(DemandRespStrategyServiceImpl.class);

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
    public boolean batchInsert(List<DemandRespStrategyModel> list) {
        StringBuilder insert = new StringBuilder("INSERT INTO demand_resp_strategy(s_id,resp_id,strategy_id,create_by) VALUES ");
        for (int i = 0; i < list.size(); i++) {
            DemandRespStrategyModel model = list.get(i);
            insert.append("('").append(model.getSId()).append("','").append(model.getRespId()).append("','").
                    append(model.getStrategyId()).append("','").append(model.getCreateBy()).append("')");
            if (i < list.size() - 1) {
                insert.append(",");
            }
        }
        insert.append(" ON CONFLICT (s_id) ");
        insert.append(" DO UPDATE SET resp_id = EXCLUDED.resp_id, strategy_id=EXCLUDED.strategy_id,create_by=EXCLUDED.create_by" );
        logger.info("批量添加中间表的SQL语句:{}", JSON.toJSON(insert));
        try {
            entityManager.createNativeQuery(insert.toString()).executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("需求响应中间表出错了！" + e.getMessage());
        }
        return true;
    }
}
