package com.timla.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        
        // Allow specific origins and patterns
        configuration.allowedOriginPatterns = listOf(
            "http://localhost:*",               // Local development (any port)
            "https://localhost:*",              // Local HTTPS (any port)
            "https://*.vercel.app",             // Vercel deployments
            "https://*.netlify.app",            // Netlify deployments
            "https://*.railway.app",            // Railway deployments
            "https://kalkulus-frontend.vercel.app",  // Specific frontend URL
            "https://*.vercel.com",             // Alternative Vercel domain
            "*"                                 // Temporary: Allow all origins for debugging
        )
        
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L // Cache preflight for 1 hour
        
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
