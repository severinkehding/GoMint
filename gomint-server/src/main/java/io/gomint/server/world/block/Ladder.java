package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class Ladder extends Block {

    @Override
    public int getBlockId() {
        return 65;
    }

    @Override
    public long getBreakTime() {
        return 600;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
