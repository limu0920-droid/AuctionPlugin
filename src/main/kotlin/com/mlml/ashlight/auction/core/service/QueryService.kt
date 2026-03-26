package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import org.bukkit.entity.Player

/**
 * 查询服务 - 处理拍卖行列表查询
 * 从模拟数据中分页获取物品
 */
object QueryService : MessageHandler {

    override val key: String = "list"

    fun queryPage(player: Player, page: Int, sender: ResponseSender) {
        val result = MockDataService.getItemsPage(page)

        // 构建 JSON 响应
        val itemsJson = result.items.joinToString(",") { item ->
            """{"id":${item.id},"itemName":"${item.itemName}","category":"${item.category}","seller":"${item.seller}","price":${item.price},"expireAt":${item.expireAt}}"""
        }

        val json = """{"page":${result.page},"totalPages":${result.totalPages},"total":${result.total},"items":[$itemsJson]}"""

        sender.send(player, "ashandlight:auction_data", json)
    }

    override fun handle(player: Player, data: String, sender: ResponseSender) {
        val page = data.toIntOrNull() ?: 1
        queryPage(player, page, sender)
    }
}
