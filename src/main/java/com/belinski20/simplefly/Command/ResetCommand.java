package com.belinski20.simplefly.Command;

import com.belinski20.simplefly.SimpleFly;
import java.io.IOException;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResetCommand implements TabExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            SimpleFly.s.fManager.flyReset();
            sender.sendMessage(Component.text().content("Player Fly file has been reset").build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
