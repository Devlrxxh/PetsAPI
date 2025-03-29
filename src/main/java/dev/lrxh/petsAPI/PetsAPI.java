package dev.lrxh.petsAPI;

import com.github.retrooper.packetevents.PacketEvents;
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
    private static HashMap<UUID, List<PlayerPet>> playerPets;
    private static Set<Pet> pets;
    private static List<UUID> ignorePlayers;

    public static void init(JavaPlugin plugin) {
        PacketEvents.getAPI().init();

        EntityLib.init(
                new SpigotEntityLibPlatform(plugin),
                new APIConfig(PacketEvents.getAPI()));
        instance = plugin;
        playerPets = new HashMap<>();
        skinData = new HashMap<>();
        pets = new HashSet<>();
        ignorePlayers = new ArrayList<>();

        ViewerEngine viewerEngine = new ViewerEngine();
        viewerEngine.enable();
        viewerEngine.addViewerRule(user -> !ignorePlayers.contains(user.getUUID()));

        instance.getServer().getPluginManager().registerEvents(new PetsListener(), instance);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (List<PlayerPet> pets : playerPets.values()) {
                for (Pet pet : pets) {
                    pet.tick();
                }
            }

            for (Pet pet : pets) {
                pet.tick();
            }
        }, 0L, 2L);
    }


    public static void addIgnoredPlayer(Player player) {
        ignorePlayers.add(player.getUniqueId());
    }

    public static void removeIgnoredPlayer(Player player) {
        ignorePlayers.remove(player.getUniqueId());

        load(player);
    }

    static void add(Pet pet) {
        pets.add(pet);

        for (Player player : Bukkit.getOnlinePlayers()) {
            load(player, pet);
        }
    }

    static void add(Player player, PlayerPet pet) {
        if (playerPets.containsKey(player.getUniqueId())) {
            playerPets.get(player.getUniqueId()).add(pet);
        } else {
            ArrayList<PlayerPet> list = new ArrayList<>();
            list.add(pet);
            playerPets.put(player.getUniqueId(), list);
        }

        load(player, pet);
    }

    static void load(Player player) {
        for (List<PlayerPet> pets : playerPets.values()) {
            for (Pet pet : pets) {
                load(player, pet);
            }
        }

        for (Pet pet : pets) {
            load(player, pet);
        }
    }

    static void load(Player player, Pet pet) {
        pet.getEntity().addViewer(player.getUniqueId());
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, pet.getEquip());
    }

    static void kill(Player player) {
        for (Pet pet : getPets(player)) {
            pet.getEntity().removeViewer(player.getUniqueId());
            pet.getEntity().despawn();
            pet.getEntity().remove();
            playerPets.remove(player.getUniqueId());
        }

        for (Pet pet : pets) {
            pet.getEntity().removeViewer(player.getUniqueId());
        }
    }

    static void kill(Pet pet) {
        pet.getEntity().despawn();
        pet.getEntity().remove();
        pets.remove(pet);
    }

    public static List<Pet> getPets(Player player) {
        if (!playerPets.containsKey(player.getUniqueId())) return new ArrayList<>();

        return new ArrayList<>(playerPets.get(player.getUniqueId()));
    }
}