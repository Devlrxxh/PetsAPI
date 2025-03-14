package dev.lrxh.petsAPI;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.manager.server.VersionComparison;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.meta.Metadata;
import me.tofaa.entitylib.meta.other.ArmorStandMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Pet {
    private final SkinData skinData;
    private final List<PacketWrapper> packets;
    private WrapperEntity armorStand;
    private Vector offset;
    private Player player;
    private float yaw;
    private float pitch;
    private boolean lookAtPlayer;
    private String customName;
    private Component component;
    private boolean floatingAnimation;

    public Pet(SkinData skinData) {
        this.skinData = skinData;
        this.offset = new Vector(1, 1, 1);
        this.packets = new ArrayList<>();
        this.yaw = Float.MAX_VALUE;
        this.pitch = Float.MAX_VALUE;
        this.lookAtPlayer = false;
        this.customName = "";
        this.component = null;
        this.floatingAnimation = false;
    }

    public Pet(AnimalSkinData animalSkinData) {
        this.skinData = animalSkinData.getSkinData();
        this.offset = new Vector(1, 1, 1);
        this.packets = new ArrayList<>();
        this.yaw = Float.MAX_VALUE;
        this.pitch = Float.MAX_VALUE;
        this.lookAtPlayer = false;
        this.customName = "";
        this.floatingAnimation = false;
    }

    public void spawn(Player player) {
        this.player = player;
        UUID uuid = UUID.randomUUID();
        int id = EntityLib.getPlatform().getEntityIdProvider().provide(uuid, EntityTypes.ARMOR_STAND);

        ArmorStandMeta armorStandMeta = new ArmorStandMeta(id, new Metadata(id));
        armorStandMeta.setInvisible(true);

        if (!PacketEvents.getAPI().getServerManager().getVersion().is(VersionComparison.NEWER_THAN, ServerVersion.V_1_14)) {
            armorStandMeta.setMaskBit(10, (byte) 1, true);
        } else {
            armorStandMeta.setSmall(true);
        }

        if (!customName.isEmpty() || component != null) {
            if (!PacketEvents.getAPI().getServerManager().getVersion().is(VersionComparison.NEWER_THAN, ServerVersion.V_1_14)) {
                armorStandMeta.setIndex((byte) 2, EntityDataTypes.STRING, customName);
            } else {
                armorStandMeta.setIndex((byte) 2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, Optional.of(Objects.requireNonNullElseGet(component, () -> Component.text(customName))));
            }

            if (!PacketEvents.getAPI().getServerManager().getVersion().is(VersionComparison.NEWER_THAN, ServerVersion.V_1_14)) {
                armorStandMeta.setMaskBit(3, (byte) 1, true);
            } else {
                armorStandMeta.setIndex((byte) 3, EntityDataTypes.BOOLEAN, true);
            }
        }

        armorStand = new WrapperEntity(id, uuid, EntityTypes.ARMOR_STAND, armorStandMeta);

        Location location = player.getLocation().clone();
        location.add(offset);

        armorStand.spawn(SpigotConversionUtil.fromBukkitLocation(location));

        List<Equipment> equipment = new ArrayList<>();

        equipment.add(new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(skinData.getPlayerHead())));

        WrapperPlayServerEntityEquipment equip = new WrapperPlayServerEntityEquipment(armorStand.getEntityId(), equipment);

        packets.add(equip);

        PetsAPI.add(player, this);
    }

    public void remove() {
        PetsAPI.kill(this);
    }

    public WrapperEntity getEntity() {
        return armorStand;
    }

    public Vector getOffset() {
        return offset;
    }

    public void setOffset(Vector offset) {
        this.offset = offset;
    }

    @Nullable
    public Location getLocation() {
        return SpigotConversionUtil.toBukkitLocation(player.getWorld(), armorStand.getLocation());
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isLookAtPlayer() {
        return lookAtPlayer;
    }

    public void setLookAtPlayer(boolean lookAtPlayer) {
        this.lookAtPlayer = lookAtPlayer;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isFloatingAnimation() {
        return floatingAnimation;
    }

    public void setFloatingAnimation(boolean value) {
        this.floatingAnimation = value;
    }

    protected List<PacketWrapper> getPackets() {
        return packets;
    }
}