package com.belinski20.simplefly;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FlyManager
{
    private Map<Player, PlayerData> flyingPlayers;
    private Map<Player, Integer> playerTasks;

    public FlyManager() {
        flyingPlayers = new HashMap<>();
        playerTasks = new HashMap<>();
    }

    public boolean flyContains(Player player)
    {
        return flyingPlayers.containsKey(player);
    }

    public long getFlyTime(Player player)
    {
        return flyingPlayers.get(player).getFlyTime();
    }

    public void startFly(Player player) throws IOException {
        FileConfiguration config;
        File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
        long time = 0;
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            if(config.get("Players." + player.getUniqueId()) == null)
            {
                int rank = config.getInt("Ranks." + SimpleFly.s.perms.getPrimaryGroup(player));
                time = rank * 60;
                config.set("Players." + player.getUniqueId(), time);
                config.save(file);
            }
            else
            {
                time = config.getLong("Players." + player.getUniqueId());
            }
            flyingPlayers.put(player, new PlayerData(time));
        }
        if(!flyContains(player))
            playerTasks.remove(player);
        int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(SimpleFly.s, () -> {
            //noinspection deprecation
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Fly available: " + toTime(flyingPlayers.get(player).getFlyTime())));
            if(!player.isFlying() && isFlying(player))
            {
                pauseTime(player);
                return;
            }
            if(player.isFlying() && !isFlying(player))
            {
                resumeTime(player);
                return;
            }
            if(flyContains(player) && isFlying(player))
            {
                if(hasFlyTime(player))
                    decrementTime(player);
                else {
                    try {
                        stopFly(player);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 20, 20);
        playerTasks.put(player, id);
    }

    public String toTime(long time)
    {
        long minute = time / 60;
        long second = time - (minute * 60);
        String sMinute = String.valueOf(minute);
        String sSecond =  String.valueOf(second);
        if(second < 10)
            sSecond = "0" + sSecond;
        return sMinute + ":" + sSecond;
    }

    public void stopFly(Player player) throws IOException {
        saveTime(player);
        player.setFlying(false);
        player.setAllowFlight(false);
        flyingPlayers.remove(player);
        Bukkit.getScheduler().cancelTask(playerTasks.get(player));
        playerTasks.remove(player);
        if(player.isOnline() && aboveGround(player))
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 15, 999));
    }

    public boolean hasFlyTime(Player player)
    {
        return getFlyTime(player) > 0;
    }

    public void saveTime(Player player) throws IOException {
        FileConfiguration config;
        File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            config.set("Players." + player.getUniqueId(), flyingPlayers.get(player).getFlyTime());
            config.save(file);
        }
    }

    public void saveForReset() throws IOException {
        for(Player player : flyingPlayers.keySet())
        {
            saveTime(player);
        }
    }

    public void pauseTime(Player player){
        if(flyingPlayers.get(player).isFlying())
            flyingPlayers.get(player).setFlying(false);
    }

    public void resumeTime(Player player){
        if(!flyingPlayers.get(player).isFlying())
            flyingPlayers.get(player).setFlying(true);
    }

    public void decrementTime(Player player)
    {
        flyingPlayers.get(player).decrementTime();
    }

    public boolean isFlying(Player player)
    {
        return flyingPlayers.get(player).isFlying();
    }

    public void timerReset() throws IOException {
        FileConfiguration config;
        File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
        for(Player player : flyingPlayers.keySet())
        {
            stopFly(player);
        }
        config = YamlConfiguration.loadConfiguration(file);
        config.set("Players", null);
        config.save(file);
    }

    public boolean aboveGround(Player player)
    {
        Location pLoc = player.getLocation();
        return player.getWorld().getBlockAt(pLoc.getBlockX(), pLoc.getBlockY() - 1, pLoc.getBlockZ()).isEmpty();
    }

    public PlayerData getData(Player player)
    {
        return flyingPlayers.get(player);
    }

    public boolean fileContain(Player player)
    {
        FileConfiguration config;
        File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            return config.contains("Players." + player.getUniqueId());
        }
        return false;
    }

    public void addTime(Player player, int timeToAdd) throws IOException {
        long flyTime = 0;
        FileConfiguration config;
        File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            flyTime = config.getLong("Players." + player.getUniqueId());
            flyTime += (timeToAdd * 60);
            config.set("Players." + player.getUniqueId(), flyTime);
            config.save(file);
        }
    }
}
