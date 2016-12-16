package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipeloshared.FormData;

public class FormDataPacket extends Packet {
	
	public FormData data;
	
	public FormDataPacket () {
		super(PacketType.FORM_DATA);
	}

}
