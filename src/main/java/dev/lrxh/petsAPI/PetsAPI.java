package dev.lrxh.petsAPI;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import me.tofaa.entitylib.ve.ViewerEngine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class PetsAPI {
    public static JavaPlugin instance;
    static HashMap<String, SkinData> skinData;
    private static HashMap<UUID, List<Pet>> pets;
    private static List<UUID> ignorePlayers;

    public static void init(JavaPlugin plugin) {
        PacketEvents.getAPI().init();

        EntityLib.init(
                new SpigotEntityLibPlatform(plugin),
                new APIConfig(PacketEvents.getAPI()));
        instance = plugin;
        pets = new HashMap<>();
        skinData = new HashMap<>();
        ignorePlayers = new ArrayList<>();

        ViewerEngine viewerEngine = new ViewerEngine();
        viewerEngine.enable();
        viewerEngine.addViewerRule(user -> !ignorePlayers.contains(user.getUUID()));

        instance.getServer().getPluginManager().registerEvents(new PetsListener(), instance);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (List<Pet> pets : pets.values()) {
                for (Pet pet : pets) {
                    MoveRunnable moveRunnable = pet.getMoveRunnable();
                    moveRunnable.tick();
                }
            }
        }, 1L);
    }


    public static void addIgnoredPlayer(Player player) {
        ignorePlayers.add(player.getUniqueId());
    }

    public static void removeIgnoredPlayer(Player player) {
        ignorePlayers.remove(player.getUniqueId());
    }

    static void add(Player player, Pet pet) {
        if (pets.containsKey(player.getUniqueId())) {
            pets.get(player.getUniqueId()).add(pet);
        } else {
            ArrayList<Pet> list = new ArrayList<>();
            list.add(pet);
            pets.put(player.getUniqueId(), list);
        }

        load(player, pet);
    }

    static void load(Player player) {
        for (List<Pet> pets : pets.values()) {
            for (Pet pet : pets) {
                load(player, pet);
            }
        }
    }

    static void load(Player player, Pet pet) {
        pet.getEntity().addViewer(player.getUniqueId());
        for (PacketWrapper packet : pet.getPackets()) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        }
    }

    static void kill(Player player) {
        if (!pets.containsKey(player.getUniqueId())) return;
        for (Pet pet : getPets(player)) {
            pet.getEntity().despawn();
            pets.remove(player.getUniqueId());
        }
    }

    public static List<Pet> getPets(Player player) {
        if (!pets.containsKey(player.getUniqueId())) return new ArrayList<>();

        return new ArrayList<>(pets.get(player.getUniqueId()));
    }
}