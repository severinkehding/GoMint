package io.gomint.server.world.block;

import io.gomint.entity.Entity;
import io.gomint.inventory.ItemStack;
import io.gomint.math.Vector;
import io.gomint.server.entity.tileentity.NoteblockTileEntity;

/**
 * @author geNAZt
 * @version 1.0
 */
public class NoteBlock extends Block {

    @Override
    public int getBlockId() {
        return 25;
    }

    @Override
    public long getBreakTime() {
        return 1200;
    }

    @Override
    public void interact( Entity entity, int face, Vector facePos, ItemStack item ) {
        NoteblockTileEntity tileEntity = getTileEntity();
        if ( tileEntity != null ) {
            tileEntity.interact( entity, face, facePos, item );
        }
    }

}
