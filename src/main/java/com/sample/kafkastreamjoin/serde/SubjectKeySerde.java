package com.sample.kafkastreamjoin.serde;

import com.sample.kafkastreamjoin.model.SubjectKey;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class SubjectKeySerde extends Serdes.WrapperSerde<SubjectKey> {
    public SubjectKeySerde () {
        super(new JsonSerializer<>(), new JsonDeserializer<>(SubjectKey.class));
    }
}
