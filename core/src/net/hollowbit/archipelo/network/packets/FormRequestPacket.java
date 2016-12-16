package net.hollowbit.archipelo.network.packets;

import java.util.HashMap;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class FormRequestPacket extends Packet {

	public String type;
	public HashMap<String, String> data;
	
	public FormRequestPacket () {
		super(PacketType.FORM_REQUEST);
	}
	
	public FormRequestPacket (String type, HashMap<String, String> data) {
		this();
		this.type = type;
		this.data = data;
	}
	
}
