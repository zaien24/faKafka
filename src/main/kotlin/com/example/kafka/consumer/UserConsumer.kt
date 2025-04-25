package com.example.kafka.consumer


import com.example.kafka.domain.User
import com.example.kafka.dto.UserRegisteredEvent
import com.example.kafka.repository.UserRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micrometer.tracing.Tracer
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

    @Component
    class UserConsumer(
        private val userRepository: UserRepository,
        private val tracer: Tracer
    ) {

        private val objectMapper = jacksonObjectMapper()

        @RetryableTopic(
            attempts = "3",
            backoff = Backoff(delay = 2000), // 2초 간격 재시도
            dltTopicSuffix = ".DLT",
            autoCreateTopics = "true"
        )
        @KafkaListener(topics = ["user-topic"], groupId = "user-group")
        fun consume(message: String, metadata: ConsumerRecordMetadata) {
            println("📥 Kafka 수신: $message")

            val span = tracer.nextSpan().name("kafka.consume").start()
            try {
                tracer.withSpan(span).use {
                    span.tag("kafka.topic", metadata.topic())
                    span.tag("kafka.partition", metadata.partition().toString())
                    span.tag("kafka.offset", metadata.offset().toString())

                    if (message.contains("\"userId\":999")) {
                        span.error(RuntimeException("의도적인 예외 발생"))
                        println("💥 의도적인 예외 발생 (userId: 999)")
                        throw RuntimeException("실패!")
                    }

                    val event = objectMapper.readValue<UserRegisteredEvent>(message)

                    val user = User(
                        userId = event.userId,
                        email = event.email
                    )

                    userRepository.save(user)

                    span.tag("user.id", user.userId.toString())
                    span.tag("user.email", user.email)

                    println("✅ DB 저장 완료: userId=${user.userId}, email=${user.email}")

                }
            } finally {
                span.end()
            }
        }

        @KafkaListener(topics = ["user-topic.DLT"], groupId = "dlt-group")
        fun handleDlt(message: String) {
            println("🛑 DLT 메시지 수신됨: $message")
        }
    }