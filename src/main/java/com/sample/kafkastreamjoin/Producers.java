package com.sample.kafkastreamjoin;

import com.sample.kafkastreamjoin.model_avro.*;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

//@EnableSchemaRegistryClient
public class Producers {

	public static void main(String... args) throws Exception {

		final Map<String, String> serdeConfig = Collections.singletonMap(
				AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081"/*"http://localhost:8990"*/);

		final SpecificAvroSerializer<School> schoolSerializer = new SpecificAvroSerializer<>();
		schoolSerializer.configure(serdeConfig, false);

		final SpecificAvroSerializer<SchoolKey> schoolKeySerializer = new SpecificAvroSerializer<>();
		schoolKeySerializer.configure(serdeConfig, true); // `true` for record keys

		Map<String, Object> props = new HashMap<>();
		props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081"/*"http://localhost:8990"*/);
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, schoolKeySerializer.getClass());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, schoolSerializer.getClass());

		//props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
		//props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);

		List<KeyValue<SchoolKey, School>> schools = Arrays.asList(
				new KeyValue<>(new SchoolKey("BMIA", "PK", "Islamabad", 1), new School("Sector F/8", "Secondary")),
				new KeyValue<>(new SchoolKey("CMII", "Hk", "Rawalpindi", 2), new School("Sector G/8", "Secondary")),
				new KeyValue<>(new SchoolKey("SCSV", "USA", "Lahore", 3), new School("Sector H/8", "Primary")),
				new KeyValue<>(new SchoolKey("NVS", "SW", "Faisalbad", 4), new School("Sector J/8", "Junior")),
				new KeyValue<>(new SchoolKey("SNVJ", "CH", "Shikarpur", 5), new School("Sector C/8", "Primary")),
				new KeyValue<>(new SchoolKey("DBJ", "CN", "Talaqand", 6), new School("Sector Z/8", "Secondary")),
				new KeyValue<>(new SchoolKey("SCNJ", "SE", "Karachi", 7), new School("Sector S/8", "Junior"))
		);

		DefaultKafkaProducerFactory<SchoolKey, School> schoolF = new DefaultKafkaProducerFactory<>(props);
		KafkaTemplate<SchoolKey, School> templateSchool = new KafkaTemplate<>(schoolF, true);
		templateSchool.setDefaultTopic("school");

		for (KeyValue<SchoolKey, School> keyValue : schools) {
			templateSchool.sendDefault(keyValue.key, keyValue.value);
		}

		final SpecificAvroSerializer<Occupation> occupationSerializer = new SpecificAvroSerializer<>();
		occupationSerializer.configure(serdeConfig, false);

		final SpecificAvroSerializer<OccupationKey> occupationKeySerializer = new SpecificAvroSerializer<>();
		occupationKeySerializer.configure(serdeConfig, true); // `true` for record keys

		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, occupationKeySerializer.getClass());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, occupationSerializer.getClass());

		//props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
		//props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);

		List<KeyValue<OccupationKey, Occupation>> occupations = Arrays.asList(
				new KeyValue<>(new OccupationKey(1), new Occupation("MTR Corporation", "Hong Kong")),
				new KeyValue<>(new OccupationKey(2), new Occupation("HSBC", "Hong Kong")),
				new KeyValue<>(new OccupationKey(3), new Occupation("AIA", "USA")),
				new KeyValue<>(new OccupationKey(4), new Occupation("UBS", "SWISS")),
				new KeyValue<>(new OccupationKey(5), new Occupation("Morgan Stanley", "USA")),
				new KeyValue<>(new OccupationKey(6), new Occupation("Snap Chat", "USA")),
				new KeyValue<>(new OccupationKey(7), new Occupation("Uber", "Singapore"))
		);

		DefaultKafkaProducerFactory<OccupationKey, Occupation> occupationF = new DefaultKafkaProducerFactory<>(props);
		KafkaTemplate<OccupationKey, Occupation> templateOccupation = new KafkaTemplate<>(occupationF, true);
		templateOccupation.setDefaultTopic("occupation");

		for (KeyValue<OccupationKey, Occupation> keyValue : occupations) {
			templateOccupation.sendDefault(keyValue.key, keyValue.value);
		}




	}



}
