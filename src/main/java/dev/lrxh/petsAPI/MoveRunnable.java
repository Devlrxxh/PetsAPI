package dev.lrxh.petsAPI;

import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MoveRunnable implements Runnable {
    protected final Pet pet;

    public MoveRunnable(Pet pet) {
        this.pet = pet;
    }

    @Override
    public void run() {
        Player player = pet.getPlayer();

        if (player == null) {
            PetsAPI.kill(pet);
            return;
        }

        Location location = player.getLocation().clone();

        if (pet.isLookAtPlayer()) {
            Location petLocation = pet.getLocation();
            if (petLocation == null) {
                PetsAPI.kill(pet);
                return;
            }

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

        pet.getEntity().teleport(SpigotConversionUtil.fromBukkitLocation(location));
    }
}
