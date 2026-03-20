package com.mlml.ashlight.auction.core

import com.mlml.ashlight.auction.network.ChannelRegistry
import org.bukkit.entity.Player

object AuctionManager {

    fun queryPage(player: Player, page: Int) {
        val expireAt = (System.currentTimeMillis() / 1000) + 3600
        val json = """{"page":$page,"total":36,"items":[
            {"id":1,"itemName":"§b锋利 V 钻石剑","category":"武器","seller":"王小明","price":5000,"expireAt":$expireAt},
            {"id":2,"itemName":"§a保护 IV 铁胸甲","category":"防具","seller":"李华","price":1200,"expireAt":$expireAt},
            {"id":3,"itemName":"§d下界合金锭","category":"材料","seller":"张三","price":9999,"expireAt":$expireAt}
        ]}""".trimIndent()
        ChannelRegistry.sendData(player, "legendengine:auction", json)
    }

    fun queryAuctionPage(player: Player, query: String) {
        val page = query.split("|").getOrNull(0)?.toIntOrNull() ?: 1
        val expireAt = (System.currentTimeMillis() / 1000) + 7200
        val json = """{"page":$page,"total":24,"items":[
            {"id":101,"itemName":"§6★ 龙之脊梁 (神话)","category":"武器","seller":"Admin","price":10000,"currentBid":15000,"expireAt":$expireAt},
            {"id":102,"itemName":"§b附魔金苹果 x64","category":"材料","seller":"Player_A","price":500,"currentBid":0,"expireAt":$expireAt}
        ]}""".trimIndent()
        ChannelRegistry.sendData(player, "legendengine:auction_bid_data", json)
    }

    fun handleBid(player: Player, data: String) {
        val amount = data.split("|").getOrNull(1)?.toIntOrNull() ?: 0
        val response = if (amount > 50000)
            """{"code":403,"message":"余额不足！"}"""
        else
            """{"code":200,"message":"成功"}"""
        ChannelRegistry.sendData(player, "legendengine:auction_bid_result", response)
    }
}
