package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import org.bukkit.entity.Player

/**
 * 我的上架服务 - 处理查询玩家自己的上架物品
 */
object MyListingService : MessageHandler {
    
    override val key: String = "myListings"
    
    fun queryMyListings(player: Player, playerName: String, sender: ResponseSender) {
        val now = System.currentTimeMillis() / 1000
        val expireAt = now + 3600
        // TODO: 查库
        val json = """{"code":200,"data":[
            {"id":1,"itemName":"钻石剑","category":"武器","mode":"fixed","price":5000,
             "currentBid":0,"status":"active","expireAt":$expireAt,"_serverNow":$now}
        ]}"""
        sender.send(player, "ashandlight:auction_mylistings_data", json)
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        queryMyListings(player, data, sender)
    }
}
