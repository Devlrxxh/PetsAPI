package dev.lrxh.petsAPI;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class PetsAPI {
    public static JavaPlugin instance;
    static HashMap<String, SkinData> skinData;
    private static HashMap<UUID, List<Pet>> pets;
    private static HashMap<UUID, List<MoveRunnable>> runnable;

    public static void init(JavaPlugin plugin) {
        PacketEvents.getAPI().init();

        EntityLib.init(
                new SpigotEntityLibPlatform(plugin),
                new APIConfig(PacketEvents.getAPI()));
        instance = plugin;
        pets = new HashMap<>();
        runnable = new HashMap<>();
        skinData = new HashMap<>();

        instance.getServer().getPluginManager().registerEvents(new PetsListener(), instance);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (List<MoveRunnable> runnable : runnable.values()) {
                for (MoveRunnable moveRunnable : runnable) {
                    moveRunnable.tick();
                }
            }
        }, 0L, 1L);
    }

    static void add(Player player, Pet pet) {
        if (!pets.containsKey(player.getUniqueId())) {
            List<Pet> pets = new ArrayList<>();
            pets.add(pet);
            PetsAPI.pets.put(player.getUniqueId(), pets);
        } else {
            pets.get(player.getUniqueId()).add(pet);
        }

        if (!runnable.containsKey(player.getUniqueId())) {
            List<MoveRunnable> runnables = new ArrayList<>();
            runnables.add(new MoveRunnable(pet));
            PetsAPI.runnable.put(player.getUniqueId(), runnables);
        } else {
            runnable.get(player.getUniqueId()).add(new MoveRunnable(pet));
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.canSee(player)) continue;
            load(online);
        }
    }

    static void load(Player player) {
        for (List<Pet> pets : pets.values()) {
            for (Pet pet : pets) {
                pet.getEntity().addViewer(player.getUniqueId());
                for (PacketWrapper packet : pet.getPackets()) {
                    PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
                }
            }
        }
    }

    static void load(Player player, Player watcher) {
        if (!pets.containsKey(player.getUniqueId())) return;
        for (Pet pet : pets.get(player.getUniqueId())) {
            pet.getEntity().addViewer(player.getUniqueId());
            for (PacketWrapper packet : pet.getPackets()) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(watcher, packet);
            }
        }
    }

    static void hide(Player player, Player watcher) {
        if (!pets.containsKey(player.getUniqueId())) return;
        for (Pet pet : pets.get(player.getUniqueId())) {
            pet.getEntity().removeViewer(watcher.getUniqueId());
        }
    }

    static void kill(Player player) {
        if (!pets.containsKey(player.getUniqueId())) return;
        for (Pet pet : pets.get(player.getUniqueId())) {
            pet.getEntity().despawn();
        }

        pets.remove(player.getUniqueId());
        runnable.remove(player.getUniqueId());
    }

    static void kill(Pet pet) {
        pet.getEntity().despawn();

        for (UUID uuid : new ArrayList<>(pets.keySet())) {
            pets.get(uuid).remove(pet);
        }

        for (UUID uuid : new ArrayList<>(runnable.keySet())) {
            runnable.get(uuid).removeIf(moveRunnable -> moveRunnable.pet.equals(pet));
        }
    }

    public static List<Pet> getPets(Player player) {
        if (!pets.containsKey(player.getUniqueId())) return new ArrayList<>();

        return new ArrayList<>(pets.get(player.getUniqueId()));
    }
}
