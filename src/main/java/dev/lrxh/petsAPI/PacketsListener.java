package dev.lrxh.petsAPI;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketsListener extends PacketListenerAbstract {

    @Override
    public void onPacketReceive(PacketReceiveEvent packet) {
        packet.markForReEncode(false);

        if (packet.getPacketType() != PacketType.Play.Client.INTERACT_ENTITY) return;

        WrapperPlayClientInteractEntity interact = new WrapperPlayClientInteractEntity(packet);

        for (Pet pet : PetsAPI.getAllPets()) {
            if (pet.getEntity().getEntityId() == interact.getEntityId()) {
                Player player = packet.getPlayer();

                PetInteractEvent event = new PetInteractEvent(player, pet);

                PetsAPI.interactions.add(event);

                break;
            }
        }
    }
}
