package com.belinski20.simplefly;

import com.belinski20.simplefly.Command.AddCommand;
import com.belinski20.simplefly.Command.FlyCommand;
import com.belinski20.simplefly.Command.ResetCommand;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

public final class SimpleFly extends JavaPlugin{

    public static SimpleFly s;
    public FlyManager fManager;
    public Permission perms;
    private boolean canReset = false;

    @Override
    public void onEnable() {
        getDataFolder().mkdir();
        s = this;
        fManager = new FlyManager();
        getCommand("sfly").setExecutor(new FlyCommand());
        getCommand("aft").setExecutor(new AddCommand());
        getCommand("rft").setExecutor(new ResetCommand());
        getServer().getPluginManager().registerEvents(new FlyEvents(), this);
        setupPermissions();

        try {
            createFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run()
            {
                try {
                    resetFlyTime();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fManager.saveForReset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 20, 20);
    }

    @Override
    public void onDisable() {
        try {
            fManager.saveForReset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile() throws IOException {
        FileConfiguration config;
        File file = new File(this.getDataFolder(), "Fly_Info.yml");
        if(file.createNewFile())
        {
            config = YamlConfiguration.loadConfiguration(file);
            config.set("Reset_Time", 24);
            config.save(file);
            for(String rank : perms.getGroups())
            {
                config.set("Ranks." + rank, 0);
                config.save(file);
            }
        }
    }

    private void setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = this.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        if(perms != null)
            this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[SimpleFly] found Permissions");
        else
            this.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[SimpleFly] did not find Permissions");
    }

    public void setResetTime(int resetHour) throws IOException {
        FileConfiguration config;
        File file = new File(this.getDataFolder(), "Fly_Info.yml");
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            config.set("Reset_Time", resetHour);
            config.save(file);
        }
    }

    private void resetFlyTime() throws IOException {
        FileConfiguration config;
        File file = new File(this.getDataFolder(), "Fly_Info.yml");
        int resetHour = 0;
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            resetHour = config.getInt("Reset_Time");
        }

        if(!canReset)
        {
            if(resetHour > LocalTime.now().getHour() + 1)
            {
                canReset = true;
            }
        }

        if(canReset)
        {
            if(resetHour < LocalTime.now().getHour() + 1)
            {
                getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[SimpleFly] Flying Time Was Reset");
                fManager.timerReset();
                canReset = false;
            }
        }
    }
}
