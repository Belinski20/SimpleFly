package com.belinski20.simplefly;

import com.belinski20.simplefly.Command.AddCommand;
import com.belinski20.simplefly.Command.FlyCommand;
import com.belinski20.simplefly.Command.ResetCommand;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SimpleFly extends JavaPlugin {
    public static SimpleFly s;

    public FlyManager fManager;

    public Permission perms;

    public void onEnable() {
        getDataFolder().mkdir();
        s = this;
        this.fManager = new FlyManager();
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("aft").setExecutor(new AddCommand());
        getCommand("rft").setExecutor(new ResetCommand());
        getServer().getPluginManager().registerEvents(new FlyEvents(), this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            (new SimpleFlyPlaceholder(this.fManager)).register();
        setupPermissions();
        try {
            createFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onDisable() {
        try {
            this.fManager.saveForReset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile() throws IOException {
        File file = new File(getDataFolder(), "Fly_Info.yml");
        if (file.createNewFile()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            yamlConfiguration.set("Reset_Time", 24);
            yamlConfiguration.save(file);
            for (String rank : this.perms.getGroups()) {
                yamlConfiguration.set("Ranks." + rank, 0);
                yamlConfiguration.save(file);
            }
        }
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        this.perms = rsp.getProvider();
        if (this.perms != null) {
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[SimpleFly] found Permissions");
        } else {
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[SimpleFly] did not find Permissions");
        }
    }
}
