package com.example.kafka.repository

import com.example.kafka.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>