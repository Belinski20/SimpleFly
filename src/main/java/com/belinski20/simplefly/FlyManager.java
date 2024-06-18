package com.belinski20.simplefly;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FlyManager {
    private final Map<Player, PlayerData> flyingPlayers = new HashMap<>();

    private final Map<Player, BossBar> bars = new HashMap<>();

    public List<Player> fallingPlayers = new LinkedList<>();

    public boolean flyContains(Player player) {
        return this.flyingPlayers.containsKey(player);
    }

    public long getFlyTime(Player player) {
        return this.flyingPlayers.get(player).getFlyTime();
    }

    public void startFly(final Player player, final String timerPlacement) throws IOException {
        File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
        long time = 0L;
        if (file.exists()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            if (yamlConfiguration.get("Players." + player.getUniqueId()) == null) {
                int rank = yamlConfiguration.getInt("Ranks." + SimpleFly.s.perms.getPrimaryGroup(player));
                time = (rank * 60L);
                yamlConfiguration.set("Players." + player.getUniqueId(), time);
                yamlConfiguration.save(file);
            } else {
                time = yamlConfiguration.getLong("Players." + player.getUniqueId());
            }
            this.flyingPlayers.put(player, new PlayerData(time));
        }
        if (time <= 0L)
            return;
        final long timer = time;
        final BossBar bar = makeFlyTimer(this.flyingPlayers.get(player).getFlyTime(), timer);
        if (!timerPlacement.equalsIgnoreCase("bottom")) {
            this.bars.put(player, bar);
            player.showBossBar(bar);
        }
        (new BukkitRunnable() {
            long time = timer;

            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                if (FlyManager.this.flyingPlayers.get(player) == null) {
                    cancel();
                    return;
                }
                if (FlyManager.this.flyingPlayers.get(player).getFlyTime() + timer > timer)
                    this.time = FlyManager.this.flyingPlayers.get(player).getFlyTime();
                if (timerPlacement.equalsIgnoreCase("bottom")) {
                    player.sendActionBar(Component.text().content("Fly available: ").color(NamedTextColor.AQUA).append(Component.text().content(FlyManager.this.toTime(FlyManager.this.flyingPlayers.get(player).getFlyTime())).color(NamedTextColor.GREEN)).build());
                } else {
                    bar.progress((float) FlyManager.this.flyingPlayers.get(player).getFlyTime() / (float)this.time);
                    bar.name(Component.text().content(FlyManager.this.toTime(FlyManager.this.flyingPlayers.get(player).getFlyTime())).color(NamedTextColor.GREEN).build());
                }
                if (!player.isFlying() && FlyManager.this.isFlying(player)) {
                    FlyManager.this.pauseTime(player);
                    return;
                }
                if (player.isFlying() && !FlyManager.this.isFlying(player)) {
                    FlyManager.this.resumeTime(player);
                    return;
                }
                if (FlyManager.this.flyContains(player) && FlyManager.this.isFlying(player))
                    if (FlyManager.this.hasFlyTime(player)) {
                        FlyManager.this.decrementTime(player);
                    } else {
                        try {
                            FlyManager.this.stopFly(player);
                            cancel();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }).runTaskTimerAsynchronously(SimpleFly.s, 0L, 20L);
    }

    private BossBar makeFlyTimer(long amount, long totalTime) {
        TextComponent textComponent = Component.text(toTime(amount));
        return BossBar.bossBar(textComponent, (float)(amount / totalTime), BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
    }

    public String toTime(long time) {
        long minute = time / 60L;
        long second = time - minute * 60L;
        String sMinute = String.valueOf(minute);
        String sSecond = String.valueOf(second);
        if (second < 10L)
            sSecond = "0" + sSecond;
        return sMinute + ":" + sSecond;
    }

    public void stopFly(Player player) throws IOException {
        saveTime(player);
        player.setFlying(false);
        player.setAllowFlight(false);
        if (this.bars.containsKey(player)) {
            if (player != null)
                this.bars.get(player).removeViewer(player);
            this.bars.remove(player);
        }
        this.flyingPlayers.remove(player);
        if (player.isOnline() && aboveGround(player))
            this.fallingPlayers.add(player);
    }

    public boolean hasFlyTime(Player player) {
        return (getFlyTime(player) > 0L);
    }

    public void saveTime(Player player) throws IOException {
        File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
        if (file.exists()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            yamlConfiguration.set("Players." + player.getUniqueId(), this.flyingPlayers.get(player).getFlyTime());
            yamlConfiguration.save(file);
        }
    }

    public void saveForReset() throws IOException {
        for (Player player : this.flyingPlayers.keySet())
            saveTime(player);
    }

    public void pauseTime(Player player) {
        if (this.flyingPlayers.get(player).isFlying())
            this.flyingPlayers.get(player).setFlying(false);
    }

    public void resumeTime(Player player) {
        if (!this.flyingPlayers.get(player).isFlying())
            this.flyingPlayers.get(player).setFlying(true);
    }

    public void decrementTime(Player player) {
        this.flyingPlayers.get(player).decrementTime();
    }

    public boolean isFlying(Player player) {
        return this.flyingPlayers.get(player).isFlying();
    }

    public void flyReset() throws IOException {
        File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
        Set<Player> playerSet = new HashSet<>(this.flyingPlayers.keySet());
        for (Player player : playerSet)
            stopFly(player);
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.set("Players", null);
        yamlConfiguration.save(file);
    }

    public boolean aboveGround(Player player) {
        Location pLoc = player.getLocation();
        return player.getWorld().getBlockAt(pLoc.getBlockX(), pLoc.getBlockY() - 1, pLoc.getBlockZ()).isEmpty();
    }

    public PlayerData getData(Player player) {
        return this.flyingPlayers.get(player);
    }

    public boolean fileContain(Player player) {
        File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
        if (file.exists()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            return yamlConfiguration.contains("Players." + player.getUniqueId());
        }
        return false;
    }

    public boolean fileContain(UUID uuid) {
        File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
        if (file.exists()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            return yamlConfiguration.contains("Players." + uuid);
        }
        return false;
    }

    public void addTime(Player player, int timeToAdd) throws IOException {
        long flyTime;
        File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
        if (file.exists()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            flyTime = yamlConfiguration.getLong("Players." + player.getUniqueId());
            flyTime += (timeToAdd * 60L);
            yamlConfiguration.set("Players." + player.getUniqueId(), flyTime);
            yamlConfiguration.save(file);
        }
    }

    public void addTime(UUID uuid, int timeToAdd) throws IOException {
        long flyTime;
        File file = new File(SimpleFly.s.getDataFolder(), "Fly_Info.yml");
        if (file.exists()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            flyTime = yamlConfiguration.getLong("Players." + uuid);
            flyTime += (timeToAdd * 60L);
            yamlConfiguration.set("Players." + uuid, flyTime);
            yamlConfiguration.save(file);
        }
    }
}
