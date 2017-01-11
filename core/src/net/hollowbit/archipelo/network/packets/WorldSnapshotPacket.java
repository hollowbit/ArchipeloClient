package net.hollowbit.archipelo.network.packets;

import java.util.HashMap;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class WorldSnapshotPacket extends Packet {
	
	public double timeCreatedMillis;
	public int time;
	public int type;
	public HashMap<String, String> entitySnapshots;
	public String mapSnapshot;
	
	public WorldSnapshotPacket () {
		super(PacketType.WORLD_SNAPSHOT);
	}
	
}
