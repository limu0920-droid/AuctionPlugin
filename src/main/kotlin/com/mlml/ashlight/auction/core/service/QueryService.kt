package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import com.mlml.ashlight.auction.utils.Database
import org.bukkit.entity.Player
import taboolib.common.platform.function.info

/**
 * 查询服务 - 处理拍卖行列表查询
 * 从数据库中分页获取物品
 */
object QueryService : MessageHandler {

    override val key: String = "list"

    fun queryPage(player: Player, page: Int, sender: ResponseSender) {
        // 从数据库查询
        val items = Database.queryAuctionPage(page)
        val total = Database.countActiveItems()
        val totalPages = (total + 19) / 20

        // 打印查询结果
        info("[QueryService] 查询一口价列表 - page=$page, total=$total")
        items.forEach { item ->
            info("[QueryService] 物品: uuid=${item.uuid}, name=${item.name}, price=${item.price}, seller=${item.sellerUuid}, expireAt=${item.expireAt}")
        }

        // 构建 JSON 响应
        val itemsJson = items.joinToString(",") { item ->
            """{"id":"${item.uuid}","itemName":"${item.name}","amount":${item.amount},"category":"${item.category}","seller":"${item.sellerUuid}","price":${item.price},"expireAt":${item.expireAt}}"""
        }

        val json = """{"page":$page,"totalPages":$totalPages,"total":$total,"items":[$itemsJson]}"""

        sender.send(player, "ashandlight:auction_data", json)
    }

    override fun handle(player: Player, data: String, sender: ResponseSender) {
        val page = data.toIntOrNull() ?: 1
        queryPage(player, page, sender)
    }
}