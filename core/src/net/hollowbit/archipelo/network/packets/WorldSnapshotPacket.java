package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class WorldSnapshotPacket extends Packet {

	public static final int NUM_OF_CHUNKS = 9;//Must be a perfect square
	public static final int NUM_OF_CHUNKS_WIDE = (int) Math.sqrt(NUM_OF_CHUNKS);//Must be a perfect square
	
	public double timeCreatedMillis;
	public boolean newMap = false;
	public int time;
	public int type = 0;
	public String mapSnapshot;
	public String[] chunks;
	
	public WorldSnapshotPacket () {
		super(PacketType.WORLD_SNAPSHOT);
	}
	
}
