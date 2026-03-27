package com.mlml.ashlight.auction.network

import com.mlml.ashlight.auction.core.network.MessageHandlerRegistry
import com.mlml.ashlight.auction.core.network.ResponseSender
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

/**
 * 频道注册器 - 实现 ResponseSender 接口
 * 负责网络通信，与业务逻辑解耦
 */
object ChannelRegistry : PluginMessageListener, ResponseSender {

    private val cooldowns = ConcurrentHashMap<UUID, Long>()
    private const val COOLDOWN_MS = 100L

    // 统一定义命名空间
    private const val NAMESPACE = "ashandlight"

    fun register() {
        val messenger = Bukkit.getMessenger()
        val plugin = BukkitPlugin.getInstance()

        // 注册接收频道 (C2S)
        messenger.registerIncomingPluginChannel(plugin, "$NAMESPACE:auction", this)

        // 注册发送频道 (S2C) - 必须包含 Lua 监听的所有频道名
        messenger.registerOutgoingPluginChannel(plugin, "$NAMESPACE:auction_data")
        messenger.registerOutgoingPluginChannel(plugin, "$NAMESPACE:data_packet")
        messenger.registerOutgoingPluginChannel(plugin, "$NAMESPACE:auction_bid_data")
        messenger.registerOutgoingPluginChannel(plugin, "$NAMESPACE:auction_bid_result")
        messenger.registerOutgoingPluginChannel(plugin, "$NAMESPACE:player_info")
        messenger.registerOutgoingPluginChannel(plugin, "$NAMESPACE:auction_mylistings_data")
        messenger.registerOutgoingPluginChannel(plugin, "$NAMESPACE:auction_cancel_result")
        messenger.registerOutgoingPluginChannel(plugin, "$NAMESPACE:auction_history_data")
        messenger.registerOutgoingPluginChannel(plugin, "$NAMESPACE:auction_create_result")
        messenger.registerOutgoingPluginChannel(plugin, "$NAMESPACE:auction_logs_data")
    }

    fun unregister() {
        val messenger = Bukkit.getMessenger()
        val plugin = BukkitPlugin.getInstance()
        messenger.unregisterIncomingPluginChannel(plugin, "$NAMESPACE:auction")
        messenger.unregisterOutgoingPluginChannel(plugin, "$NAMESPACE:data_packet")
        messenger.unregisterOutgoingPluginChannel(plugin, "$NAMESPACE:auction_bid_data")
        messenger.unregisterOutgoingPluginChannel(plugin, "$NAMESPACE:auction_bid_result")
        messenger.unregisterOutgoingPluginChannel(plugin, "$NAMESPACE:auction_mylistings_data")
        messenger.unregisterOutgoingPluginChannel(plugin, "$NAMESPACE:auction_cancel_result")
        messenger.unregisterOutgoingPluginChannel(plugin, "$NAMESPACE:player_info")
        messenger.unregisterOutgoingPluginChannel(plugin, "$NAMESPACE:auction_history_data")
        messenger.unregisterOutgoingPluginChannel(plugin, "$NAMESPACE:auction_create_result")
        messenger.unregisterOutgoingPluginChannel(plugin, "$NAMESPACE:auction_logs_data")
        MessageHandlerRegistry.clear()
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != "$NAMESPACE:auction") return

        val now = System.currentTimeMillis()
        if (now - (cooldowns[player.uniqueId] ?: 0L) < COOLDOWN_MS) return
        cooldowns[player.uniqueId] = now

        val stream = DataInputStream(ByteArrayInputStream(message))
        try {
            val key = stream.readMcString()
            val value = stream.readMcString()
            info("[收包] player=${player.name} key=$key value=$value")
            
            // 使用注册表分发消息
            val handler = MessageHandlerRegistry.getHandler(key)
            if (handler != null) {
                handler.handle(player, value, this)
            } else {
                info("[警告] 未找到处理器: key=$key")
            }
        } catch (e: Exception) {
            info("[错误] 解析失败: ${e.message}")
        }
    }

    // 实现 ResponseSender 接口
    override fun send(player: Player, channel: String, data: String) {
        sendData(player, channel, data)
    }

    override fun sendOpenUI(player: Player, uiId: String) {
        val out = ByteArrayOutputStream()
        val stream = DataOutputStream(out)
        stream.writeMcString("OpenUI")
        stream.writeMcString(uiId)
        player.sendPluginMessage(BukkitPlugin.getInstance(), "$NAMESPACE:data_packet", out.toByteArray())
    }

    /**
     * 发送数据包给客户端
     * @param player 目标玩家
     * @param targetChannel 目标频道 (如 ashandlight:auction_bid_data)
     * @param data JSON数据
     */
    fun sendData(player: Player, targetChannel: String, data: String) {
        val out = ByteArrayOutputStream()
        val stream = DataOutputStream(out)
        stream.writeMcString(data)
        val bytes = out.toByteArray()
        // 限制日志长度，避免日志过长
        val logData = if (data.length > 1000) "${data.substring(0, 1000)}..." else data
        info("[发包] channel=$targetChannel player=${player.name} bytes=${bytes.size} data=$logData")
        try {
            player.sendPluginMessage(BukkitPlugin.getInstance(), targetChannel, bytes)
            info("[发包成功] $targetChannel")
        } catch (e: Exception) {
            info("[发包异常] ${e::class.simpleName}: ${e.message}")
        }
    }
}