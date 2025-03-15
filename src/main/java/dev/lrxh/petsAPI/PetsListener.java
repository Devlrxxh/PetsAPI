package dev.lrxh.petsAPI;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PetsListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(PetsAPI.instance, () -> {
            PetsAPI.addIgnoredPlayer(event.getPlayer());
            PetsAPI.removeIgnoredPlayer(event.getPlayer());
        }, 10L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PetsAPI.kill(event.getPlayer());
    }
}