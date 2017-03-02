package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class EntityRemovePacket extends Packet {
	
	public String name;
	
	public EntityRemovePacket () {
		super(PacketType.ENTITY_REMOVE);
	}
	
}
