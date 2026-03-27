package com.mlml.ashlight.auction.core.database

import com.mlml.ashlight.auction.core.config.PluginConfig
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import taboolib.common.platform.function.warning

/**
 * Redis 管理器
 * 负责与 Redis 服务器的连接管理和缓存操作
 */
object RedisManager {

    private var client: RedisClient? = null
    private var connection: StatefulRedisConnection<String, String>? = null

    /**
     * 连接到 Redis 服务器
     * 使用配置文件中的 host、port 和 password 建立连接
     */
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

    /**
     * 断开与 Redis 服务器的连接
     * 关闭连接并释放资源
     */
    fun disconnect() {
        connection?.close()
        client?.shutdown()
    }

    /**
     * 获取同步 Redis 命令接口
     * @return RedisCommands 对象，用于执行 Redis 命令
     * @throws IllegalStateException 如果 Redis 未连接
     */
    fun sync(): RedisCommands<String, String> =
        connection?.sync() ?: error("Redis 未连接")

    /**
     * 获取缓存数据
     * @param cacheKey 缓存键名
     * @return 缓存的 JSON 字符串，如果未命中返回 null
     */
    fun getPage(cacheKey: String): String? = try {
        sync().get(cacheKey)
    } catch (e: Exception) {
        warning("[Redis] get 失败: ${e.message}")
        null
    }

    /**
     * 写入缓存数据
     * @param cacheKey 缓存键名
     * @param json 要缓存的 JSON 字符串
     * TTL 由配置文件中的 redisPageTtl 决定
     */
    fun setPage(cacheKey: String, json: String) = try {
        sync().setex(cacheKey, PluginConfig.redisPageTtl, json)
    } catch (e: Exception) {
        warning("[Redis] setex 失败: ${e.message}")
    }

    /**
     * 失效所有分页缓存
     * 在执行写操作（上架、购买、下架）后调用，确保数据一致性
     * 删除所有以 "auction:page:" 开头的键
     */
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