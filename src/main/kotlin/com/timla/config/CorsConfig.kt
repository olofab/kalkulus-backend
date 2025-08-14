package com.timla.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfig {

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins(
                        "http://localhost:3000",           // Local development
                        "https://localhost:3000",          // Local HTTPS
                        "https://*.vercel.app",            // Vercel deployment
                        "https://*.netlify.app",           // Netlify deployment
                        "https://*.railway.app"            // Railway deployment
                    )
                    .allowedMethods("*")
                    .allowedHeaders("*")
                    .allowCredentials(true)

            }
        }
    }
}
