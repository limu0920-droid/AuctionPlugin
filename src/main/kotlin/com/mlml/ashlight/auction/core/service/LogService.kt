package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.database.CacheManager
import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import org.bukkit.entity.Player
import taboolib.common.platform.function.info

/**
 * 日志服务 - 处理全局日志查询
 * 使用三级缓存架构（Caffeine + Redis + MySQL）
 */
object LogService : MessageHandler {
    
    override val key: String = "logs"
    
    /**
     * 查询全局日志
     * logs 是全局日志，不过滤玩家
     * @param player 请求的玩家
     * @param sender 响应发送器
     */
    fun queryLogs(player: Player, sender: ResponseSender) {
        val cacheKey = "auction:logs:global"
        info("[LogService] 收到请求 - player=${player.name}, cacheKey=$cacheKey")
        
        val json = CacheManager.get(cacheKey) {
            info("[LogService] 缓存未命中，生成模拟数据")
            // TODO: 查库，logs 是全局日志不过滤玩家
            """{"code":200,"data":[
                {"type":"上架","player":"张三","itemName":"钻石剑","amount":5000},
                {"type":"购买","player":"李四","itemName":"铁胸甲","amount":1200}
            ]}"""
        }
        
        info("[LogService] 返回数据: $json")
        if (json != null) {
            sender.send(player, "ashandlight:auction_logs_data", json)
        }
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        queryLogs(player, sender)
    }
}