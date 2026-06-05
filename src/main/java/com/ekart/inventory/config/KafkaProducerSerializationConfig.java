package com.ekart.inventory.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerSerializationConfig {

	@Bean
	public ProducerFactory<Object, Object> kafkaProducerFactory(Environment env) {
		Map<String, Object> props = new HashMap<>();
		props.put(
				ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
				env.getProperty("spring.kafka.bootstrap-servers", "localhost:9092"));

		String clientId = env.getProperty("spring.kafka.producer.client-id");
		if (clientId != null && !clientId.isBlank()) {
			props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
		}
		String acks = env.getProperty("spring.kafka.producer.acks");
		if (acks != null && !acks.isBlank()) {
			props.put(ProducerConfig.ACKS_CONFIG, acks);
		}

		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		DefaultKafkaProducerFactory<Object, Object> factory = new DefaultKafkaProducerFactory<>(props);
		JacksonJsonSerializer<Object> serializer = new JacksonJsonSerializer<>();
		serializer.setAddTypeInfo(false);
		factory.setValueSerializer(serializer);
		return factory;
	}
}
