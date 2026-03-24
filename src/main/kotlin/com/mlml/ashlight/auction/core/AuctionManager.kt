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
    fun handleBuy(player: Player, itemId: String) {
        // TODO: 查库 → 扣款 → 转移物品
        val response = """{"code":200,"message":"购买成功"}"""
        ChannelRegistry.sendData(player, "legendengine:auction", response)
    }

    fun handleCreate(player: Player, data: String) {
        val parts = data.split("|")
        if (parts.size < 6) {
            ChannelRegistry.sendData(player, "legendengine:auction_create_result",
                """{"code":400,"message":"参数错误"}""")
            return
        }
        val itemName  = parts[0]
        val category  = parts[1]
        val mode      = parts[2]
        val price     = parts[3].toLongOrNull() ?: 0L
        val duration  = parts[4].toIntOrNull() ?: 1
        val seller    = parts[5]

        // TODO: 写库
        val response = """{"code":200,"message":"上架成功"}"""
        ChannelRegistry.sendData(player, "legendengine:auction_create_result", response)
    }
    fun queryMyListings(player: Player, playerName: String) {
        val now = System.currentTimeMillis() / 1000
        val expireAt = now + 3600
        // TODO: 查库
        val json = """{"code":200,"data":[
        {"id":1,"itemName":"钻石剑","category":"武器","mode":"fixed","price":5000,
         "currentBid":0,"status":"active","expireAt":$expireAt,"_serverNow":$now}
    ]}"""
        ChannelRegistry.sendData(player, "legendengine:auction_mylistings_data", json)
    }

    fun handleCancel(player: Player, itemId: String) {
        // TODO: 查库校验 → 删除或标记取消
        val response = """{"code":200,"message":"取消成功"}"""
        ChannelRegistry.sendData(player, "legendengine:auction_cancel_result", response)
    }

    fun queryHistory(player: Player, playerName: String) {
        // TODO: 查库
        val json = """{"code":200,"data":[
        {"role":"seller","itemName":"钻石剑","price":5000,"fee":250,"mode":"fixed","target":"买家A"},
        {"role":"buyer","itemName":"铁胸甲","price":1200,"fee":0,"mode":"auction","target":"卖家B"}
    ]}"""
        ChannelRegistry.sendData(player, "legendengine:auction_history_data", json)
    }

    fun queryLogs(player: Player) {
        // TODO: 查库，logs 是全局日志不过滤玩家
        val json = """{"code":200,"data":[
        {"type":"上架","player":"张三","itemName":"钻石剑","amount":5000},
        {"type":"购买","player":"李四","itemName":"铁胸甲","amount":1200}
    ]}"""
        ChannelRegistry.sendData(player, "legendengine:auction_logs_data", json)
    }
}
