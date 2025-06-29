//package com.example.vvpweb.systemmanagement.energymodel.model.factory;
//
//import com.example.vvpdomain.entity.BiStorageEnergyLog;
//import com.example.vvpdomain.entity.BiStorageEnergyResources;
//import com.example.vvpdomain.entity.CfgStorageEnergyStrategy;
//
//import java.util.List;
//
//public class EnergyStorageFactory {
//
//
//
//    public List<CfgStorageEnergyStrategy> bisListtocfgList(List<CfgStorageEnergyStrategy>cfgList,List<BiStorageEnergyLog> bigList) {
//        return bigList.forEach(v -> {
//            CfgStorageEnergyStrategy cfg = new CfgStorageEnergyStrategy();
//            cfg.setId(v.getId());
//            cfg.setNodeId(v.getNodeId());
////            cfg.setLongitude();
////            cfg.setLatitude();
////            cfg.setSystemId();
//            cfg.setEffectiveDate(v.getTs());
////            cfg.setPriceTag();
//            cfg.setOrder("?");
//            cfg.setTimeFrame();
//            cfg.setSTime();
//            cfg.setETime();
//            cfg.setProperty();
//            cfg.setPriceHour();
//            cfg.setStrategy();
//            cfg.setStrategyForecasting();
//            cfg.setStrategyHour();
//            cfg.setMultiplyingPower();
//            cfg.setCreatedTime();
//            cfg.setUpdateTime();
//            cfgList.add(cfg);
//            cfg.setProperty();
//            cfgList.add(cfg);
//
//        });
//    }
//}
