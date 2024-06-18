package com.belinski20.simplefly.Command;

import com.belinski20.simplefly.SimpleFly;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddCommand implements TabExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int timeToAdd;
        if (args.length != 2)
            return false;
        final OfflinePlayer player = SimpleFly.s.getServer().getOfflinePlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player with name " + args[0] + " is not found");
            return true;
        }
        try {
            timeToAdd = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + args[1] + " is not a valid number");
            return true;
        }
        if (!SimpleFly.s.fManager.fileContain(player.getUniqueId())) {
            final File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
            if (file.exists()) {
                final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                if (config.get("Players." + player.getUniqueId()) == null) {
                    final int finalTimeToAdd = timeToAdd;
                    (new BukkitRunnable() {
                        public void run() {
                            int rank = config.getInt("Ranks." + SimpleFly.s.perms.getPrimaryGroup(null, player));
                            long time = (rank * 60L);
                            config.set("Players." + player.getUniqueId(), Long.valueOf(time + finalTimeToAdd));
                            try {
                                config.save(file);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            cancel();
                        }
                    }).runTaskTimerAsynchronously(SimpleFly.s, 0L, 0L);
                }
            }
            sender.sendMessage(ChatColor.RED + args[0] + " has not flown yet today, fly entry created.");
            return true;
        }
        if (SimpleFly.s.fManager.flyContains(player.getPlayer())) {
            SimpleFly.s.fManager.getData(player.getPlayer()).incrementTime(timeToAdd);
            if (!sender.equals(player))
                sender.sendMessage(ChatColor.GREEN + args[1] + " minutes has been added to " + player.getName() + "'s fly time");
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[Simple Fly] " + sender.getName() + " has added " + timeToAdd + " minutes to " + player.getName() + "'s fly time");
            player.getPlayer().sendMessage(ChatColor.GREEN + args[1] + " minutes has been added to your fly timer");
            return true;
        }
        if (SimpleFly.s.fManager.fileContain(player.getUniqueId())) {
            try {
                SimpleFly.s.fManager.addTime(player.getUniqueId(), timeToAdd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!sender.equals(player))
                sender.sendMessage(ChatColor.GREEN + args[1] + " minutes has been added to " + player.getName() + "'s fly time");
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[Simple Fly] " + sender.getName() + " has added " + timeToAdd + " minutes to " + player.getName() + "'s fly time");
            if (player.getPlayer() != null)
                player.getPlayer().sendMessage(ChatColor.GREEN + args[1] + " minutes has been added to your fly timer");
            return true;
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2)
            return getBasicTimes();
        return null;
    }

    public ArrayList<String> getBasicTimes() {
        ArrayList<String> basicTimes = new ArrayList<>();
        basicTimes.add("15");
        basicTimes.add("30");
        basicTimes.add("60");
        basicTimes.add("90");
        return basicTimes;
    }
}
