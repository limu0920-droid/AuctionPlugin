package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import org.bukkit.entity.Player

/**
 * 日志服务 - 处理全局日志查询
 */
object LogService : MessageHandler {
    
    override val key: String = "logs"
    
    fun queryLogs(player: Player, sender: ResponseSender) {
        // TODO: 查库，logs 是全局日志不过滤玩家
        val json = """{"code":200,"data":[
            {"type":"上架","player":"张三","itemName":"钻石剑","amount":5000},
            {"type":"购买","player":"李四","itemName":"铁胸甲","amount":1200}
        ]}"""
        sender.send(player, "ashandlight:auction_logs_data", json)
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        queryLogs(player, sender)
    }
}
