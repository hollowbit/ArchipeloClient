package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class PopupTextPacket extends Packet {
	
	public String text;
	public int type;
	
	public PopupTextPacket () {
		super(PacketType.POPUP_TEXT);
	}
	
}
