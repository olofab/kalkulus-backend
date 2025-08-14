package com.timla.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/health")
class HealthController {

    @GetMapping
    fun health(): ResponseEntity<Map<String, Any>> {
        val healthStatus = mapOf(
            "status" to "UP",
            "timestamp" to LocalDateTime.now(),
            "service" to "kalkulus-backend",
            "version" to "0.0.1-SNAPSHOT"
        )
        return ResponseEntity.ok(healthStatus)
    }
}
