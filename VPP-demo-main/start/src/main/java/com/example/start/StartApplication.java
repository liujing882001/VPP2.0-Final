package com.example.start;

import com.thebeastshop.forest.springboot.annotation.ForestScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.TimeZone;


@EnableSwagger2
@EnableScheduling
//时间自动创建
@EnableJpaAuditing
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan(value = {"com.example.start",
        "com.example.vvpdomain",
        "com.example.vvpcommom",
        "com.example.vvpservice",
        "com.example.kafka",
        "com.example.vvpweb",
        "com.example.gateway",
        "com.example.vvpscheduling"})
@EnableJpaRepositories(basePackages = {"com.example.vvpdomain"})// 2. Dao 层所在的包
@EntityScan(basePackages = "com.example.vvpdomain")// 3. Entity 所在的包
@ForestScan(basePackages = "com.example.gateway.forest")
public class StartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
    }

}
