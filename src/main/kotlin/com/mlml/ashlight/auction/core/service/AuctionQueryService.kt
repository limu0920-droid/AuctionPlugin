package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import org.bukkit.entity.Player

/**
 * 竞拍查询服务 - 处理竞拍列表查询
 */
object AuctionQueryService : MessageHandler {
    
    override val key: String = "list_auction"
    
    fun queryAuctionPage(player: Player, query: String, sender: ResponseSender) {
        val page = query.split("|").getOrNull(0)?.toIntOrNull() ?: 1
        val expireAt = (System.currentTimeMillis() / 1000) + 7200
        val json = """{"page":$page,"total":24,"items":[
            {"id":101,"itemName":"§6★ 龙之脊梁 (神话)","category":"武器","seller":"Admin","price":10000,"currentBid":15000,"expireAt":$expireAt},
            {"id":102,"itemName":"§b附魔金苹果 x64","category":"材料","seller":"Player_A","price":500,"currentBid":0,"expireAt":$expireAt},
            {"id":103,"itemName":"§b附魔金苹果 x63","category":"材料","seller":"Player_A","price":500,"currentBid":0,"expireAt":$expireAt},
            {"id":104,"itemName":"§b附魔金苹果 x62","category":"材料","seller":"Player_A","price":500,"currentBid":0,"expireAt":$expireAt},
            {"id":105,"itemName":"§b附魔金苹果 x61","category":"材料","seller":"Player_A","price":500,"currentBid":0,"expireAt":$expireAt},
            {"id":106,"itemName":"§b附魔金苹果 x60","category":"材料","seller":"Player_A","price":500,"currentBid":0,"expireAt":$expireAt},
            {"id":107,"itemName":"§b附魔金苹果 x59","category":"材料","seller":"Player_A","price":500,"currentBid":0,"expireAt":$expireAt},
            {"id":108,"itemName":"§b附魔金苹果 x58","category":"材料","seller":"Player_A","price":500,"currentBid":0,"expireAt":$expireAt},
            {"id":109,"itemName":"§b附魔金苹果 x57","category":"材料","seller":"Player_A","price":500,"currentBid":0,"expireAt":$expireAt},
            {"id":110,"itemName":"§b附魔金苹果 x56","category":"材料","seller":"Player_A","price":500,"currentBid":0,"expireAt":$expireAt},
            {"id":111,"itemName":"§b附魔金苹果 x55","category":"材料","seller":"Player_A","price":500,"currentBid":0,"expireAt":$expireAt},
            {"id":112,"itemName":"§b附魔金苹果 x54","category":"材料","seller":"Player_A","price":500,"currentBid":0,"expireAt":$expireAt}
        ]}""".trimIndent()
        sender.send(player, "ashandlight:auction_bid_data", json)
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        println("====> 拍卖行收到请求: $data")
        queryAuctionPage(player, data, sender)
    }
}
