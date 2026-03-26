package com.mlml.ashlight.auction

import com.mlml.ashlight.auction.core.network.ResponseSender
import com.mlml.ashlight.auction.network.ChannelRegistry
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.platform.BukkitPlugin

/**
 * 玩家加入事件监听器
 * 通过 ResponseSender 接口与网络层交互
 */
class PlayerJoinListener : Listener {
    
    // 通过依赖注入获取 ResponseSender 实现
    private val sender: ResponseSender get() = ChannelRegistry
    
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val plugin = BukkitPlugin.getInstance()
        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            val json = """{"username":"${player.name}","uuid":"${player.uniqueId}"}"""
            sender.send(player, "ashandlight:player_info", json)
        }, 20L)
    }
}