package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class OakWoodStairs extends Block {

    @Override
    public int getBlockId() {
        return 53;
    }

    @Override
    public long getBreakTime() {
        return 3000;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
