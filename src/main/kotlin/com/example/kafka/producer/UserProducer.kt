package com.example.kafka.producer

import com.example.kafka.dto.UserRegisteredEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class UserProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun send(event: UserRegisteredEvent) {
        val json = objectMapper.writeValueAsString(event)
        kafkaTemplate.send("user-topic", event.userId.toString(), json)
        println("🔥 Sent to Kafka with key=${event.userId}: $json")
    }
}
