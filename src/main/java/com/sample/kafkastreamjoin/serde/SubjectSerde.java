package com.sample.kafkastreamjoin.serde;

import com.sample.kafkastreamjoin.model.Subject;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class SubjectSerde extends Serdes.WrapperSerde<Subject> {
    public SubjectSerde () {
        super(new JsonSerializer<>(), new JsonDeserializer<>(Subject.class));
    }
}
