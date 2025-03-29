package dev.lrxh.petsAPI;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.manager.server.VersionComparison;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.meta.Metadata;
import me.tofaa.entitylib.meta.other.ArmorStandMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.*;

public class Pet {
    private final SkinData skinData;
    protected double floatingOffset;
    private WrapperPlayServerEntityEquipment equip;
    private WrapperEntity armorStand;
    private Vector offset;
    private float yaw;
    private float pitch;
    private String customName;
    private Component component;
    private boolean floatingAnimation;
    private World world;

    public Pet(SkinData skinData) {
        this.skinData = skinData;
        this.offset = new Vector(0, 0, 0);
        this.yaw = Float.MAX_VALUE;
        this.pitch = Float.MAX_VALUE;
        this.customName = "";
        this.floatingAnimation = false;
        this.floatingOffset = 0;
    }

    public Pet(AnimalSkinData animalSkinData) {
        this.skinData = animalSkinData.getSkinData();
        this.offset = new Vector(0, 0, 0);
        this.yaw = Float.MAX_VALUE;
        this.pitch = Float.MAX_VALUE;
        this.customName = "";
        this.floatingAnimation = false;
        this.floatingOffset = 0;
    }

    public void spawn(Location location) {
        spawn(location, true);
    }

    protected void spawn(Location location, boolean add) {
        this.world = location.getWorld();
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
                Optional<Component> optionalComponent;
                optionalComponent = Optional.of(Objects.requireNonNullElseGet(component, () -> Component.text(customName)));
                armorStandMeta.setIndex((byte) 2, EntityDataTypes.OPTIONAL_ADV_COMPONENT, optionalComponent);
            }
            if (!PacketEvents.getAPI().getServerManager().getVersion().is(VersionComparison.NEWER_THAN, ServerVersion.V_1_14)) {
                armorStandMeta.setMaskBit(3, (byte) 1, true);
            } else {
                armorStandMeta.setIndex((byte) 3, EntityDataTypes.BOOLEAN, true);
            }
        }

        armorStand = new WrapperEntity(id, uuid, EntityTypes.ARMOR_STAND, armorStandMeta);

        location.add(offset);

        armorStand.spawn(SpigotConversionUtil.fromBukkitLocation(location));

        List<Equipment> equipment = new ArrayList<>();

        equipment.add(new Equipment(EquipmentSlot.HELMET, SpigotConversionUtil.fromBukkitItemStack(skinData.get())));

        equip = new WrapperPlayServerEntityEquipment(armorStand.getEntityId(), equipment);

        if (add) PetsAPI.add(this);
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

    public Location getLocation() {
        return SpigotConversionUtil.toBukkitLocation(world, armorStand.getLocation());
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

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public boolean isFloatingAnimation() {
        return floatingAnimation;
    }

    public void setFloatingAnimation(boolean value) {
        this.floatingAnimation = value;
    }

    protected WrapperPlayServerEntityEquipment getEquip() {
        return equip;
    }

    public void tick() {
        Location location = getLocation().clone();

        location.add(getOffset());

        if (getYaw() != Float.MAX_VALUE) {
            location.setYaw(getYaw());
        }

        if (getPitch() != Float.MAX_VALUE) {
            location.setPitch(getPitch());
        }

        if (isFloatingAnimation()) {
            double FLOATING_SPEED = 0.05;
            double FLOATING_AMPLITUDE = 0.01;

            floatingOffset += FLOATING_SPEED;
            location.setY(location.getY() + Math.sin(floatingOffset) * FLOATING_AMPLITUDE);
        }

        getEntity().teleport(SpigotConversionUtil.fromBukkitLocation(location));
    }
}