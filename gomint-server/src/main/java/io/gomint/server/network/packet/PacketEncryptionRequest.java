package io.gomint.server.network.packet;

import io.gomint.jraknet.PacketBuffer;
import io.gomint.server.network.Protocol;
import lombok.Data;

/**
 * @author geNAZt
 * @version 1.0
 */
@Data
public class PacketEncryptionRequest extends Packet {

    private String serverKey;
    private byte[] clientSalt;

    public PacketEncryptionRequest() {
        super( Protocol.PACKET_ENCRYPTION_REQUEST );
    }

    @Override
    public void serialize( PacketBuffer buffer ) {
        buffer.writeString( this.serverKey );
        buffer.writeUnsignedVarInt( this.clientSalt.length );
        buffer.writeBytes( this.clientSalt );
    }

    @Override
    public void deserialize( PacketBuffer buffer ) {

    }

}
