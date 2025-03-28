package dev.lrxh.petsAPI;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MoveRunnable {
    protected final Pet pet;
    private double floatingOffset = 0;

    public MoveRunnable(Pet pet) {
        this.pet = pet;
    }

    public void tick() {
        Player player = pet.getPlayer();

        Location location = player.getLocation().clone();

        if (pet.isLookAtPlayer()) {
            Location petLocation = pet.getLocation();

            Vector direction = location.toVector().subtract(petLocation.toVector());

            float yaw = (float) Math.toDegrees(Math.atan2(direction.getZ(), direction.getX())) - 90;
            float pitch = (float) -Math.toDegrees(Math.asin(direction.getY() / direction.length()));

            pet.setYaw(yaw);
            pet.setPitch(pitch);
        }

        location.add(pet.getOffset());

        if (pet.getYaw() != Float.MAX_VALUE) {
            location.setYaw(pet.getYaw());
        }

        if (pet.getPitch() != Float.MAX_VALUE) {
            location.setPitch(pet.getPitch());
        }

        if (pet.isFloatingAnimation()) {
            double FLOATING_SPEED = 0.05;
            double FLOATING_AMPLITUDE = 0.1;

            floatingOffset += FLOATING_SPEED;
            location.setY(location.getY() + Math.sin(floatingOffset) * FLOATING_AMPLITUDE);
        }

        pet.getEntity().teleport(SpigotConversionUtil.fromBukkitLocation(location));
    }
}