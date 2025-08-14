package com.timla.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import javax.sql.DataSource

@RestController
@RequestMapping("/health")
class HealthController {

    @Autowired
    private lateinit var dataSource: DataSource

    @GetMapping
    fun health(): ResponseEntity<Map<String, Any>> {
        val healthStatus = mutableMapOf<String, Any>(
            "status" to "UP",
            "timestamp" to LocalDateTime.now(),
            "service" to "kalkulus-backend",
            "version" to "0.0.1-SNAPSHOT"
        )
        
        // Check database connection and tables
        try {
            dataSource.connection.use { connection ->
                // Check if we can connect
                healthStatus["database"] = "CONNECTED"
                
                // Check if tables exist
                val tables = mutableListOf<String>()
                val meta = connection.metaData
                val rs = meta.getTables(null, "public", "%", arrayOf("TABLE"))
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"))
                }
                healthStatus["tables"] = tables
                healthStatus["table_count"] = tables.size
            }
        } catch (e: Exception) {
            healthStatus["database"] = "ERROR: ${e.message}"
        }
        
        return ResponseEntity.ok(healthStatus)
    }
}
