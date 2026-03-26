package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import org.bukkit.entity.Player

/**
 * 竞价服务 - 处理竞拍出价
 */
object BidService : MessageHandler {
    
    override val key: String = "bid"
    
    fun handleBid(player: Player, data: String, sender: ResponseSender) {
        val amount = data.split("|").getOrNull(1)?.toIntOrNull() ?: 0
        val response = if (amount > 50000)
            """{"code":403,"message":"余额不足！"}"""
        else
            """{"code":200,"message":"成功"}"""
        sender.send(player, "ashandlight:auction_bid_result", response)
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        handleBid(player, data, sender)
    }
}
