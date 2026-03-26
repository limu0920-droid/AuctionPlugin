package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import org.bukkit.entity.Player

/**
 * 取消服务 - 处理取消上架
 */
object CancelService : MessageHandler {
    
    override val key: String = "cancel"
    
    fun handleCancel(player: Player, itemId: String, sender: ResponseSender) {
        // TODO: 查库校验 → 删除或标记取消
        val response = """{"code":200,"message":"取消成功"}"""
        sender.send(player, "ashandlight:auction_cancel_result", response)
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        handleCancel(player, data, sender)
    }
}
