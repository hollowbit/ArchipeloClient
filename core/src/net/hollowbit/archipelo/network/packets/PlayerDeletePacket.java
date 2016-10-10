package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class PlayerDeletePacket extends Packet {
	
	public static final int RESULT_SUCCESSFUL = 0;
	public static final int RESULT_NO_PLAYER_WITH_NAME = 1;
	public static final int RESULT_NOT_PLAYER_OWNER = 2;
	public static final int RESULT_INVALID_NAME = 3;
	
	public String name;
	public int result = 0;
	
	public PlayerDeletePacket () {
		super(PacketType.PLAYER_DELETE);
	}
	
	public PlayerDeletePacket (String name) {
		this();
		this.name = name;
	}
	
}
