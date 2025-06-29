package com.example.vvpservice.ai.service;

import com.alibaba.fastjson.JSON;
import com.example.vvpdomain.entity.AiLoadForecasting;
import com.example.vvpservice.demand.model.DemandRespStrategyNoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

@Service
public class AiLoadForecastingServiceImpl implements AiLoadForecastingService {

    private static Logger logger = LoggerFactory.getLogger(AiLoadForecastingServiceImpl.class);

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
    public void batchInsertOrUpdate(List<AiLoadForecasting> list) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdfDate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        StringBuilder insert = new StringBuilder("INSERT INTO ai_load_forecasting(id, node_id, system_id,count_data_time,baseline_load_value_other) VALUES ");
        for (int i = 0; i < list.size(); i++) {
            AiLoadForecasting model = list.get(i);
            insert.append("('").append(model.getId()).append("','").append(model.getNodeId()).append("','").
                    append(model.getSystemId()).append("','").append(sdfDate.format(model.getCountDataTime()))
                    .append("','").append(model.getBaselineLoadValueOther()).append("')");
            if (i < list.size() - 1) {
                insert.append(",");
            }
        }
        insert.append(" ON conflict(id) DO UPDATE SET baseline_load_value_other =excluded.baseline_load_value_other where ai_load_forecasting.baseline_load_value_other is distinct from excluded.baseline_load_value_other");
        logger.info("批量添加更新AiLoadForecasting表的SQL语句:{}", JSON.toJSON(insert));
        try {
            entityManager.createNativeQuery(insert.toString()).executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("批量添加更新AiLoadForecasting表出错了！" + e.getMessage());
        }

    }
}
