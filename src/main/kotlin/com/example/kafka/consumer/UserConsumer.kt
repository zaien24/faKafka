package com.example.kafka.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component


@Component
class UserConsumer {
    @KafkaListener(topics = ["user-topic"], groupId = "user-group")
    fun consume(
        message: String,
        @Header("kafka_receivedPartitionId") partition: Int
    ) {
        println("✅ [partition=$partition] message = $message")
    }
}