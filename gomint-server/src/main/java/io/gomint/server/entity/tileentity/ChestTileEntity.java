/*
 * Copyright (c) 2017, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.entity.tileentity;

import io.gomint.inventory.ItemStack;
import io.gomint.inventory.Material;
import io.gomint.server.inventory.ChestInventory;
import io.gomint.server.inventory.MaterialMagicNumbers;
import io.gomint.server.util.EnumConnectors;
import io.gomint.server.world.WorldAdapter;
import io.gomint.taglib.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

/**
 * @author geNAZt
 * @version 1.0
 */
class ChestTileEntity extends TileEntity {

    private ChestInventory inventory;

    /**
     * Construct new TileEntity from TagCompound
     *
     * @param tagCompound The TagCompound which should be used to read data from
     * @param world       The world in which this TileEntity resides
     */
    public ChestTileEntity( NBTTagCompound tagCompound, WorldAdapter world ) {
        super( tagCompound, world );
        this.inventory = new ChestInventory();

        // Read in items
        List<Object> itemList = tagCompound.getList( "Items", false );
        if ( itemList == null ) return;

        for ( Object item : itemList ) {
            NBTTagCompound itemCompound = (NBTTagCompound) item;

            // This is needed since minecraft changed from storing raw ids to string keys somewhere in 1.7 / 1.8
            Material material = null;
            try {
                material = EnumConnectors.MATERIAL_CONNECTOR.revert( MaterialMagicNumbers.valueOfWithId( itemCompound.getShort( "id", (short) 0 ) ) );
            } catch ( ClassCastException e ) {
                material = EnumConnectors.MATERIAL_CONNECTOR.revert( MaterialMagicNumbers.valueOfWithId( itemCompound.getString( "id", "minecraft:air" ) ) );
            }

            // Skip non existent items for PE
            if ( material == null || material == Material.AIR ) {
                continue;
            }

            ItemStack itemStack = new ItemStack(
                    material,
                    itemCompound.getShort( "Damage", (short) 0 ),
                    itemCompound.getByte( "Count", (byte) 0 )
            );

            this.inventory.setContent( itemCompound.getByte( "Slot", (byte) 127 ), itemStack );
        }
    }

    @Override
    public void update( long currentMillis, float dF ) {

    }

    @Override
    public void toCompound( NBTTagCompound compound ) {
        super.toCompound( compound );
        compound.addValue( "id", "Chest" );

        List<NBTTagCompound> nbtTagCompounds = new ArrayList<>();
        for ( int i = 0; i < this.inventory.size(); i++ ) {
            ItemStack itemStack = this.inventory.getContent( i );
            if ( itemStack != null ) {
                NBTTagCompound nbtTagCompound = new NBTTagCompound( "" );
                nbtTagCompound.addValue( "Slot", (byte) i );
                nbtTagCompound.addValue( "id", EnumConnectors.MATERIAL_CONNECTOR.convert( itemStack.getMaterial() ).getOldId() );
                nbtTagCompound.addValue( "Damage", itemStack.getData() );
                nbtTagCompound.addValue( "Count", itemStack.getAmount() );
                nbtTagCompounds.add( nbtTagCompound );
            }
        }

        compound.addValue( "Items", nbtTagCompounds );
    }
}
