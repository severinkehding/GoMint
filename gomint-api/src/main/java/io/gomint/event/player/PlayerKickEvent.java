package io.gomint.event.player;

import io.gomint.entity.Player;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PlayerKickEvent extends PlayerEvent {

    private final String message;

    /**
     * Construct a new player kick event
     *
     * @param player    The player which is going to be kicked
     * @param message   The message with which the player is going to be kicked
     */
    public PlayerKickEvent( Player player, String message ) {
        super( player );
        this.message = message;
    }

    /**
     * Get the message with which this user is kicked
     *
     * @return the message with which is the user kicked
     */
    public String getMessage() {
        return this.message;
    }

}
