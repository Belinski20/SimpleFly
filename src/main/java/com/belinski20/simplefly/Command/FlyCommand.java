package com.belinski20.simplefly.Command;

import com.belinski20.simplefly.SimpleFly;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

public class FlyCommand implements TabExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "Fly cannot be run from console");
            return true;
        }
        Player player = (Player) sender;
        if(!SimpleFly.s.fManager.flyContains(player))
        {
            try {
                SimpleFly.s.fManager.startFly(player);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(SimpleFly.s.fManager.hasFlyTime(player)) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[SimpleFly] " + player.getName() + " has activated fly");
                sender.sendMessage(ChatColor.AQUA + "SimpleFly" + ChatColor.WHITE + " : " + ChatColor.GREEN + "active");
                player.setAllowFlight(true);
                player.setFlying(true);
            }
            else
            {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[SimpleFly] " + player.getName() + " tried to fly but has no time remaining");
                sender.sendMessage(ChatColor.RED + "No Fly Time Left For Today!");
                try {
                    SimpleFly.s.fManager.stopFly(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[SimpleFly] " + player.getName() + " has deactivated fly");
            sender.sendMessage(ChatColor.AQUA + "SimpleFly" + ChatColor.WHITE + " : " + ChatColor.RED + "deactived");
            player.setFlying(false);
            player.setAllowFlight(false);
            try {
                SimpleFly.s.fManager.stopFly(player);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
