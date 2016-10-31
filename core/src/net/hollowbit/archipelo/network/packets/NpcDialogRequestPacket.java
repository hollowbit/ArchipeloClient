package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class NpcDialogRequestPacket extends Packet {

	public String messageId;

	public NpcDialogRequestPacket () {
		super(PacketType.NPC_DIALOG_REQUEST);
	}
	
	public NpcDialogRequestPacket (String messageId) {
		this();
		this.messageId = messageId;
	}

}
