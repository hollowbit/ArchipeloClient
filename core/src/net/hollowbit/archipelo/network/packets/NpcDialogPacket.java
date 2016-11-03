package net.hollowbit.archipelo.network.packets;

import java.util.ArrayList;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class NpcDialogPacket extends Packet {

	public String messageId;
	public ArrayList<String> links;
	
	public String name;
	public ArrayList<String> messages;
	
	public boolean usesId = false;

	public NpcDialogPacket () {
		super(PacketType.NPC_DIALOG);
	}
	
}
