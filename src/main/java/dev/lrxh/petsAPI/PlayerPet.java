package dev.lrxh.petsAPI;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PlayerPet extends Pet {
    private Player player;
    private boolean lookAtPlayer;

    public PlayerPet(SkinData skinData) {
        super(skinData);
    }

    public PlayerPet(AnimalSkinData animalSkinData) {
        super(animalSkinData);
    }

    public void spawn(Player player) {
        this.player = player;
        Location location = player.getLocation().clone();
        spawn(location);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void tick() {
        Player player = getPlayer();

        Location location = player.getLocation().clone();

        if (isLookAtPlayer()) {
            Location petLocation = getLocation();

            Vector direction = location.toVector().subtract(petLocation.toVector());

            float yaw = (float) Math.toDegrees(Math.atan2(direction.getZ(), direction.getX())) - 90;
            float pitch = (float) -Math.toDegrees(Math.asin(direction.getY() / direction.length()));

            setYaw(yaw);
            setPitch(pitch);
        }

        location.add(getOffset());

        if (getYaw() != Float.MAX_VALUE) {
            location.setYaw(getYaw());
        }

        if (getPitch() != Float.MAX_VALUE) {
            location.setPitch(getPitch());
        }

        if (isFloatingAnimation()) {
            double FLOATING_SPEED = 0.05;
            double FLOATING_AMPLITUDE = 0.1;

            floatingOffset += FLOATING_SPEED;
            location.setY(location.getY() + Math.sin(floatingOffset) * FLOATING_AMPLITUDE);
        }

        getEntity().teleport(SpigotConversionUtil.fromBukkitLocation(location));
    }

    public boolean isLookAtPlayer() {
        return lookAtPlayer;
    }

    public void setLookAtPlayer(boolean lookAtPlayer) {
        this.lookAtPlayer = lookAtPlayer;
    }
}