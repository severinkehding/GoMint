package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class IronBars extends Block {

    @Override
    public int getBlockId() {
        return 101;
    }

    @Override
    public long getBreakTime() {
        return 7500;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
