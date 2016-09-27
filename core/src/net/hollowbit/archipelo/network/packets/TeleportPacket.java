package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class TeleportPacket extends Packet {
	
	public String username;
	public float x, y;
	public int direction;
	public boolean newMap;
	
	public TeleportPacket () {
		super(PacketType.TELEPORT);
	}
	
}
