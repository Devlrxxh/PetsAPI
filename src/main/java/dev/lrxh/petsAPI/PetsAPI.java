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
    static HashMap<String, SkinData> skinDatas;
    private static HashMap<UUID, List<Pet>> pets;
    private static HashMap<UUID, List<MoveRunnable>> runnables;

    public static void init(JavaPlugin plugin) {
        PacketEvents.getAPI().init();

        EntityLib.init(
                new SpigotEntityLibPlatform(plugin),
                new APIConfig(PacketEvents.getAPI()));
        instance = plugin;
        pets = new HashMap<>();
        runnables = new HashMap<>();
        skinDatas = new HashMap<>();

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (List<MoveRunnable> runnable : runnables.values()) {
                for (MoveRunnable moveRunnable : runnable) {
                    Bukkit.getScheduler().runTask(plugin, moveRunnable);
                }
            }
        }, 0L, 2L);
    }

    static void add(Player player, Pet pet) {
        if (!pets.containsKey(player.getUniqueId())) {
            List<Pet> pets = new ArrayList<>();
            pets.add(pet);
            PetsAPI.pets.put(player.getUniqueId(), pets);
        } else {
            pets.get(player.getUniqueId()).add(pet);
        }

        if (!runnables.containsKey(player.getUniqueId())) {
            List<MoveRunnable> runnables = new ArrayList<>();
            runnables.add(new MoveRunnable(player, pet));
            PetsAPI.runnables.put(player.getUniqueId(), runnables);
        } else {
            runnables.get(player.getUniqueId()).add(new MoveRunnable(player, pet));
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
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

    static void kill(Player player) {
        for (Pet pet : pets.get(player.getUniqueId())) {
            pet.getEntity().despawn();
        }

        pets.remove(player.getUniqueId());
        runnables.remove(player.getUniqueId());
    }

    static void kill(Pet pet) {
        pet.getEntity().despawn();

        for (UUID uuid : new ArrayList<>(pets.keySet())) {
            pets.get(uuid).remove(pet);
        }

        for (UUID uuid : new ArrayList<>(runnables.keySet())) {
            runnables.get(uuid).removeIf(moveRunnable -> moveRunnable.pet.equals(pet));
        }
    }

    public static List<Pet> getPets(Player player) {
        if (!pets.containsKey(player.getUniqueId())) return new ArrayList<>();

        return new ArrayList<>(pets.get(player.getUniqueId()));
    }
}
