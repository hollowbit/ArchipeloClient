package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class PositionCorrectionPacket extends Packet {
	
	public float x;
	public float y;
	public int id;
	
	public PositionCorrectionPacket() {
		super(PacketType.POSITION_CORRECTION);
	}

}
