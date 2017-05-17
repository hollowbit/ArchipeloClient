package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class PlayerStatsPacket extends Packet {

	public int health = 0;
	
	public PlayerStatsPacket() {
		super(PacketType.PLAYER_STATS);
	}

}
