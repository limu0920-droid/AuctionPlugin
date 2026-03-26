package com.mlml.ashlight.auction.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 单条拍卖条目，对应 Redis key: auction:{uuid} 和 MySQL auction_items 表
 *
 * name 格式（冒号分隔）：模板ID:强化等级:lore等级:自定义模型数据
 * 例：diamond_sword:5:3:1001
 */
@Serializable
data class AuctionItem(
    val uuid: String,
    val name: String,
    val amount: Int,
    val price: Long,
    @SerialName("seller_uuid") val sellerUuid: String,
    @SerialName("expire_at")   val expireAt: Long
)

/**
 * 分页响应，序列化后通过 ashilight:data_packet 发给客户端
 */
@Serializable
data class AuctionPage(
    val page: Int,
    val total: Int,
    val items: List<AuctionItem>
)
