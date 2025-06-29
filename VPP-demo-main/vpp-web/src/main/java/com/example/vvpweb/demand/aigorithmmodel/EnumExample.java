package com.example.vvpweb.demand.aigorithmmodel;


import lombok.Data;

@Data
public class EnumExample {
//    深圳泰伦广场A座 node1    176c0991f24e30c2b25a9dbf1185b7b9
//    深圳泰伦广场B座 node2    5eb413037ba16ea6108c12e0d6353be3
//    深圳某工业设备新材料股份有限公司 node3    3da72e052a0b48759b0f4633df42235a
    public enum Weights {
        nodeOne("{\"weather\":5.79070186,\"ch_water_outlet_temperature\":-2.11031409,\"bias\":-9.877451716013667}"),
        nodeTwo("{\"weather\":3.86046791,\"ch_water_outlet_temperature\":-1.40687606,\"bias\":-6.584967810675778}"),
        nodeThree("{\"weather\":11.58140372,\"ch_water_outlet_temperature\":-4.22062818,\"bias\":-19.754903432027334}");

        private final String jsonValue;

        Weights(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        public static String getNode(String value) {
            switch (value) {
                case "176c0991f24e30c2b25a9dbf1185b7b9":
                    return Weights.nodeOne.jsonValue;
                case "5eb413037ba16ea6108c12e0d6353be3":
                    return Weights.nodeTwo.jsonValue;
                case "3da72e052a0b48759b0f4633df42235a":
                    return Weights.nodeThree.jsonValue;
                // 添加其他节点的情况
                default:
                    return Weights.nodeOne.jsonValue;
            }
        }
    }
//    public static void main(String[] args) {
//        System.out.println("解析后的JsonNode为: " + EnumExample.Weights.getNode("176c0991f24e30c2b25a9dbf1185b7b9"));
//    }
}

