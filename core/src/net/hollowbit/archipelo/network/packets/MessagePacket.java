package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class MessagePacket extends Packet {
	
	public String message;
	
	public MessagePacket () {
		super(PacketType.MESSAGE);
	}
	
	public MessagePacket (String message) {
		super(PacketType.MESSAGE);
		this.message = message;
	}
	
	@Override
	public String toString () {
		return message;
	}
	
}
