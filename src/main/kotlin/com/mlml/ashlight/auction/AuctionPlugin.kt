package com.mlml.ashlight.auction

import com.mlml.ashlight.auction.command.AhCommand
import com.mlml.ashlight.auction.core.config.PluginConfig
import com.mlml.ashlight.auction.core.database.RedisManager
import com.mlml.ashlight.auction.network.ChannelRegistry
import com.mlml.ashlight.auction.utils.Database
import org.bukkit.Bukkit
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin

@PlatformSide(Platform.BUKKIT)
object AuctionPlugin : Plugin() {

    override fun onEnable() {
        PluginConfig.load()
        Database.connect()
        RedisManager.connect()
        ChannelRegistry.register()
        Bukkit.getPluginCommand("ah")?.setExecutor(AhCommand)
        Bukkit.getConsoleSender().sendMessage("§a[拍卖行] 启动完毕！输入 /ah 打开界面。")
    }

    override fun onDisable() {
        ChannelRegistry.unregister()
        RedisManager.disconnect()
        Database.disconnect()
    }
}
