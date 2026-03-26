package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import org.bukkit.entity.Player

/**
 * 历史记录服务 - 处理交易历史查询
 */
object HistoryService : MessageHandler {
    
    override val key: String = "history"
    
    fun queryHistory(player: Player, playerName: String, sender: ResponseSender) {
        // TODO: 查库
        val json = """{"code":200,"data":[
            {"role":"seller","itemName":"钻石剑","price":5000,"fee":250,"mode":"fixed","target":"买家A"},
            {"role":"buyer","itemName":"铁胸甲","price":1200,"fee":0,"mode":"auction","target":"卖家B"}
        ]}"""
        sender.send(player, "ashandlight:auction_history_data", json)
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        queryHistory(player, data, sender)
    }
}
