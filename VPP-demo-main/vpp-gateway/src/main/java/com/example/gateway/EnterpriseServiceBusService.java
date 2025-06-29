package com.example.gateway;

public interface EnterpriseServiceBusService {


     /**
      * key  分配的key
      * topic 消息topic
      * msg  消息内容
      * */
     boolean eventBusToESB(String key,String topic,String msg);
}
