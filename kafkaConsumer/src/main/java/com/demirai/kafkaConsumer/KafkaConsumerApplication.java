package com.demirai.kafkaConsumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.*;

import java.util.Arrays;
import java.util.Properties;

public class KafkaConsumerApplication {

	public static void main(String[] args) {

		System.out.println("Starting....");

		// settings
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "0.0.0.0:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
		props.put("schema.registry.url", "http://0.0.0.0:8081");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		String topic0 = "DB_EcommerceServerUsers.dbo.TBL_Users";
		String topic1 = "DB_EcommerceServerOrder.dbo.TBL_Order";

		final Consumer<String, GenericRecord> consumer = new KafkaConsumer<String, GenericRecord>(props);
		consumer.subscribe(Arrays.asList(topic0, topic1));

		try {
			while (true) {

				ConsumerRecords<String, GenericRecord> records = consumer.poll(100);

				for (ConsumerRecord<String, GenericRecord> record : records) {


					try{
						Gson gson = new GsonBuilder().setPrettyPrinting().create();
						JsonParser jp = new JsonParser();
						JsonElement je = jp.parse(record.value().toString());
						String prettyJsonString = gson.toJson(je);

						System.out.println(prettyJsonString);
					} catch (Exception e){

					}
				}
			}
		} finally {
			consumer.close();
		}

	}

}
