package com.mlml.ashlight.auction.core.config

import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.Configuration
import java.io.File

object PluginConfig {

    private lateinit var config: Configuration

    fun load() {
        val file = File(getDataFolder(), "config.yml")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            PluginConfig::class.java.getResourceAsStream("/config.yml")!!
                .copyTo(file.outputStream())
        }
        config = Configuration.loadFromFile(file)
    }

    val mysqlHost: String get() = config.getString("mysql.host", "localhost")!!
    val mysqlPort: Int get() = config.getInt("mysql.port", 3306)
    val mysqlDatabase: String get() = config.getString("mysql.database", "auction")!!
    val mysqlUsername: String get() = config.getString("mysql.username", "root")!!
    val mysqlPassword: String get() = config.getString("mysql.password", "")!!
    val mysqlPoolSize: Int get() = config.getInt("mysql.pool-size", 10)

    val redisHost: String get() = config.getString("redis.host", "localhost")!!
    val redisPort: Int get() = config.getInt("redis.port", 6379)
    val redisPassword: String get() = config.getString("redis.password", "")!!
    val redisPageTtl: Long get() = config.getLong("redis.page-ttl", 300)
}
