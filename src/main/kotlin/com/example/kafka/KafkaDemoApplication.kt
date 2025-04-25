package com.example.kafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafkaStreams

@SpringBootApplication
@EnableKafkaStreams
class KafkaDemoApplication

fun main(args: Array<String>) {
    runApplication<KafkaDemoApplication>(*args)
}
