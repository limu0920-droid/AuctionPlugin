package com.mlml.ashlight.auction

import com.mlml.ashlight.auction.command.AhCommand
import com.mlml.ashlight.auction.core.config.PluginConfig
import com.mlml.ashlight.auction.core.database.RedisManager
import com.mlml.ashlight.auction.core.network.MessageHandlerRegistry
import com.mlml.ashlight.auction.core.service.*
import com.mlml.ashlight.auction.network.ChannelRegistry
import com.mlml.ashlight.auction.utils.Database
import org.bukkit.Bukkit
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.severe
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitPlugin


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
        
        // 注册消息处理器
        registerMessageHandlers()
        
        ChannelRegistry.register()
        Bukkit.getPluginCommand("ah")?.setExecutor(AhCommand)
        Bukkit.getPluginManager().registerEvents(PlayerJoinListener(), BukkitPlugin.getInstance())
        Bukkit.getConsoleSender().sendMessage("§a[拍卖行] 启动完毕！输入 /ah 打开界面。")
    }

    override fun onDisable() {
        ChannelRegistry.unregister()
        RedisManager.disconnect()
        Database.disconnect()
    }
    
    /**
     * 注册所有消息处理器
     * 新增处理器只需在此处添加一行注册代码
     */
    private fun registerMessageHandlers() {
        MessageHandlerRegistry.registerAll(
            QueryService,         // 列表查询
            AuctionQueryService,  // 竞拍列表查询
            BidService,           // 竞价处理
            BuyService,           // 购买处理
            ListingService,       // 上架处理
            MyListingService,     // 我的上架查询
            CancelService,        // 取消上架
            HistoryService,       // 历史记录查询
            LogService            // 日志查询
        )
    }
}

