package com.example.start;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class KafkaConfig {

	@Primary
	@ConfigurationProperties(prefix = "spring.kafka.alarm.performance")
	@Bean
	public KafkaProperties alarmKafkaProperties() {
		return new KafkaProperties();
	}

	@Bean
	public KafkaTemplate<String, String> alarmKafkaTemplate(
			@Autowired @Qualifier("alarmKafkaProperties") KafkaProperties firstKafkaProperties) {
		return new KafkaTemplate<>(alarmProducerFactory(firstKafkaProperties));
	}

	private DefaultKafkaProducerFactory<String, String> alarmProducerFactory(KafkaProperties kafkaProperties) {
		return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
	}

}
