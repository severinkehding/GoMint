package io.gomint.server.inventory;

import io.gomint.inventory.ItemStack;
import io.gomint.inventory.Material;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.network.PlayerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author geNAZt
 * @version 1.0
 */
public abstract class Inventory {

    private static final Logger LOGGER = LoggerFactory.getLogger( Inventory.class );

    protected final InventoryHolder owner;
    protected Set<PlayerConnection> viewer = new HashSet<>();

    protected int size;
    protected ItemStack[] contents;

    public Inventory( InventoryHolder owner, int size ) {
        this.owner = owner;
        this.size = size;

        this.contents = new ItemStack[size];
        Arrays.fill( this.contents, new ItemStack( Material.AIR, (short) 0, 0 ) );

        // Add owner to viewers if needed
        if ( this.owner instanceof EntityPlayer ) {
            addViewer( (EntityPlayer) this.owner );
        }
    }

    public void addViewer( EntityPlayer player ) {
        this.sendContents( player.getConnection() );
        this.viewer.add( player.getConnection() );
    }

    public void removeViewer( EntityPlayer player ) {
        this.viewer.remove( player.getConnection() );
    }

    public void setItem( int index, ItemStack item ) {
        this.contents[index] = item;

        for ( PlayerConnection playerConnection : this.viewer ) {
            this.sendContents( index, playerConnection );
        }
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public int getSize() {
        return size;
    }

    public ItemStack getItem( int slot ) {
        return this.contents[slot];
    }

    public abstract void sendContents( PlayerConnection playerConnection );

    public abstract void sendContents( int slot, PlayerConnection playerConnection );

    /**
     * Checks if this inventory can store the given item stack without being full
     *
     * @param itemStack The item stack which may fit
     * @return true when the inventory has place for the item stack, false if not
     */
    public boolean hasPlaceFor( ItemStack itemStack ) {
        ItemStack clone = itemStack.clone();

        for ( ItemStack content : this.contents ) {
            if ( content.getMaterial() == Material.AIR ) {
                return true;
            } else if ( content.equals( clone ) &&
                    content.getAmount() <= content.getMaximumAmount() ) {
                if ( content.getAmount() + clone.getAmount() <= content.getMaximumAmount() ) {
                    return true;
                } else {
                    int amountToDecrease = content.getMaximumAmount() - content.getAmount();
                    clone.setAmount( clone.getAmount() - amountToDecrease );
                }

                if ( clone.getAmount() == 0 ) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Add a item into the inventory. Try to merge existing stacks or use the next free slot.
     *
     * @param itemStack the item stack which should be added
     * @return true when it got added, false if not
     */
    public boolean addItem( ItemStack itemStack ) {
        // Check if we have plave for this item
        if ( !this.hasPlaceFor( itemStack ) ) {
            return false;
        }

        ItemStack clone = itemStack.clone();

        // First try to merge
        for ( int i = 0; i < this.contents.length; i++ ) {
            if ( this.contents[i].equals( clone ) &&
                    this.contents[i].getAmount() <= this.contents[i].getMaximumAmount() ) {
                if ( this.contents[i].getAmount() + clone.getAmount() <= this.contents[i].getMaximumAmount() ) {
                    this.contents[i].setAmount( this.contents[i].getAmount() + clone.getAmount() );
                    clone.setAmount( 0 );
                } else {
                    int amountToDecrease = this.contents[i].getMaximumAmount() - this.contents[i].getAmount();
                    this.contents[i].setAmount( this.contents[i].getMaximumAmount() );
                    clone.setAmount( clone.getAmount() - amountToDecrease );
                }

                // Send item to all viewers
                setItem( i, this.contents[i] );

                // We added all of the stack to this inventory
                if ( clone.getAmount() == 0 ) {
                    return true;
                }
            }
        }

        // Search for a free slot
        for ( int i = 0; i < this.contents.length; i++ ) {
            if ( this.contents[i].getMaterial() == Material.AIR ) {
                setItem( i, clone );
                return true;
            }
        }

        return false;
    }

}
