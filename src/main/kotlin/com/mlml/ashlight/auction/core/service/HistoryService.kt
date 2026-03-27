package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.database.CacheManager
import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import com.mlml.ashlight.auction.utils.Database
import org.bukkit.entity.Player
import taboolib.common.platform.function.info

/**
 * 历史记录服务 - 处理交易历史查询
 * 使用三级缓存架构（Caffeine + Redis + MySQL）
 */
object HistoryService : MessageHandler {
    
    override val key: String = "history"
    
    /**
     * 查询交易历史
     * @param player 请求的玩家
     * @param playerName 玩家名称（传入的是玩家名称，需要用 UUID 查询）
     * @param sender 响应发送器
     */
    fun queryHistory(player: Player, playerName: String, sender: ResponseSender) {
        val cacheKey = "auction:history:$playerName"
        info("[HistoryService] 收到请求 - playerName=$playerName, playerUuid=${player.uniqueId}, cacheKey=$cacheKey")
        
        val json = CacheManager.get(cacheKey) {
            // 从数据库查询（使用玩家的 UUID）
            val playerUuid = player.uniqueId.toString()
            val records = Database.queryHistory(playerUuid)
            info("[HistoryService] 数据库查询结果: ${records.size} 条记录")
            records.forEach { record ->
                info("[HistoryService] 记录: role=${record.role}, itemName=${record.itemName}, price=${record.price}, target=${record.target}")
            }
            
            val recordsJson = records.joinToString(",") { record ->
                """{"role":"${record.role}","itemName":"${record.itemName}","price":${record.price},"fee":${record.fee},"mode":"${record.mode}","target":"${record.target}"}"""
            }
            """{"code":200,"data":[$recordsJson]}"""
        }
        
        info("[HistoryService] 返回数据: $json")
        if (json != null) {
            sender.send(player, "ashandlight:auction_history_data", json)
        }
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        info("[HistoryService] handle called - data='$data', player=${player.name}")
        queryHistory(player, data, sender)
    }
}