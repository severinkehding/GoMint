package io.gomint.server.inventory.transaction;

import io.gomint.inventory.ItemStack;
import io.gomint.inventory.Material;
import io.gomint.server.entity.EntityPlayer;
import io.gomint.server.inventory.Inventory;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author geNAZt
 * @version 1.0
 */
public class TransactionGroup {

    private final EntityPlayer player;
    @Getter private final long creationTime;

    @Getter private boolean hasExecuted = false;
    @Getter private final Set<Inventory> inventories = new HashSet<>();
    private final Set<Transaction> transactions = new HashSet<>();

    // Need / have for this transactions
    @Getter private List<ItemStack> haveItems = new ArrayList<>();
    private List<ItemStack> needItems = new ArrayList<>();

    // Matched
    private boolean matchItems;

    /**
     * Generate a new transaction group
     *
     * @param player The player for which those transactions are
     */
    public TransactionGroup( EntityPlayer player ) {
        this.player = player;
        this.creationTime = System.currentTimeMillis();
    }

    /**
     * Add a new transaction to this group
     *
     * @param transaction The transaction which should be added
     */
    public void addTransaction( Transaction transaction ) {
        // Check if not already added
        if ( this.transactions.contains( transaction ) ) {
            return;
        }

        // Check if we have a older transaction which this should replace
        for ( Transaction tx : new HashSet<>( this.transactions ) ) {
            if ( tx.hasInventory() && tx.getInventory().equals( transaction.getInventory() ) && tx.getSlot() == transaction.getSlot() ) {
                if ( transaction.getCreationTime() >= tx.getCreationTime() ) {
                    this.transactions.remove( tx );
                } else {
                    return;
                }
            }
        }

        // Add this transaction and also the inventory
        this.transactions.add( transaction );
        if ( transaction.hasInventory() ) {
            this.inventories.add( transaction.getInventory() );
        }
    }

    private void calcMatchItems() {
        // Clear both sides for a fresh compare
        this.haveItems.clear();
        this.needItems.clear();

        // Check all transactions for needed and having items
        for ( Transaction ts : this.transactions ) {
            if ( ts.getTargetItem().getMaterial() != Material.AIR ) {
                this.needItems.add( ts.getTargetItem().clone() );
            }

            ItemStack sourceItem = ts.getSourceItem() != null ? ts.getSourceItem().clone() : null;
            if ( ts.hasInventory() && sourceItem != null ) {
                ItemStack checkSourceItem = ts.getInventory().getItem( ts.getSlot() );

                // Check if source inventory changed during transaction
                if ( !checkSourceItem.equals( sourceItem ) || sourceItem.getAmount() != checkSourceItem.getAmount() ) {
                    this.matchItems = false;
                    return;
                }
            }

            if ( sourceItem != null && sourceItem.getMaterial() != Material.AIR ) {
                this.haveItems.add( sourceItem );
            }
        }

        // Now check if we have items left which are needed
        for ( ItemStack needItem : new ArrayList<>( this.needItems ) ) {
            for ( ItemStack haveItem : new ArrayList<>( this.haveItems ) ) {
                if ( needItem.equals( haveItem ) ) {
                    int amount = Math.min( haveItem.getAmount(), needItem.getAmount() );
                    needItem.setAmount( needItem.getAmount() - amount );
                    haveItem.setAmount( haveItem.getAmount() - amount );

                    if ( haveItem.getAmount() == 0 ) {
                        this.haveItems.remove( haveItem );
                    }

                    if ( needItem.getAmount() == 0 ) {
                        this.needItems.remove( needItem );
                        break;
                    }
                }
            }
        }

        this.matchItems = true;
    }

    /**
     * Check if transaction is complete and can be executed
     *
     * @return true if the transaction is complete and can be executed
     */
    public boolean canExecute() {
        this.calcMatchItems();
        return this.matchItems && this.haveItems.isEmpty() && this.needItems.isEmpty() && !this.transactions.isEmpty();
    }

    /**
     * Try to execute the transaction
     *
     * @param currentTimeMillis The time where the tick started
     * @return true on success, false otherwise
     */
    private boolean execute( long currentTimeMillis ) {
        if ( this.hasExecuted || !this.canExecute() ) {
            return false;
        }

        // TODO: Add a inventory event here

        for ( Transaction transaction : this.transactions ) {
            if ( transaction.hasInventory() ) {
                transaction.getInventory().setItem( transaction.getSlot(), transaction.getTargetItem() );
            } else if ( transaction instanceof DropItemTransaction ) {
                ( (DropItemTransaction) transaction ).getItemDrop().unlock( currentTimeMillis );
            }
        }

        this.hasExecuted = true;
        return true;
    }

    /**
     * Try to execute this transaction. If it tails it reverts itself
     *
     * @param currentTimeMillis The time where the tick started
     */
    public void tryExecute( long currentTimeMillis ) {
        if ( this.canExecute() ) {
            if ( !this.execute( currentTimeMillis ) ) {
                // Revert inventory
                for ( Inventory inventory : this.getInventories() ) {
                    inventory.sendContents( this.player.getConnection() );
                }

                // Revert dropped items
                for ( Transaction transaction : this.transactions ) {
                    if ( transaction instanceof DropItemTransaction ) {
                        ( (DropItemTransaction) transaction ).getItemDrop().despawn();
                    }
                }
            }

            this.player.setTransactions( null );
        }
    }

}
