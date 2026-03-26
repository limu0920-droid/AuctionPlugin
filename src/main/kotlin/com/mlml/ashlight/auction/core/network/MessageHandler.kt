package com.mlml.ashlight.auction.core.network

import org.bukkit.entity.Player

/**
 * 消息处理器接口
 * 用于解耦消息分发逻辑
 */
interface MessageHandler {
    /**
     * 处理器标识，对应客户端发送的 key
     */
    val key: String
    
    /**
     * 处理消息
     * @param player 发送消息的玩家
     * @param data 消息数据
     * @param sender 响应发送器
     */
    fun handle(player: Player, data: String, sender: ResponseSender)
}
