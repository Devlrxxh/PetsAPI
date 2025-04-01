package dev.lrxh.petsAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PetInteractEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Pet pet;
    private final Player player;

    public PetInteractEvent(Player player, Pet pet) {
        this.pet = pet;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Pet getPet() {
        return pet;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}