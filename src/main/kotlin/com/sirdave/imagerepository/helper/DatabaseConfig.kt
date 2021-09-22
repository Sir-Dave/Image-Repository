package com.sirdave.imagerepository.helper

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DatabaseConfig {
    @Value("\${spring.datasource.url}")
    private val dbUrl: String? = null

    @Bean
    fun dataSource(): DataSource {
        val config = HikariConfig()
        //config.jdbcUrl = dbUrl;
        config.jdbcUrl = System.getenv("DB_URL")
        config.username = System.getenv("DB_USER")
        config.password = System.getenv("DB_PASS")
        return HikariDataSource(config)
    }
}