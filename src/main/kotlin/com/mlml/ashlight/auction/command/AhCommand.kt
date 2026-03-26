package com.mlml.ashlight.auction.command

import com.mlml.ashlight.auction.core.network.ResponseSender
import com.mlml.ashlight.auction.network.ChannelRegistry
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * 拍卖行命令处理器
 * 通过 ResponseSender 接口与网络层交互
 */
object AhCommand : CommandExecutor {
    
    // 通过依赖注入获取 ResponseSender 实现
    private val sender: ResponseSender get() = ChannelRegistry
    
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§c只有玩家可以使用此命令！")
            return true
        }

        this.sender.sendOpenUI(sender, "main")
        sender.sendMessage("§a[拍卖行] 正在打开界面...")
        return true
    }
}