package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class ChatMessagePacket extends Packet {
	
	public String prefix;
	public String message;
	public String sender;
	
	public ChatMessagePacket () {
		super(PacketType.CHAT_MESSAGE);
	}
	
	public ChatMessagePacket (String message) {
		this();
		this.message = message;
		this.sender = "";
	}
	
	public ChatMessagePacket (String message, String sender) {
		this(message);
		this.sender = sender;
	}
	
}
