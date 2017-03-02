package net.hollowbit.archipelo.network.packets;

import java.util.HashMap;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class EntityAddPacket extends Packet {

	public String name;
	public String type;
	public String anim;
	public String animMeta;
	public float animTime;
	public HashMap<String, String> properties;
	
	public EntityAddPacket () {
		super(PacketType.ENTITY_ADD);
	}
	
}
