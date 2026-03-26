package com.mlml.ashlight.auction.core.service

import com.mlml.ashlight.auction.core.network.MessageHandler
import com.mlml.ashlight.auction.core.network.ResponseSender
import org.bukkit.entity.Player

/**
 * 查询服务 - 处理拍卖行列表查询
 */
object QueryService : MessageHandler {
    
    override val key: String = "list"
    
    fun queryPage(player: Player, page: Int, sender: ResponseSender) {
        // 计算过期时间戳（当前时间 + 1小时，单位：秒）
        val expireAt = (System.currentTimeMillis() / 1000) + 3600
        // 构建拍卖列表JSON响应（含模拟数据）
        val json = """{"page":$page,"total":36,"items":[
            {"id":1,"itemName":"§b锋利 V 钻石剑","category":"武器","seller":"王小明","price":5000,"expireAt":$expireAt},
            {"id":2,"itemName":"§a保护 IV 铁胸甲","category":"防具","seller":"李华","price":1200,"expireAt":$expireAt},
            {"id":3,"itemName":"§d下界合金锭","category":"材料","seller":"张三","price":9999,"expireAt":$expireAt}
        ]}""".trimIndent()
        // 通过插件通道发送响应数据给玩家客户端
        sender.send(player, "ashandlight:auction_data", json)
    }
    
    override fun handle(player: Player, data: String, sender: ResponseSender) {
        queryPage(player, data.toIntOrNull() ?: 1, sender)
    }
}
