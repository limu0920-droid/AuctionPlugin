package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import org.bukkit.entity.Player

/**
 * 购买服务 - 处理直接购买
 */
object BuyService : MessageHandler {
    
    override val key: String = "buy"
    
    fun handleBuy(player: Player, itemId: String, sender: ResponseSender) {
        // TODO: 查库 → 扣款 → 转移物品
        val response = """{"code":200,"message":"购买成功"}"""
        sender.send(player, "ashandlight:auction_bid_result", response)
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        handleBuy(player, data, sender)
    }
}
