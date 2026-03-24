package com.mlml.ashlight.auction.command

import com.mlml.ashlight.auction.network.ChannelRegistry
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object AhCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§c只有玩家可以使用此命令！")
            return true
        }
        ChannelRegistry.sendRpc(sender, "ui open", "main")//LegendEngine ui open main 命令
        sender.sendMessage("§a[拍卖行] 正在打开界面...")
        return true
    }
}
