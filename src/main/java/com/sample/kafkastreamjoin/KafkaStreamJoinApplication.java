package com.sample.kafkastreamjoin;

import com.sample.kafkastreamjoin.model_avro.*;
import com.sample.kafkastreamjoin.serde.*;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
//@EnableSchemaRegistryClient
public class KafkaStreamJoinApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaStreamJoinApplication.class, args);
	}

	@EnableBinding(KStreamProcessorX.class)
	public static class KafkaKStreamJoinApplication {

		// create and configure the SpecificAvroSerdes required in this example
		final Map<String, String> serdeConfig = Collections.singletonMap(
				AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

		@StreamListener
		public void process(@Input("school") KStream<SchoolKey, School> schools, @Input("occupation") KStream<OccupationKey, Occupation> occupations) {

			final SpecificAvroSerde<Person> personSerde = new SpecificAvroSerde<>();
			personSerde.configure(serdeConfig, false);

			final SpecificAvroSerde<School> schoolSerde = new SpecificAvroSerde<>();
			schoolSerde.configure(serdeConfig, false);

			final SpecificAvroSerde<PersonKey> personKeySerde = new SpecificAvroSerde<>();
			personKeySerde.configure(serdeConfig, true); // `true` for record keys

			final SpecificAvroSerde<Occupation> occupationSerde = new SpecificAvroSerde<>();
			occupationSerde.configure(serdeConfig, false);

			final SpecificAvroSerde<OccupationKey> occupationKeySerde = new SpecificAvroSerde<>();
			occupationKeySerde.configure(serdeConfig, true); // `true` for record keys

			occupations.peek((key, value) -> System.out.println("Occupationkey= " + key + ", OccupationValue= " + value));

			schools.peek((key, value) -> System.out.println("Schoolkey= " + key + ", SchoolValue= " + value))
					.map((schoolKey, school) -> {
				return KeyValue.pair(new PersonKey("Adam", "Smith", schoolKey.getId()), new Person(12.0));
			})
					.through("person", Produced.with(personKeySerde, personSerde));

			/*schools.selectKey((schoolKey, school) -> new OccupationKey(schoolKey.getId()))
					.join(occupations,
							(school, occupation) -> {
								System.out.println("school_app1.2= " + school + ", occupation_app1.2= " + occupation);
								return null;
							},
							JoinWindows.of(Duration.ofSeconds(1)),
							Joined.with(occupationKeySerde, schoolSerde, occupationSerde)
					);*/

		}

		@StreamListener
		public void process1(@Input("school_1") KStream<SchoolKey, School> schools, @Input("person") KStream<PersonKey, Person> persons) {

			schools.peek((key, value) -> System.out.println("Schoolkey1= " + key + ", SchoolValue1= " + value));
			persons.peek((key, value) -> System.out.println("Personkey1= " + key + ", PersonValue1= " + value));

			final SpecificAvroSerde<Person> personSerde = new SpecificAvroSerde<>();
			personSerde.configure(serdeConfig, false);

			final SpecificAvroSerde<School> schoolSerde = new SpecificAvroSerde<>();
			schoolSerde.configure(serdeConfig, false);

			final SpecificAvroSerde<SubjectKey> subjectKeySerde = new SpecificAvroSerde<>();
			subjectKeySerde.configure(serdeConfig, true); // `true` for record keys

			final SpecificAvroSerde<OccupationKey> occupationKeySerde = new SpecificAvroSerde<>();
			occupationKeySerde.configure(serdeConfig, true); // `true` for record keys


			schools.peek((key, value) -> System.out.println("Schoolkey1= " + key + ", SchoolValue1= " + value)).selectKey((schoolKey, school) -> schoolKey.getId())
					.join(persons.peek((key, value) -> System.out.println("Personkey1= " + key + ", PersonValue1= " + value)).selectKey((personKey, person) -> personKey.getId()),
							(school, person) -> {
								System.out.println("school_app2.1= " + school + ", person_app2.1= " + person);
								return null;
							},
							JoinWindows.of(Duration.ofSeconds(1)),
							Joined.with(Serdes.Integer(), schoolSerde, personSerde)
					);

			/*schools.selectKey((schoolKey, school) -> new SubjectKey(schoolKey.getId()))
					.join(persons.selectKey((personKey, person) -> new SubjectKey(personKey.getId())),
							(school, person) -> {
								System.out.println("school_app2.2= " + school + ", person_app2.2= " + person);
								return null;
							},
							JoinWindows.of(Duration.ofSeconds(1)),
							Joined.with(subjectKeySerde, schoolSerde, personSerde)
					);*/

			schools.selectKey((schoolKey, school) -> new OccupationKey(schoolKey.getId()))
					.join(persons.selectKey((personKey, person) -> new OccupationKey(personKey.getId())),
							(school, person) -> {
								System.out.println("school_app2.2= " + school + ", person_app2.2= " + person);
								return null;
							},
							JoinWindows.of(Duration.ofSeconds(1)),
							Joined.with(occupationKeySerde, schoolSerde, personSerde)
					);

			schools.selectKey((schoolKey, school) -> new OccupationKey(schoolKey.getId())).peek((key, value) -> System.out.println("Occupationkey2= " + key + ", OccupationValue2= " + value));

		}

		@StreamListener
		@SendTo("subject")
		public KStream<SubjectKey, Subject> process3(@Input("school_2") KStream<SchoolKey, School> schools, @Input("person_2") KStream<PersonKey, Person> persons) {

			final SpecificAvroSerde<Person> personSerde = new SpecificAvroSerde<>();
			personSerde.configure(serdeConfig, false);

			final SpecificAvroSerde<School> schoolSerde = new SpecificAvroSerde<>();
			schoolSerde.configure(serdeConfig, false);

			schools.peek((key, value) -> System.out.println("Schoolkey3= " + key + ", SchoolValue3= " + value));
			persons.peek((key, value) -> System.out.println("Personkey3= " + key + ", PersonValue3= " + value));

			return schools.selectKey((schoolKey, school) -> schoolKey.getId())
					.join(persons.selectKey((personKey, person) -> personKey.getId()),
							(school, person) -> {
								System.out.println("school_app3= " + school + ", person_app3= " + person);
								if(person.getAge() < 30){
									return Arrays.asList(new Subject("math"), new Subject());
								}
								if(person.getAge() < 35){
									return Arrays.asList(new Subject(), new Subject("physics"));
								}
								return Arrays.asList(new Subject("math"), new Subject("physics"));

							},
							JoinWindows.of(Duration.ofSeconds(1)),
							Joined.with(Serdes.Integer(), schoolSerde, personSerde)
					)
					.flatMap((key, value) -> Arrays.asList(
							KeyValue.pair(new SubjectKey(key), value != null ? value.get(0) : null),
							KeyValue.pair(new SubjectKey(key), value != null ? value.get(1) : null))
					)
					//.flatMapValues((value) -> Arrays.asList(value.get(0), value.get(1)))
					.filter((key, value) -> value.getTitle() != null);
		}


	}

	interface KStreamProcessorX {

		@Input("person")
		KStream<?, ?> inputPersonKStream();

		@Input("occupation")
		KStream<?, ?> inputOccupationKStream();

		@Input("school")
		KStream<?, ?> inputSchoolKStream();

		@Input("school_1")
		KStream<?, ?> inputSchool1KStream();

		@Input("school_2")
		KStream<?, ?> inputSchool2KStream();

		@Input("person_2")
		KStream<?, ?> inputPerson2KStream();

		@Output("subject")
		KStream<?, ?> inputSubjectKStream();

	}
}
