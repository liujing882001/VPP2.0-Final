package com.example.vvpservice.carbon.service;

import com.example.vvpdomain.entity.CaCollectionModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Service
public class CaCollectionModelServiceImpl implements CaCollectionModelService {

    private static Logger logger = LoggerFactory.getLogger(CaCollectionModelServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Double getEmissionDataCountDay(String nodeId, Integer scopeType, String startTime, String endTime) {
        StringBuilder sqlBuilder = new StringBuilder("select COALESCE(sum(COALESCE(cs.discharge_value::::float,0)*COALESCE(cef.co2,0)),0) " +
                " from (select n.province,cs.* from node n,ca_scope cs  where " +
                " cs.node_id = n.node_id and cs.s_status=1 ");
        if (StringUtils.isNotBlank(nodeId)) {
            sqlBuilder.append(" and n.node_id='" + nodeId + "'");//节点SQL
        }
        if (scopeType != null) {
            sqlBuilder.append(" and cs.scope_type=" + scopeType);//范围类型
        }
        if (StringUtils.isNotBlank(startTime)) {
            sqlBuilder.append(" and cs.ca_year_month >='" + startTime + "'");//天的SQL
        }
        if (StringUtils.isNotBlank(endTime)) {
            sqlBuilder.append(" and cs.ca_year_month <='" + endTime + "'");//月的SQL
        }
        sqlBuilder.append(" ) cs " +
                " LEFT JOIN ca_emission_factor cef " +
                " on cs.province=cef.province  " +
                " and cs.scope_type=cef.scope_type " +
                " and cef.s_status=1  ");
        if (scopeType != null) {
            if (scopeType == 1) {
                sqlBuilder.append(" and cs.discharge_entity = cef.emission_factor_num");//范围一排放实体 对应 排放因子编号
            } else if (scopeType == 2) {
                sqlBuilder.append(" and cs.scope_two = cef.emission_factor_num");//范围二购买内容 对应 排放因子编号
            } else if (scopeType == 3) {
                sqlBuilder.append(" and cs.scope_three = cef.emission_factor_num");//范围三类型 对应 排放因子编号
            }
        }

        Object object = entityManager.createNativeQuery(sqlBuilder.toString()).getSingleResult();
        return object == null ? 0.0 : Double.parseDouble(object.toString());
    }

    @Override
    public Double getEmissionDataCountYM(String nodeId, Integer scopeType, String startTime, String endTime) {
        StringBuilder sqlBuilder = new StringBuilder("select COALESCE(sum(COALESCE(cs.discharge_value::::float,0)*COALESCE(cef.co2,0)),0) " +
                " from (select n.province,cs.* from node n,ca_scope cs  where " +
                " cs.node_id = n.node_id and cs.s_status=1 ");
        if (StringUtils.isNotBlank(nodeId)) {
            sqlBuilder.append(" and n.node_id='" + nodeId + "'");//节点SQL
        }
        if (scopeType != null) {
            sqlBuilder.append(" and cs.scope_type=" + scopeType);//范围类型
        }
        if (StringUtils.isNotBlank(startTime)) {
            sqlBuilder.append(" and cs.ca_year_month >='" + startTime + "'");//天的SQL
        }
        if (StringUtils.isNotBlank(endTime)) {
            sqlBuilder.append(" and cs.ca_year_month <='" + endTime + "'");//月的SQL
        }
        sqlBuilder.append(" ) cs " +
                " LEFT JOIN ca_emission_factor cef " +
                " on cs.province=cef.province  " +
                " and cs.scope_type=cef.scope_type " +
                " and cef.s_status=1  ");
        if (scopeType != null) {
            if (scopeType == 1) {
                sqlBuilder.append(" and cs.discharge_entity = cef.emission_factor_num");//范围一排放实体 对应 排放因子编号
            } else if (scopeType == 2) {
                sqlBuilder.append(" and cs.scope_two = cef.emission_factor_num");//范围二购买内容 对应 排放因子编号
            } else if (scopeType == 3) {
                sqlBuilder.append(" and cs.scope_three = cef.emission_factor_num");//范围三类型 对应 排放因子编号
            }
        }

        Object object = entityManager.createNativeQuery(sqlBuilder.toString()).getSingleResult();
        return object == null ? 0.0 : Double.parseDouble(object.toString());
    }

    @Override
    public List<Object[]> getDisplacementAnalysis(String nodeId, Integer scopeType, String startTime, String endTime) {
        StringBuilder sqlBuilder = new StringBuilder("select cs.ca_year_month,COALESCE(sum(COALESCE(cs.discharge_value::::float,0)*COALESCE(cef.co2,0)),0) as dischargeValue  " +
                " from (select n.province,cs.* from node n,ca_scope cs  where " +
                " cs.node_id = n.node_id and cs.s_status=1 ");
        if (StringUtils.isNotBlank(nodeId)) {
            sqlBuilder.append(" and n.node_id='" + nodeId + "'");//节点SQL
        }
        if (scopeType != null) {
            sqlBuilder.append(" and cs.scope_type=" + scopeType);//范围类型
        }
        if (StringUtils.isNotBlank(startTime)) {
            sqlBuilder.append(" and cs.ca_year_month >='" + startTime + "'");//天的SQL
        }
        if (StringUtils.isNotBlank(endTime)) {
            sqlBuilder.append(" and cs.ca_year_month <='" + endTime + "'");//月的SQL
        }
        sqlBuilder.append(" ) cs LEFT JOIN " +
                " ca_emission_factor cef " +
                " on cs.scope_type=cef.scope_type " +
                " and cs.province=cef.province " +
                " and cef.s_status=1 ");
        if (scopeType != null) {
            if (scopeType == 1) {
                sqlBuilder.append(" and cs.discharge_entity = cef.emission_factor_num");//范围一排放实体 对应 排放因子编号
            } else if (scopeType == 2) {
                sqlBuilder.append(" and cs.scope_two = cef.emission_factor_num");//范围二购买内容 对应 排放因子编号
            } else if (scopeType == 3) {
                sqlBuilder.append(" and cs.scope_three = cef.emission_factor_num");//范围三类型 对应 排放因子编号
            }
        }
        sqlBuilder.append(" group by ca_year_month");
        List<Object[]> object = entityManager.createNativeQuery(sqlBuilder.toString()).getResultList();
        return object;
    }

    @Override
    public Double getTradeDataCount(String nodeId, Integer tradeType, Integer greenType, String startTime, String endTime) {
        StringBuilder sqlBuilder = new StringBuilder("select COALESCE(sum(trading_volume),0) from ca_trade where t_status=1 ");
        if (StringUtils.isNotBlank(nodeId)) {
            sqlBuilder.append(" and node_id='" + nodeId + "'");//节点SQL
        }
        if (tradeType != null) {
            sqlBuilder.append(" and trade_type=" + tradeType);
        }
        if (greenType != null) {
            sqlBuilder.append(" and green_type=" + greenType);
        }
        if (StringUtils.isNotBlank(startTime)) {
            sqlBuilder.append(" and TO_CHAR(trade_date, 'YYYY-MM')>='" + startTime + "'");//天的SQL
        }
        if (StringUtils.isNotBlank(endTime)) {
            sqlBuilder.append(" and TO_CHAR(trade_date, 'YYYY-MM')<='" + endTime + "'");//月的SQL
        }

        Object object = entityManager.createNativeQuery(sqlBuilder.toString()).getSingleResult();
        return object == null ? 0.0 : Double.parseDouble(object.toString());
    }

    @Override
    public List<CaCollectionModel> getCaCollectionModelList(String nodeId, Integer scopeType) {
        StringBuilder sqlBuilder = new StringBuilder("select ccm.collection_model_id,cef.emission_factor_num,cef.emission_factor_name,cef.scope_type,ccm.collect_mode," +
                "ccm.system_id,ccm.system_name,ccm.device_id,ccm.device_name,ccm.data_point_id,ccm.data_point_name,ccm.node_id,ccm.created_time,ccm.update_time,ccm.s_status " +
                "from (select n.node_id,cef.* from ca_emission_factor cef , node n where n.province = cef.province " +
                " and cef.s_status=1 ");
        if (StringUtils.isNotBlank(nodeId)) {
            sqlBuilder.append(" and n.node_id='" + nodeId + "'");//节点SQL
        }
        if (scopeType != null) {
            sqlBuilder.append(" and cef.scope_type=" + scopeType);
        }
        sqlBuilder.append(") cef " +
                " left join ca_collection_model ccm on cef.emission_factor_name=ccm.emission_factor_name " +
                "and cef.node_id = ccm.node_id " +
                "and (ccm.s_status=1 or ccm.s_status is null) ");
        List<Object[]> objects = entityManager.createNativeQuery(sqlBuilder.toString()).getResultList();
        List<CaCollectionModel> caCollectionModels = new ArrayList<>();
        for (Object[] object : objects) {
            CaCollectionModel ccm = new CaCollectionModel();
            ccm.setCollectionModelId(toString(object[0]));
            ccm.setEmissionFactorNum(toInt(object[1]));
            ccm.setEmissionFactorName(toString(object[2]));
            ccm.setScopeType(toInt(object[3]));
            ccm.setCollectMode(toInt(object[4]));
            ccm.setSystemId(toString(object[5]));
            ccm.setSystemName(toString(object[6]));
            ccm.setDeviceId(toString(object[7]));
            ccm.setDeviceName(toString(object[8]));
            ccm.setDataPointId(toString(object[9]));
            ccm.setDataPointName(toString(object[10]));
            ccm.setNodeId(toString(object[11]));
            caCollectionModels.add(ccm);
        }
        return caCollectionModels;
    }

    @Override
    public Object[] getSinkConfCount(String nodeId, String cType, String emissionFactorName, String startTime, String endTime) {
        StringBuilder sqlBuilder = new StringBuilder("select COALESCE(sum(cs.attr_num),0) as attr_num,COALESCE(sum(COALESCE(cs.attr_num,0)*COALESCE(cef.co2,0)),0) as countSum  " +
                " from (select n.province,cs.* from node n,ca_sink_conf cs " +
                " where cs.node_id = n.node_id");
        if (StringUtils.isNotBlank(nodeId)) {
            sqlBuilder.append(" and n.node_id='" + nodeId + "'");//节点SQL
        }
        if (StringUtils.isNotBlank(cType)) {
            sqlBuilder.append(" and cs.c_type='" + cType + "'");
        }
        if (StringUtils.isNotBlank(startTime)) {
            sqlBuilder.append(" and TO_CHAR(cs.add_time, 'YYYY-MM')>='" + startTime + "'");//天的SQL
        }
        if (StringUtils.isNotBlank(endTime)) {
            sqlBuilder.append(" and TO_CHAR(cs.add_time, 'YYYY-MM')<='" + endTime + "'");//月的SQL
        }
        sqlBuilder.append(" ) cs" +
                " left join ca_emission_factor cef " +
                " on cs.province=cef.province " +
                " and cef.s_status=1  ");
        if (StringUtils.isNotBlank(emissionFactorName)) {
            sqlBuilder.append(" and cef.emission_factor_name='" + emissionFactorName + "'");
        }

        Object[] object = (Object[]) entityManager.createNativeQuery(sqlBuilder.toString()).getSingleResult();
        return object;
    }

    @Override
    public List<Object[]> getReportCount(String nodeId, Integer scopeType, Integer dischargeType, String year, String ziDuan) {
        StringBuilder sqlBuilder = new StringBuilder("select " + ziDuan + "COALESCE(sum(COALESCE(cs.discharge_value::::float,0)*COALESCE(cef.co2,0)),0) from ca_scope cs " +
                "join ca_emission_factor cef on cs.scope_type=cef.scope_type  " +
                "join node n on n.province=cef.province where cs.s_status=1 and cef.s_status=1 ");
        if (StringUtils.isNotBlank(nodeId)) {
            sqlBuilder.append(" and n.node_id='" + nodeId + "'");//节点SQL
        }
        if (scopeType != null) {
            sqlBuilder.append(" and cs.scope_type=" + scopeType);//节点SQL
        }
        if (dischargeType != null) {
            sqlBuilder.append(" and cs.discharge_type=" + dischargeType);//节点SQL
        }
        if (StringUtils.isNotBlank(year)) {
            sqlBuilder.append(" and cs.ca_year=" + year);//天的SQL
        }
        if (StringUtils.isNotBlank(ziDuan)) {
            sqlBuilder.append(" group by " + ziDuan.substring(0, ziDuan.length() - 1));
        }
        List<Object[]> object = entityManager.createNativeQuery(sqlBuilder.toString()).getResultList();
        return object;
    }

    @Override
    public List<Object[]> getCaFactorList(String nodeId, String[] factorNames) {
        StringBuilder sqlBuilder = new StringBuilder("select cef.emission_factor_name,cef.co2,cef.unit " +
                " from node n,ca_emission_factor cef" +
                " where n.province=cef.province " +
                " and cef.s_status=1  ");
        if (StringUtils.isNotBlank(nodeId)) {
            sqlBuilder.append(" and n.node_id='" + nodeId + "'");//节点SQL
        }
        if (factorNames.length > 0) {
            sqlBuilder.append(" and cef.emission_factor_name in( ");//碳因子名称
            for (int i = 0; i < factorNames.length; i++) {
                sqlBuilder.append("'" + factorNames[i] + "'");
                if (i < factorNames.length - 1) {
                    sqlBuilder.append(",");
                }
            }
            sqlBuilder.append(" ) ");//碳因子名称
        }
        return entityManager.createNativeQuery(sqlBuilder.toString()).getResultList();
    }

    private String toString(Object object) {
        return object == null ? "" : object.toString();
    }

    private Integer toInt(Object object) {
        return object == null ? null : Integer.valueOf(object.toString());
    }
}
