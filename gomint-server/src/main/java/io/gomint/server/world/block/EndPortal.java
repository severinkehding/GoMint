package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EndPortal extends Block {

    @Override
    public int getBlockId() {
        return 119;
    }

    @Override
    public long getBreakTime() {
        return -1;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

}
