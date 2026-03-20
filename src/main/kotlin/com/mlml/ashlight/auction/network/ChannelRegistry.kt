package com.mlml.ashlight.auction.network

import com.mlml.ashlight.auction.core.AuctionManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import taboolib.common.platform.function.info
import taboolib.platform.BukkitPlugin
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object ChannelRegistry : PluginMessageListener {

    private val cooldowns = ConcurrentHashMap<UUID, Long>()
    private const val COOLDOWN_MS = 100L

    fun register() {
        val messenger = Bukkit.getMessenger()
        val plugin = BukkitPlugin.getInstance()
        messenger.registerIncomingPluginChannel(plugin, "legendengine:auction", this)
        messenger.registerOutgoingPluginChannel(plugin, "legendengine:data_packet")
        messenger.registerOutgoingPluginChannel(plugin, "legendengine:rpc_packet")
    }

    fun unregister() {
        val messenger = Bukkit.getMessenger()
        val plugin = BukkitPlugin.getInstance()
        messenger.unregisterIncomingPluginChannel(plugin, "legendengine:auction")
        messenger.unregisterOutgoingPluginChannel(plugin, "legendengine:data_packet")
        messenger.unregisterOutgoingPluginChannel(plugin, "legendengine:rpc_packet")
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != "legendengine:auction") return

        val now = System.currentTimeMillis()
        if (now - (cooldowns[player.uniqueId] ?: 0L) < COOLDOWN_MS) return
        cooldowns[player.uniqueId] = now

        val stream = DataInputStream(ByteArrayInputStream(message))
        try {
            val key = stream.readMcString()
            val value = stream.readMcString()
            info("[收包] player=${player.name} key=$key value=$value")
            when (key) {
                "list"         -> AuctionManager.queryPage(player, value.toIntOrNull() ?: 1)
                "list_auction" -> AuctionManager.queryAuctionPage(player, value)
                "bid"          -> AuctionManager.handleBid(player, value)
            }
        } catch (e: Exception) {
            info("[错误] 解析失败: ${e.message}")
        }
    }

    /** 发送 RPC 指令给客户端（如 open_ui） */
    fun sendRpc(player: Player, command: String, vararg args: String) {
        val out = ByteArrayOutputStream()
        val stream = DataOutputStream(out)
        stream.writeMcString(command)
        args.forEach { stream.writeMcString(it) }
        player.sendPluginMessage(BukkitPlugin.getInstance(), "legendengine:rpc_packet", out.toByteArray())
    }

    /** 发送数据包给客户端 */
    fun sendData(player: Player, channel: String, json: String) {
        val out = ByteArrayOutputStream()
        val stream = DataOutputStream(out)
        stream.writeMcString(channel)
        stream.writeMcString(json)
        player.sendPluginMessage(BukkitPlugin.getInstance(), "legendengine:data_packet", out.toByteArray())
    }
}
