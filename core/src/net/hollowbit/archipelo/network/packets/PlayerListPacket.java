package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class PlayerListPacket extends Packet {
	
	public static final int RESULT_SUCCESSFUL = 0;
	public static final int RESULT_INVALID_LOGIN = 1;
	
	public Item[][] playerEquippedInventories;
	public String[] names;
	public String[] islands;
	public String[] lastPlayedDateTimes;
	public String[] creationDateTimes;
	public int[] levels;
	
	public String name;
	public int result = 0;
	
	public PlayerListPacket () {
		super(PacketType.PLAYER_LIST);
	}
	
	public PlayerListPacket (String name) {
		this();
		this.name = name;
	}

}
