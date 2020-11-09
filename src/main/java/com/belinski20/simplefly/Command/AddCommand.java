package com.belinski20.simplefly.Command;

import com.belinski20.simplefly.SimpleFly;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int timeToAdd = 0;
        if(args.length != 2)
            return false;
        Player player = SimpleFly.s.getServer().getPlayer(args[0]);
        if(player == null)
        {
            sender.sendMessage(ChatColor.RED + "Player with name " + args[0] + " is not online");
            return true;
        }
        if(!SimpleFly.s.fManager.fileContain(player))
        {
            sender.sendMessage(ChatColor.RED + args[0] + " has not flown yet today");
            return true;
        }
        try
        {
            timeToAdd = Integer.parseInt(args[1]);
        }
        catch(NumberFormatException e)
        {
            sender.sendMessage(ChatColor.RED + args[1] + " is not a valid number");
            return true;
        }
        if(SimpleFly.s.fManager.flyContains(player))
        {
            SimpleFly.s.fManager.getData(player).incrementTime(timeToAdd);
            if(!sender.equals(player))
                sender.sendMessage(ChatColor.GREEN + args[1] + " has been added to " + player.getName());
            player.sendMessage(ChatColor.GREEN + args[1] + " minutes has been added to your fly timer");
            return true;
        }
        if(SimpleFly.s.fManager.fileContain(player))
        {
            try {
                SimpleFly.s.fManager.addTime(player, timeToAdd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!sender.equals(player))
                sender.sendMessage(ChatColor.GREEN + args[1] + " has been added to " + player.getName());
            player.sendMessage(ChatColor.GREEN + args[1] + " minutes has been added to your fly timer");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 2)
            return getBasicTimes();
        return null;
    }

    public ArrayList<String> getBasicTimes()
    {
        ArrayList<String> basicTimes = new ArrayList<>();
        basicTimes.add("15");
        basicTimes.add("30");
        basicTimes.add("60");
        basicTimes.add("90");
        return basicTimes;
    }
}
