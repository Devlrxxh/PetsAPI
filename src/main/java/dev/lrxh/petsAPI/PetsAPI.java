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
import java.util.concurrent.CopyOnWriteArrayList;

public final class PetsAPI {
    public static JavaPlugin instance;
    static HashMap<String, SkinData> skinData;
    private static HashMap<UUID, Set<PlayerPet>> playerPets;
    private static Set<Pet> pets;
    private static List<UUID> ignorePlayers;
    static CopyOnWriteArrayList<PetInteractEvent> interactions;

    public static void init(JavaPlugin plugin) {
        PacketEvents.getAPI().getEventManager().registerListener(new PacketsListener());
        PacketEvents.getAPI().init();

        EntityLib.init(
                new SpigotEntityLibPlatform(plugin),
                new APIConfig(PacketEvents.getAPI()));
        instance = plugin;
        playerPets = new HashMap<>();
        skinData = new HashMap<>();
        pets = new HashSet<>();
        ignorePlayers = new ArrayList<>();
        interactions = new CopyOnWriteArrayList<>();

        ViewerEngine viewerEngine = new ViewerEngine();
        viewerEngine.enable();
        viewerEngine.addViewerRule(user -> !ignorePlayers.contains(user.getUUID()));

        instance.getServer().getPluginManager().registerEvents(new PetsListener(), instance);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Set<PlayerPet> pets : playerPets.values()) {
                for (Pet pet : pets) {
                    pet.tick();
                }
            }

            for (Pet pet : pets) {
                pet.tick();
            }
        }, 0L, 2L);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (PetInteractEvent event : interactions) {
                Bukkit.getPluginManager().callEvent(event);
                interactions.remove(event);
            }
        }, 0L, 4L);
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
            Set<PlayerPet> list = new HashSet<>();
            list.add(pet);
            playerPets.put(player.getUniqueId(), list);
        }

        load(player, pet);
    }

    static void load(Player player) {
        for (Set<PlayerPet> pets : playerPets.values()) {
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

    static void kill(Player player, boolean onlyRemoveViewer) {
        if (!onlyRemoveViewer) {
            for (Pet pet : getPets(player)) {
                pet.getEntity().remove();
                playerPets.remove(player.getUniqueId());
            }
        }

        for (Pet pet : getAllPets()) {
            pet.getEntity().removeViewer(player.getUniqueId());
        }
    }

    static void kill(Pet pet) {
        pets.remove(pet);
        pet.getEntity().remove();
    }

    static void kill(Player player, PlayerPet pet) {
        playerPets.get(player.getUniqueId()).remove(pet);
        pet.getEntity().despawn();
        pet.getEntity().remove();
    }

    public static Set<Pet> getPets(Player player) {
        if (!playerPets.containsKey(player.getUniqueId())) return new HashSet<>();

        return new HashSet<>(playerPets.get(player.getUniqueId()));
    }

    public static Pet getById(int id) {
        for (Pet pet : pets) {
            if (pet.getEntity().getEntityId() == id) return pet;
        }

        for (Set<PlayerPet> pets : playerPets.values()) {
            for (PlayerPet pet : pets) {
                if (pet.getEntity().getEntityId() == id) return pet;
            }
        }

        return null;
    }

    public static Set<Pet> getAllPets() {
        Set<Pet> pets = new HashSet<>(PetsAPI.pets);

        for (Set<PlayerPet> petSet : playerPets.values()) {
            pets.addAll(petSet);
        }

        return pets;
    }
}