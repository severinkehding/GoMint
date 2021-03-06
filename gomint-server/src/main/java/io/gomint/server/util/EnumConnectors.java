package io.gomint.server.util;

import io.gomint.inventory.Material;
import io.gomint.server.inventory.MaterialMagicNumbers;
import io.gomint.server.world.GamemodeMagicNumbers;
import io.gomint.server.world.SoundMagicNumbers;
import io.gomint.world.Gamemode;
import io.gomint.world.Sound;

/**
 * @author geNAZt
 * @version 1.0
 */
public class EnumConnectors {

    public static final EnumConnector<Gamemode, GamemodeMagicNumbers> GAMEMODE_CONNECTOR = new EnumConnector<>( Gamemode.class, GamemodeMagicNumbers.class );
    public static final EnumConnector<Sound, SoundMagicNumbers> SOUND_CONNECTOR = new EnumConnector<>( Sound.class, SoundMagicNumbers.class );
    public static final EnumConnector<Material, MaterialMagicNumbers> MATERIAL_CONNECTOR = new EnumConnector<>( Material.class, MaterialMagicNumbers.class );


}
