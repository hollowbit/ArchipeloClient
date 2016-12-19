package net.hollowbit.archipelo.network.packets;

import java.util.HashMap;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class FormInteractPacket extends Packet {
	
	public String id;
	public String command;
	public HashMap<String, String> data;
	public boolean close = false;
	
	public FormInteractPacket () {
		super(PacketType.FORM_INTERACT);
	}
	
	public FormInteractPacket (String id, boolean close) {
		this();
		this.id = id;
		this.close = close;
	}
	
	public FormInteractPacket (String id, String command, HashMap<String, String> data) {
		this();
		this.id = id;
		this.command = command;
		this.data = data;
	}

}
