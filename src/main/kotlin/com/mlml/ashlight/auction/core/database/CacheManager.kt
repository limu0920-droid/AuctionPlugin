package com.mlml.ashlight.auction.core.database

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.mlml.ashlight.auction.core.config.PluginConfig
import taboolib.common.platform.function.warning
import java.util.concurrent.TimeUnit

/**
 * 缓存管理器 - 提供三级缓存（Caffeine 本地缓存 + Redis 分布式缓存）
 * Caffeine 用于热点数据缓存，Redis 用于跨进程共享
 */
object CacheManager {

    // 本地 Caffeine 缓存 - 用于高频访问数据
    private val localCache: Cache<String, String> = Caffeine.newBuilder()
        .maximumSize(PluginConfig.caffeineMaxSize)
        .expireAfterWrite(PluginConfig.caffeineExpireSeconds, TimeUnit.SECONDS)
        .build()

    /**
     * 获取缓存数据
     * 优先从本地 Caffeine 缓存获取，未命中则查询 Redis
     * @param key 缓存键名
     * @param defaultValueProvider 当缓存未命中时的数据提供器（懒加载）
     * @return 缓存的 JSON 字符串，如果未命中且无法获取数据返回 null
     */
    fun get(key: String, defaultValueProvider: () -> String): String? {
        // 1. 尝试从本地 Caffeine 缓存获取
        localCache.getIfPresent(key)?.let { return it }

        // 2. 尝试从 Redis 获取
        try {
            RedisManager.getPage(key)?.let { json ->
                // 写入本地缓存并返回
                localCache.put(key, json)
                return json
            }
        } catch (e: Exception) {
            warning("[CacheManager] Redis 获取失败: ${e.message}")
        }

        // 3. 缓存未命中，调用数据提供器获取数据
        return try {
            val json = defaultValueProvider()
            // 写入 Redis（异步）与本地缓存
            localCache.put(key, json)
            try {
                RedisManager.setPage(key, json)
            } catch (e: Exception) {
                warning("[CacheManager] Redis 写入失败: ${e.message}")
            }
            json
        } catch (e: Exception) {
            warning("[CacheManager] 数据获取失败: ${e.message}")
            null
        }
    }

    /**
     * 失效指定缓存
     * 同时清除本地缓存和 Redis 缓存
     * @param key 要失效的缓存键名
     */
    fun invalidate(key: String) {
        localCache.invalidate(key)
        try {
            RedisManager.sync().del(key)
        } catch (e: Exception) {
            warning("[CacheManager] Redis 删除失败: ${e.message}")
        }
    }

    /**
     * 清空本地缓存
     */
    fun clearLocal() {
        localCache.invalidateAll()
    }
}