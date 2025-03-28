package dev.lrxh.petsAPI;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PetsListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PetsAPI.load(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PetsAPI.kill(event.getPlayer());
    }
}