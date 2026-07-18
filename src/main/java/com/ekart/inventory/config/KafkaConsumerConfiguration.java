package com.ekart.inventory.config;

import com.ekart.inventory.event.OrderPlacedKafkaPayload;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * JSON messages omit Kafka type headers ({@code addTypeInfo=false} on producers). The consumer must use a
 * {@link JacksonJsonDeserializer} constructed with the concrete payload type and {@code useHeadersIfPresent=false}.
 */
@Configuration
public class KafkaConsumerConfiguration {

	private static Map<String, Object> baseConsumerProps(Environment env) {
		Map<String, Object> props = new HashMap<>();
		String bootstrapServers =
				env.getProperty("spring.kafka.bootstrap-servers");

		System.out.println("Kafka Bootstrap Servers = " + bootstrapServers);
		props.put(
				ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
				bootstrapServers);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		props.put(
				ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
				env.getProperty("spring.kafka.consumer.auto-offset-reset", "earliest"));
		return props;
	}

	@Bean
	public ConsumerFactory<String, OrderPlacedKafkaPayload> orderPlacedConsumerFactory(Environment env) {
		Map<String, Object> props = baseConsumerProps(env);
		JacksonJsonDeserializer<OrderPlacedKafkaPayload> deserializer =
				new JacksonJsonDeserializer<>(OrderPlacedKafkaPayload.class, false);
		deserializer.addTrustedPackages("com.ekart");
		DefaultKafkaConsumerFactory<String, OrderPlacedKafkaPayload> factory =
				new DefaultKafkaConsumerFactory<>(props);
		factory.setValueDeserializer(deserializer);
		return factory;
	}

	@Bean(name = "kafkaListenerContainerFactory")
	public ConcurrentKafkaListenerContainerFactory<String, OrderPlacedKafkaPayload> kafkaListenerContainerFactory(
			ConsumerFactory<String, OrderPlacedKafkaPayload> orderPlacedConsumerFactory) {
		ConcurrentKafkaListenerContainerFactory<String, OrderPlacedKafkaPayload> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(orderPlacedConsumerFactory);
		return factory;
	}
}
