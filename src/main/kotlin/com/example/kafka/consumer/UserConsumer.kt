package com.example.kafka.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class UserConsumer {
    @KafkaListener(topics = ["user-topic"], groupId = "user-group")
    fun consume(message: String) {
        println("✅ Kafka Event Received: $message")
    }
}