package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Chest extends Block {

    @Override
    public int getBlockId() {
        return 54;
    }

    @Override
    public long getBreakTime() {
        return 3750;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
