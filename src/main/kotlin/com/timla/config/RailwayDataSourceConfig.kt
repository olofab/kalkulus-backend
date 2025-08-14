package com.timla.config

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile("railway")
class RailwayDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    fun dataSourceProperties(): DataSourceProperties {
        return object : DataSourceProperties() {
            override fun determineUrl(): String {
                val url = super.determineUrl()
                println("Original DATABASE_URL: $url")
                
                // Convert Railway's postgresql:// URL to jdbc:postgresql:// format
                val jdbcUrl = if (url.startsWith("postgresql://")) {
                    "jdbc:$url"
                } else {
                    url
                }
                
                println("Converted JDBC URL: $jdbcUrl")
                return jdbcUrl
            }
        }
    }
}
