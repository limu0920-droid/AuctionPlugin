package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.database.CacheManager
import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import com.mlml.ashlight.auction.utils.Database
import org.bukkit.entity.Player
import taboolib.common.platform.function.info

/**
 * 我的上架服务 - 处理查询玩家自己的上架物品
 * 使用三级缓存架构（Caffeine + Redis + MySQL）
 */
object MyListingService : MessageHandler {
    
    override val key: String = "myListings"
    
    /**
     * 查询我的上架
     * @param player 请求的玩家
     * @param playerName 玩家名称
     * @param sender 响应发送器
     */
    fun queryMyListings(player: Player, playerName: String, sender: ResponseSender) {
        val cacheKey = "auction:mylistings:$playerName"
        info("[MyListingService] 收到请求 - playerName=$playerName, cacheKey=$cacheKey")
        
        val json = CacheManager.get(cacheKey) {
            // 从数据库查询
            val items = Database.queryMyListings(player.uniqueId.toString())
            info("[MyListingService] 数据库查询结果: ${items.size} 条记录")
            items.forEach { item ->
                info("[MyListingService] 物品: uuid=${item.uuid}, name=${item.name}, price=${item.price}, expireAt=${item.expireAt}")
            }
            
            val now = System.currentTimeMillis() / 1000
            val itemsJson = items.joinToString(",") { item ->
                """{"id":"${item.uuid}","itemName":"${item.name}","amount":${item.amount},"category":"${item.category}","mode":"fixed","price":${item.price},"currentBid":0,"status":"active","expireAt":${item.expireAt},"_serverNow":$now}"""
            }
            """{"code":200,"data":[$itemsJson]}"""
        }
        
        info("[MyListingService] 返回数据: $json")
        if (json != null) {
            sender.send(player, "ashandlight:auction_mylistings_data", json)
        }
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        info("[MyListingService] handle called - data='$data', player=${player.name}")
        queryMyListings(player, data, sender)
    }
}