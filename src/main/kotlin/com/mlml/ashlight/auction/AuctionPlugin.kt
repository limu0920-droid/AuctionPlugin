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
import taboolib.common.platform.function.severe
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration


@PlatformSide(Platform.BUKKIT)
object AuctionPlugin : Plugin() {
    @Config("config.yml")
    lateinit var config: Configuration

    @Override
    override fun onEnable() {
        PluginConfig.load()
        try {
            Database.connect()
        } catch (e: Exception) {
            severe("[拍卖行] MySQL 连接失败: ${e.message}")
            severe("[拍卖行] 请检查 config.yml 中的 mysql 配置，插件将在无数据库模式下运行")
        }
        try {
            RedisManager.connect()
        } catch (e: Exception) {
            severe("[拍卖行] Redis 连接失败: ${e.message}")
            severe("[拍卖行] 请检查 config.yml 中的 redis 配置，缓存功能将不可用")
        }
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

