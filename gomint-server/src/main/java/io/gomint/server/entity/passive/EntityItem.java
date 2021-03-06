package io.gomint.server.entity.passive;

import io.gomint.entity.passive.ItemDrop;
import io.gomint.inventory.ItemStack;
import io.gomint.server.entity.Entity;
import io.gomint.server.entity.EntityType;
import io.gomint.server.network.packet.Packet;
import io.gomint.server.network.packet.PacketAddItemEntity;
import io.gomint.server.world.WorldAdapter;
import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt
 * @version 1.0
 */
@ToString
public class EntityItem extends Entity implements ItemDrop {

    private final ItemStack itemStack;
    @Getter
    private boolean unlocked;
    @Getter
    private long pickupTime;

    private float lastUpdateDt;

    /**
     * Construct a new Entity
     *
     * @param itemStack The itemstack which should be dropped
     * @param world     The world in which this entity is in
     */
    public EntityItem( ItemStack itemStack, WorldAdapter world ) {
        super( EntityType.ITEM_DROP, world );
        this.itemStack = itemStack;
        this.setSize( 0.25f, 0.25f );
        this.unlocked = true;
        setPickupDelay( 5, TimeUnit.SECONDS );
    }

    @Override
    public ItemStack getItemStack() {
        return this.itemStack.clone();
    }

    @Override
    public void setPickupDelay( long duration, TimeUnit timeUnit ) {
        this.pickupTime = System.currentTimeMillis() + timeUnit.toMillis( duration );
    }

    /**
     * Unlock the item to allow picking it up
     *
     * @param currentTimeMillis the time in millis where the tick started
     */
    public void unlock( long currentTimeMillis ) {
        this.unlocked = true;
        this.pickupTime = currentTimeMillis + 2000;
    }

    /**
     * Lock this item drop against picking up
     */
    public void lock() {
        this.unlocked = false;
    }

    @Override
    public void update( long currentTimeMS, float dT ) {
        // Entity base tick (movement)
        super.update( currentTimeMS, dT );

        // Check if we need to calc friction
        this.lastUpdateDt += dT;
        if ( this.lastUpdateDt >= CLIENT_TICK_RATE ) {
            // Calculate friction
            float friction = 1 - DRAG;
            if ( this.onGround && ( Math.abs( this.getMotionX() ) > 0.00001 || Math.abs( this.getMotionZ() ) > 0.00001 ) ) {
                friction = this.world.getBlockAt( (int) this.getPositionX(),
                        (int) ( this.getPositionY() - 1 ),
                        (int) this.getPositionZ() ).getFrictionFactor() * friction;
            }

            // Calculate new motion
            float newMotX = this.getTransform().getMotionX() * friction;
            float newMotY = this.getTransform().getMotionY() * 1 - DRAG;
            float newMotZ = this.getTransform().getMotionZ() * friction;

            if ( this.onGround ) {
                newMotY *= -0.5f;
            }

            this.getTransform().setMotion( newMotX, newMotY, newMotZ );
            this.lastUpdateDt = 0;
        }
    }

    @Override
    protected void fall() {

    }

    @Override
    public Packet createSpawnPacket() {
        PacketAddItemEntity packetAddItemEntity = new PacketAddItemEntity();
        packetAddItemEntity.setEntityId( this.getEntityId() );
        packetAddItemEntity.setItemStack( this.itemStack );
        packetAddItemEntity.setX( this.getPositionX() );
        packetAddItemEntity.setY( this.getPositionY() );
        packetAddItemEntity.setZ( this.getPositionZ() );
        packetAddItemEntity.setMotionX( this.getMotionX() );
        packetAddItemEntity.setMotionY( this.getMotionY() );
        packetAddItemEntity.setMotionZ( this.getMotionZ() );
        return packetAddItemEntity;
    }

}
