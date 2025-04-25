package com.example.kafka.config

import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KStream
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaStreamsConfig {

    @Bean
    fun userStream(builder: StreamsBuilder): KStream<String, String> {
        val stream = builder.stream<String, String>("user-topic")

        stream
            .peek { key, value -> println("📥 Stream 수신: key=$key, value=$value") }
            .mapValues { value -> value.uppercase() }
            .to("user-topic-processed")

        return stream
    }
}