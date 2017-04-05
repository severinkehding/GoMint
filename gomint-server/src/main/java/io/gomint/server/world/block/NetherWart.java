package io.gomint.server.world.block;

/**
 * @author geNAZt
 * @version 1.0
 */
public class NetherWart extends Block {

    @Override
    public int getBlockId() {
        return 115;
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