package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import com.mlml.ashlight.auction.utils.Database
import org.bukkit.entity.Player
import taboolib.common.platform.function.info

/**
 * 竞拍查询服务 - 处理竞拍列表查询
 */
object AuctionQueryService : MessageHandler {
    
    override val key: String = "list_auction"
    
    fun queryAuctionPage(player: Player, query: String, sender: ResponseSender) {
        val page = query.split("|").getOrNull(0)?.toIntOrNull() ?: 1

        // 从数据库查询
        val items = Database.queryAuctionPage(page)
        val total = Database.countActiveItems()

        // 打印查询结果
        info("[AuctionQueryService] 查询竞拍列表 - page=$page, total=$total")
        items.forEach { item ->
            info("[AuctionQueryService] 物品: uuid=${item.uuid}, name=${item.name}, price=${item.price}, seller=${item.sellerUuid}, expireAt=${item.expireAt}")
        }

        // 构建 JSON 响应（添加 currentBid 字段，竞拍初始为 0）
        val itemsJson = items.joinToString(",") { item ->
            """{"id":"${item.uuid}","itemName":"${item.name}","amount":${item.amount},"category":"${item.category}","seller":"${item.sellerUuid}","price":${item.price},"currentBid":0,"expireAt":${item.expireAt}}"""
        }

        val json = """{"page":$page,"total":$total,"items":[$itemsJson]}"""

        sender.send(player, "ashandlight:auction_bid_data", json)
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        println("====> 拍卖行收到请求: $data")
        queryAuctionPage(player, data, sender)
    }
}