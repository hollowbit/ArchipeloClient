package net.hollowbit.archipelo.network.packets;

import java.util.ArrayList;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class FlagsAddPacket extends Packet {

	public ArrayList<String> flags;
	
	public FlagsAddPacket () {
		super(PacketType.FLAGS_ADD);
	}

}
