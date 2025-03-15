package dev.lrxh.petsAPI;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.manager.server.VersionComparison;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkinData {
    private final String value;

    public SkinData(String value) {
        this.value = value;
    }

    public static SkinData ofPlayerName(String name) {
        Player player = Bukkit.getPlayerExact(name);

        if (player == null) {
            if (PetsAPI.skinData.containsKey(name)) {
                return PetsAPI.skinData.get(name);
            }

            CompletableFuture<String> uuidFuture = getPlayerUUID(name);
            String uuid = uuidFuture.join();

            if (uuid.isEmpty()) return AnimalSkinData.STEVE.getSkinData();

            CompletableFuture<String> valueFuture = getValue(uuid);
            String value = valueFuture.join();
            if (value.isEmpty()) return AnimalSkinData.STEVE.getSkinData();

            SkinData skinData = new SkinData(value);

            PetsAPI.skinData.put(name, skinData);

            return skinData;
        }

        if (PacketEvents.getAPI().getServerManager().getVersion().is(VersionComparison.NEWER_THAN, ServerVersion.V_1_14)) {
            PlayerProfile profile = player.getPlayerProfile();

            Optional<ProfileProperty> property = profile.getProperties().stream().filter(loopProperty -> loopProperty.getName().equals("textures")).findFirst();
            return property.map(signedProperty -> new SkinData(signedProperty.getValue())).orElse(null);
        } else {

            EntityPlayer ep = ((CraftPlayer) player).getHandle();
            GameProfile gameProfile = ep.getProfile();

            return gameProfile.getProperties().get("textures").stream()
                    .map(signedProperty -> new SkinData(signedProperty.getValue()))
                    .findFirst()
                    .orElse(AnimalSkinData.STEVE.getSkinData());
        }
    }

    private static CompletableFuture<String> getValue(String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setDoInput(true);

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                JSONArray propertiesArray = jsonResponse.getJSONArray("properties");

                for (int i = 0; i < propertiesArray.length(); i++) {
                    JSONObject propertyObject = propertiesArray.getJSONObject(i);

                    if (propertyObject.getString("name").equals("textures")) {
                        return propertyObject.getString("value");
                    }
                }

            } catch (IOException e) {
                return "";
            }
            return "";
        });
    }

    private static CompletableFuture<String> getPlayerUUID(String name) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setDoInput(true);

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                return jsonResponse.getString("id");

            } catch (IOException e) {
                return "";
            }
        });
    }

    public ItemStack getPlayerHead() {
        if (PacketEvents.getAPI().getServerManager().getVersion().is(VersionComparison.NEWER_THAN, ServerVersion.V_1_14)) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            PlayerProfile playerProfile = Bukkit.createProfile(UUID.randomUUID());
            playerProfile.setProperty(new ProfileProperty("textures",
                    value,
                    null
            ));
            skullMeta.setPlayerProfile(playerProfile);
            head.setItemMeta(skullMeta);
            return head;
        } else {
            ItemStack head = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            GameProfile profile = new GameProfile(UUID.randomUUID(), "");
            profile.getProperties().put("textures", new Property("textures", value));
            Field profileField;
            try {
                profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                PetsAPI.instance.getLogger().severe(e.getMessage());
            }
            head.setItemMeta(meta);
            return head;
        }
    }

    public String getValue() {
        return value;
    }
}