package io.gomint.server.network.handler;

import io.gomint.event.player.PlayerMoveEvent;
import io.gomint.inventory.ItemStack;
import io.gomint.inventory.Material;
import io.gomint.math.Location;
import io.gomint.math.Vector;
import io.gomint.server.entity.passive.EntityItem;
import io.gomint.server.network.PlayerConnection;
import io.gomint.server.network.packet.PacketMovePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PacketMovePlayerHandler implements PacketHandler<PacketMovePlayer> {

    private static final Logger LOGGER = LoggerFactory.getLogger( PacketMovePlayerHandler.class );

    @Override
    public void handle( PacketMovePlayer packet, long currentTimeMillis, PlayerConnection connection ) {
        Location to = connection.getEntity().getLocation();
        to.setX( packet.getX() );
        to.setY( packet.getY() - connection.getEntity().getEyeHeight() ); // Subtract eye height since client sends it at the eyes
        to.setZ( packet.getZ() );
        to.setHeadYaw( packet.getHeadYaw() );
        to.setYaw( packet.getYaw() );
        to.setPitch( packet.getPitch() );

        Location from = connection.getEntity().getLocation();

        // The packet did not contain any movement? skip it
        if ( from.getX() - to.getX() == 0 &&
                from.getY() - to.getY() == 0 &&
                from.getZ() - to.getZ() == 0 &&
                from.getHeadYaw() - to.getHeadYaw() == 0 &&
                from.getYaw() - to.getYaw() == 0 &&
                from.getPitch() - to.getPitch() == 0 ) {
            return;
        }

        PlayerMoveEvent playerMoveEvent = connection.getNetworkManager().getServer().getPluginManager().callEvent(
                new PlayerMoveEvent( connection.getEntity(), from, to )
        );

        // LOGGER.debug( "Moving " + ( Math.abs( Math.sqrt( from.distanceSquared( to ) ) ) * 20 ) + " blocks/s" );

        if ( playerMoveEvent.isCancelled() ) {
            playerMoveEvent.setTo( playerMoveEvent.getFrom() );
        }

        to = playerMoveEvent.getTo();
        if ( to.getX() != packet.getX() || to.getY() != packet.getY() - connection.getEntity().getEyeHeight() || to.getZ() != packet.getZ() ||
                !to.getWorld().equals( connection.getEntity().getWorld() ) || to.getYaw() != packet.getYaw() ||
                to.getPitch() != packet.getPitch() || to.getHeadYaw() != packet.getHeadYaw() ) {
            connection.getEntity().teleport( to );
        }

        connection.getEntity().setPosition( to.getX(), to.getY(), to.getZ() );
        connection.getEntity().setPitch( to.getPitch() );
        connection.getEntity().setYaw( to.getYaw() );
        connection.getEntity().setHeadYaw( to.getHeadYaw() );

        if ( (int) from.getX() != (int) to.getX() ||
                (int) from.getZ() != (int) to.getZ() ||
                !to.getWorld().equals( from.getWorld() ) ) {
            connection.checkForNewChunks();
        }
    }

}
