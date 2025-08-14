package com.timla.config

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.net.Socket

@Configuration
@Profile("prod", "railway")
class ProductionDataSourceConfig : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        println("✅ Application started successfully with production database connection!")
        
        // Test basic connectivity to Railway PostgreSQL
        try {
            Socket("postgres.railway.internal", 5432).use {
                println("✅ Raw TCP connection to postgres.railway.internal:5432 successful!")
            }
        } catch (e: Exception) {
            println("❌ Cannot connect to postgres.railway.internal:5432 - ${e.message}")
        }
    }
}
