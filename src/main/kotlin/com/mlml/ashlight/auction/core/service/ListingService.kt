package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import org.bukkit.entity.Player

/**
 * 上架服务 - 处理物品上架
 */
object ListingService : MessageHandler {
    
    override val key: String = "create"
    
    fun handleCreate(player: Player, data: String, sender: ResponseSender) {
        val parts = data.split("|")
        if (parts.size < 6) {
            sender.send(player, "ashandlight:auction_create_result",
                """{"code":400,"message":"参数错误"}""")
            return
        }
        // val itemName = parts[0]
        // val category = parts[1]
        // val mode = parts[2]
        // val price = parts[3].toLongOrNull() ?: 0L
        // val duration = parts[4].toIntOrNull() ?: 1
        // val seller = parts[5]
        
        // TODO: 写库
        val response = """{"code":200,"message":"上架成功"}"""
        sender.send(player, "ashandlight:auction_create_result", response)
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        handleCreate(player, data, sender)
    }
}
