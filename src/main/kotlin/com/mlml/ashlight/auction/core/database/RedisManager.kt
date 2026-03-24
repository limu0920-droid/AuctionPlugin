package com.mlml.ashlight.auction.core.database

import com.mlml.ashlight.auction.core.config.PluginConfig
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import taboolib.common.platform.function.warning

object RedisManager {

    private var client: RedisClient? = null
    private var connection: StatefulRedisConnection<String, String>? = null

    fun connect() {
        val uri = RedisURI.builder()
            .withHost(PluginConfig.redisHost)
            .withPort(PluginConfig.redisPort)
            .apply {
                if (PluginConfig.redisPassword.isNotEmpty())
                    withPassword(PluginConfig.redisPassword.toCharArray())
            }
            .build()
        client = RedisClient.create(uri)
        connection = client!!.connect()
    }

    fun disconnect() {
        connection?.close()
        client?.shutdown()
    }

    fun sync(): RedisCommands<String, String> =
        connection?.sync() ?: error("Redis 未连接")

    /** 获取缓存，未命中返回 null */
    fun getPage(cacheKey: String): String? = try {
        sync().get(cacheKey)
    } catch (e: Exception) {
        warning("[Redis] get 失败: ${e.message}")
        null
    }

    /** 写入缓存，TTL 秒 */
    fun setPage(cacheKey: String, json: String) = try {
        sync().setex(cacheKey, PluginConfig.redisPageTtl, json)
    } catch (e: Exception) {
        warning("[Redis] setex 失败: ${e.message}")
    }

    /** 写操作后失效所有分页缓存 */
    fun invalidatePageCache() = try {
        val keys = sync().keys("auction:page:*")
        if (keys.isNotEmpty()) {
            sync().del(*keys.toTypedArray())
        } else {
            Unit;
        }
    } catch (e: Exception) {
        warning("[Redis] 失效缓存失败: ${e.message}")
    }
}
