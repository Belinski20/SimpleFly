package com.belinski20.simplefly.Command;

import com.belinski20.simplefly.SimpleFly;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class FlyCommand implements TabExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text().content("Fly cannot be run from console").color(NamedTextColor.RED).build());
            return true;
        }
        Player player = (Player)sender;
        if (!SimpleFly.s.fManager.flyContains(player)) {
            try {
                if (args.length == 0) {
                    SimpleFly.s.fManager.startFly(player, "top");
                } else {
                    SimpleFly.s.fManager.startFly(player, args[0]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (SimpleFly.s.fManager.hasFlyTime(player)) {
                Bukkit.getServer().getConsoleSender().sendMessage(Component.text().content("[SimpleFly] " + player.getName() + " has activated fly").color(NamedTextColor.LIGHT_PURPLE).build());
                sender.sendMessage(Component.text().content("SimpleFly").color(NamedTextColor.AQUA).append(Component.text().content(" : ").color(NamedTextColor.WHITE)).append(Component.text().content("active").color(NamedTextColor.GREEN)).build());
                player.setAllowFlight(true);
                player.setFlying(true);
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(Component.text().content("[SimpleFly] " + player.getName() + " tried to fly but has no time remaining").color(NamedTextColor.LIGHT_PURPLE).build());
                sender.sendMessage(Component.text().content("No Fly Time Left For Today!").color(NamedTextColor.RED).build());
                try {
                    SimpleFly.s.fManager.stopFly(player);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(Component.text().content("[SimpleFly] " + player.getName() + " has deactivated fly.").color(NamedTextColor.LIGHT_PURPLE).append(Component.text().content(" Remaining Time :").color(NamedTextColor.WHITE).append(Component.text().content(SimpleFly.s.fManager.toTime(SimpleFly.s.fManager.getFlyTime(player)))).color(NamedTextColor.GOLD)).build());
            sender.sendMessage(Component.text().content("SimpleFly").color(NamedTextColor.AQUA).append(Component.text().content(" : ").color(NamedTextColor.WHITE)).append(Component.text().content("deactivated").color(NamedTextColor.RED)).build());
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

    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (strings.length == 1)
            return Arrays.asList("top", "bottom");
        return null;
    }
}
