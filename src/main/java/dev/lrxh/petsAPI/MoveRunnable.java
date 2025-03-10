package dev.lrxh.petsAPI;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MoveRunnable implements Runnable {
    protected final Pet pet;
    private final Player player;

    public MoveRunnable(Player player, Pet pet) {
        this.pet = pet;
        this.player = player;
    }

    @Override
    public void run() {
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
