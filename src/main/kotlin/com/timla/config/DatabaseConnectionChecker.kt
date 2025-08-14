package com.timla.config

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.net.Socket
import kotlin.system.exitProcess

@Component
@Profile("prod", "railway")
class DatabaseConnectionChecker : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        println("Application started successfully with production database connection!")
        
        // Test basic connectivity to Railway PostgreSQL
        try {
            Socket("postgres.railway.internal", 5432).use {
                println("✅ Raw TCP connection to postgres.railway.internal:5432 successful!")
            }
        } catch (e: Exception) {
            println("❌ Cannot connect to postgres.railway.internal:5432 - ${e.message}")
        }
    }

    companion object {
        fun waitForDatabase(host: String, port: Int, timeoutSeconds: Int = 60): Boolean {
            val endTime = System.currentTimeMillis() + (timeoutSeconds * 1000)
            
            while (System.currentTimeMillis() < endTime) {
                try {
                    Socket(host, port).use {
                        println("Database connection successful to $host:$port")
                        return true
                    }
                } catch (e: Exception) {
                    println("Waiting for database at $host:$port... (${e.message})")
                    Thread.sleep(2000)
                }
            }
            
            println("Failed to connect to database at $host:$port after $timeoutSeconds seconds")
            return false
        }
    }
}
