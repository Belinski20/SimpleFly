package com.belinski20.simplefly;

import com.belinski20.simplefly.Command.AddCommand;
import com.belinski20.simplefly.Command.FlyCommand;
import com.belinski20.simplefly.Command.ResetCommand;
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

public class SimpleFly extends JavaPlugin{

    public static SimpleFly s;
    public FlyManager fManager;
    public Permission perms;
    private boolean canReset = false;
    private int resetHour = 0;


    @Override
    public void onEnable() {
        getDataFolder().mkdir();
        s = this;
        fManager = new FlyManager();
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("aft").setExecutor(new AddCommand());
        getCommand("rft").setExecutor(new ResetCommand());
        getServer().getPluginManager().registerEvents(new FlyEvents(), this);
        setupPermissions();
        setupResetTime();
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

    public void setupResetTime()
    {
        FileConfiguration config;
        File file = new File(this.getDataFolder(), "Fly_Info.yml");
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            int resetTime = config.getInt("Reset_Time");
            if(resetTime == 24)
                resetTime = 0;
            this.resetHour = resetTime;
        }
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
        if(resetHour == 24)
            resetHour = 0;
        this.resetHour = resetHour;
    }

    private void resetFlyTime() throws IOException {
        if(resetHour - LocalTime.now().getHour() == 0 && canReset)
        {
            alertPLayers();
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[SimpleFly] Flying Time Was Reset");
            fManager.timerReset();
            canReset = false;
        }
        if(resetHour - LocalTime.now().getHour() < 0 && !canReset)
        {
            canReset = true;
        }
    }

    private void alertPLayers()
    {
        Bukkit.broadcast(ChatColor.LIGHT_PURPLE + "[SimpleFly] Fly Time was reset", "simplefly.notify");
    }
}
