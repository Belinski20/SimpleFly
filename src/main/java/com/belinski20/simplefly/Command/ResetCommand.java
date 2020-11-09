package com.belinski20.simplefly.Command;

import com.belinski20.simplefly.SimpleFly;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.io.IOException;
import java.util.List;

public class ResetCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int newResetTime = 0;
        if(args.length != 1)
            return false;
        try
        {
            newResetTime = Integer.parseInt(args[0]);
        }
        catch(NumberFormatException e)
        {
            sender.sendMessage(ChatColor.RED + args[0] + " is not a valid number");
            return true;
        }
        if(0 >= newResetTime || 24 < newResetTime)
        {
            sender.sendMessage(ChatColor.RED + args[0] + " is not a valid time");
            return true;
        }
        try {
            SimpleFly.s.setResetTime(newResetTime);
            sender.sendMessage(ChatColor.GREEN + "Fly Reset Time has been set to " + args[0]);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
