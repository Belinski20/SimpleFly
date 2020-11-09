package com.belinski20.simplefly;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;

public class FlyEvents implements Listener
{
    @EventHandler
    public void OnDisconnect(PlayerQuitEvent event) throws IOException {
        Player player = event.getPlayer();
        if(SimpleFly.s.fManager.flyContains(player))
            SimpleFly.s.fManager.stopFly(player);
    }
}
