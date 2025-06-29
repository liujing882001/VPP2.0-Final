package com.example.vvpweb.tradepower.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;


@Component
@Data
public class TradeEnvironmentConfig {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private String aireqNode;
    private String loadNode1;
    private String energyNode1;
    private String energyNode2;
    private String pvNode1;
    private String masterNode;
    private List<String> genListLoadNode;
    private Map<String, String> nodeMap;
    private String station;
    private String energyStoragePredictionLoadNode;
    private String energyStoragePredictionEnergyNode;
    private String findAIStorageEnergystrategy;
    private String energyStoragePredictionPvNode;
    private Integer declareForOperationNum;


    @PostConstruct
    public void init() {
        switch (activeProfile) {
            case "resDemo":
            case "china":
                // 演示环境配置
                this.aireqNode = "c20a1ecb5d33539e5334ad85af822252";
                this.loadNode1 = "e238bb37143b82082f695bb5c9cb438f";
                this.energyNode1 = "c20a1ecb5d33539e5334ad85af822252";
                this.energyNode2 = "96a1a8c51194b433025bc8fb677de785";
                this.pvNode1 = "5cfd76f998dbcf8d7e214187d0e30ac5";
                this.masterNode = "e238bb37143b82082f695bb5c9cb438f";
                this.genListLoadNode = Arrays.asList("e238bb37143b82082f695bb5c9cb438f", "38c9a70b255900044a85838900214aec");

                Map<String, String> demoNodeMap = new HashMap<>();
                demoNodeMap.put("e238bb37143b82082f695bb5c9cb438f", "某某商业大楼");
                demoNodeMap.put("38c9a70b255900044a85838900214aec", "某某中心A栋");
                this.nodeMap = Collections.unmodifiableMap(demoNodeMap);

                this.station = "某某商业大楼、某某中心A栋";
                this.energyStoragePredictionLoadNode = "38c9a70b255900044a85838900214aec,e238bb37143b82082f695bb5c9cb438f";
                this.energyStoragePredictionEnergyNode = "c20a1ecb5d33539e5334ad85af822252,96a1a8c51194b433025bc8fb677de785";
                this.findAIStorageEnergystrategy = "c20a1ecb5d33539e5334ad85af822252,96a1a8c51194b433025bc8fb677de785," +
                        "c0ff9f529001e03d80010fb473b655b6,56d470ae53931a3c3eea60d5f237be0d," +
                        "d088991e894c0ddacf3602106e38ea09,5728e4d3bdb13e8a107a365e252ae5e2,a61b5dbc3327f502c02ce44fcaf063d3,f79237fc44855884911d8136ba431f5c,f7a388e48987a8003245d4c7028fed70" +
                        ",919a98f00fe071d49c40d310fbc2fe98,cb94e7b07bdea52552cb463167e9d0b4,fe68ff1b88d2bf965e89486b65246249,e8cd2ba5b8ecf6247a9f9842f23a6753";
                this.energyStoragePredictionPvNode = "5cfd76f998dbcf8d7e214187d0e30ac5";
                this.declareForOperationNum = 2;
                break;
            case "resLe":
                // 产投环境配置
                this.aireqNode = "c20a1ecb5d33539e5334ad85af822252";
                this.loadNode1 = "e238bb37143b82082f695bb5c9cb438f";
                this.energyNode1 = "c20a1ecb5d33539e5334ad85af822252";
                this.energyNode2 = "96a1a8c51194b433025bc8fb677de785";
                this.pvNode1 = "bb05b2b6d467846b9ea2b68de14c6f70";
                this.masterNode = "e238bb37143b82082f695bb5c9cb438f";
                this.genListLoadNode = Arrays.asList("e238bb37143b82082f695bb5c9cb438f");

                Map<String, String> leNodeMap = new HashMap<>();
                leNodeMap.put("e238bb37143b82082f695bb5c9cb438f", "长乐产投大楼");
                this.nodeMap = Collections.unmodifiableMap(leNodeMap);

                this.station = "长乐产投大楼";
                this.energyStoragePredictionLoadNode = "e238bb37143b82082f695bb5c9cb438f";
                this.energyStoragePredictionEnergyNode = "c20a1ecb5d33539e5334ad85af822252,96a1a8c51194b433025bc8fb677de785,f79237fc44855884911d8136ba431f5c,f7a388e48987a8003245d4c7028fed70";
                this.findAIStorageEnergystrategy = "c20a1ecb5d33539e5334ad85af822252,96a1a8c51194b433025bc8fb677de785,f79237fc44855884911d8136ba431f5c,f7a388e48987a8003245d4c7028fed70";
                this.energyStoragePredictionPvNode = "bb05b2b6d467846b9ea2b68de14c6f70";
                this.declareForOperationNum = 1;
                break;
            default:
                // 默认（测试）环境配置
                this.aireqNode = "e4653aad857c96f4c2ea4fd044bffbea";
                this.loadNode1 = "e238bb37143b82082f695bb5c9cb438f";
                this.energyNode1 = "e4653aad857c96f4c2ea4fd044bffbea";
                this.energyNode2 = "07c3c82df1dd93e9c303644eb79985cb";
                this.pvNode1 = "bb05b2b6d467846b9ea2b68de14c6f70";
                this.masterNode = "e238bb37143b82082f695bb5c9cb438f";
                this.genListLoadNode = Arrays.asList("e238bb37143b82082f695bb5c9cb438f", "38c9a70b255900044a85838900214aec");

                Map<String, String> testNodeMap = new HashMap<>();
                testNodeMap.put("e238bb37143b82082f695bb5c9cb438f", "长乐产投大楼");
                testNodeMap.put("38c9a70b255900044a85838900214aec", "联想后海中心A栋");
                this.nodeMap = Collections.unmodifiableMap(testNodeMap);

                this.station = "长乐产投大楼、联想后海中心A栋";
                this.energyStoragePredictionLoadNode = "38c9a70b255900044a85838900214aec,e238bb37143b82082f695bb5c9cb438f";
                this.energyStoragePredictionEnergyNode = "e4653aad857c96f4c2ea4fd044bffbea,07c3c82df1dd93e9c303644eb79985cb,f79237fc44855884911d8136ba431f5c,f7a388e48987a8003245d4c7028fed70";
                this.findAIStorageEnergystrategy = "e4653aad857c96f4c2ea4fd044bffbea,07c3c82df1dd93e9c303644eb79985cb,f79237fc44855884911d8136ba431f5c,f7a388e48987a8003245d4c7028fed70";
                this.energyStoragePredictionPvNode = "bb05b2b6d467846b9ea2b68de14c6f70";
                this.declareForOperationNum = 2;
                break;
        }
    }
}
