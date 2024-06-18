package com.belinski20.simplefly;

import java.io.IOException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class FlyEvents implements Listener {
    @EventHandler
    public void OnDisconnect(PlayerQuitEvent event) throws IOException {
        Player player = event.getPlayer();
        if (SimpleFly.s.fManager.flyContains(player)) {
            SimpleFly.s.fManager.stopFly(player);
            SimpleFly.s.fManager.fallingPlayers.remove(player);
        }
    }

    @EventHandler
    public void OnDimensionTravel(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (SimpleFly.s.fManager.flyContains(player))
            player.setAllowFlight(true);
    }

    @EventHandler
    public void OnTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (SimpleFly.s.fManager.flyContains(player))
            player.setAllowFlight(true);
    }

    @EventHandler
    public void OnFallFromFly(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;
        Player player = (Player)event.getEntity();
        if (SimpleFly.s.fManager.fallingPlayers.remove(player))
            event.setCancelled(true);
    }
}
