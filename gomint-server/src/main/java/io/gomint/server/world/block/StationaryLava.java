package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class StationaryLava extends Block {

    @Override
    public int getBlockId() {
        return 11;
    }

    @Override
    public long getBreakTime() {
        return 150000;
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
