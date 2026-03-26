package com.mlml.ashlight.auction.core.network

import org.bukkit.entity.Player

/**
 * 响应发送器接口
 * 用于解耦业务层与网络层的直接依赖
 */
interface ResponseSender {
    /**
     * 发送数据给客户端
     * @param player 目标玩家
     * @param channel 目标频道
     * @param data JSON数据
     */
    fun send(player: Player, channel: String, data: String)
    
    /**
     * 发送 OpenUI 指令给客户端
     * @param player 目标玩家
     * @param uiId UI标识
     */
    fun sendOpenUI(player: Player, uiId: String)
}
