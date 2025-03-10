package dev.lrxh.petsAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHideEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShowEntityEvent;

public class PetsListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PetsAPI.load(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PetsAPI.kill(event.getPlayer());
    }

    @EventHandler
    public void onShow(PlayerShowEntityEvent event) {
        if (event.getEntity() instanceof Player player)
            PetsAPI.load(player, event.getPlayer());
    }

    @EventHandler
    public void onHide(PlayerHideEntityEvent event) {
        if (event.getEntity() instanceof Player player)
            PetsAPI.hide(player, event.getPlayer());
    }
}
