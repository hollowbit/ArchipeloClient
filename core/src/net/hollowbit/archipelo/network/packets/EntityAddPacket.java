package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipeloshared.EntitySnapshot;

public class EntityAddPacket extends Packet {

	public EntitySnapshot snapshot;
	
	public EntityAddPacket () {
		super(PacketType.ENTITY_ADD);
	}
	
}
