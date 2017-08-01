package net.hollowbit.archipelo.tools;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.WorldSnapshotPacket;
import net.hollowbit.archipelo.world.World;
import net.hollowbit.archipelo.world.WorldSnapshot;
import net.hollowbit.archipeloshared.ChunkData;
import net.hollowbit.archipeloshared.MapSnapshot;

public class WorldSnapshotManager implements PacketHandler {
	
	public static final int DELAY = 100;//milliseconds  This delay is a set delay between server and client to keep it consistent.
	
	//Stored in packets, rather than the snapshots themselves.
	//The reason for this is to prevent decoding of packets that won't be used.
	private ArrayList<WorldSnapshot> worldInterpSnapshotPackets;
	private ArrayList<WorldSnapshot> worldChangesSnapshotPackets;
	private ArrayList<WorldSnapshot> worldFullSnapshotPackets;
	private World world;
	private Json json;
	
	public WorldSnapshotManager (World world) {
		this.world = world;
		this.worldInterpSnapshotPackets = new ArrayList<WorldSnapshot>();
		this.worldChangesSnapshotPackets = new ArrayList<WorldSnapshot>();
		this.json = new Json();
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
	}
	
	/**
	 * Perform interpolation update
	 * @param delta
	 */
	public void update (float delta) {
		double timeOfPacket = System.currentTimeMillis() - DELAY;
		updateChange(timeOfPacket);
		updateInterp(timeOfPacket);
	}
	
	private synchronized void updateInterp (double timeOfPacket) {
		if (worldInterpSnapshotPackets.isEmpty())
			return;
		
		ArrayList<WorldSnapshot> oldPackets = getPacketsFromBeforeMillis(timeOfPacket, worldInterpSnapshotPackets);
		worldInterpSnapshotPackets.removeAll(oldPackets);
		
		if (worldInterpSnapshotPackets.size() < 2)
			return;

		WorldSnapshot packet1 = worldInterpSnapshotPackets.get(0);
		WorldSnapshot packet2 = worldInterpSnapshotPackets.get(1);
		
		double delta = packet2.timeCreatedMillis - packet1.timeCreatedMillis;
		double deltaF = timeOfPacket - packet1.timeCreatedMillis;
		
		double fraction = deltaF / delta;
		if (fraction > 1)//This could happen and causes undesired effects
			fraction = 1;
		
		//Find the latest packet
		world.interpolate((long) timeOfPacket, packet1, packet2, (float) fraction);
	}
	
	private void updateChange (double timeOfPacket) {
		//Apply all changes packets to world that are at proper time
		ArrayList<WorldSnapshot> packetsToApply = getPacketsFromBeforeMillis(timeOfPacket, worldChangesSnapshotPackets);
		for (WorldSnapshot packet : packetsToApply) {
			world.applyChangesWorldSnapshot(packet);
		}
		this.removeFromCollection(worldChangesSnapshotPackets, packetsToApply);
		
		ArrayList<WorldSnapshot> packetsToApplyFull = getPacketsFromBeforeMillis(timeOfPacket, worldFullSnapshotPackets);
		for (WorldSnapshot packet : packetsToApplyFull)
			world.applyFullWorldSnapshot(packet);
		this.removeFromCollection(worldFullSnapshotPackets, packetsToApplyFull);
	}
	
	private synchronized void removeFromCollection(ArrayList<WorldSnapshot> collection, ArrayList<WorldSnapshot> itemsToRemove) {
		collection.removeAll(itemsToRemove);
	}
	
	private synchronized ArrayList<WorldSnapshot> getPacketsFromBeforeMillis (double millis, ArrayList<WorldSnapshot> packetList) {
		if (packetList.isEmpty())
			return new ArrayList<WorldSnapshot>();
		
		WorldSnapshot max = null;
		for (WorldSnapshot packet : packetList) {
			if (packet.timeCreatedMillis <= millis) {
				if (max == null || packet.timeCreatedMillis > max.timeCreatedMillis)
					max = packet;
			}
		}
		
		if (max == null)
			return new ArrayList<WorldSnapshot>();
		
		ArrayList<WorldSnapshot> packets = new ArrayList<WorldSnapshot>();
		for (WorldSnapshot packet : packetList) {
			if (packet.timeCreatedMillis < max.timeCreatedMillis)
				packets.add(packet);
		}
		return packets;
	}
	
	private WorldSnapshot decode (WorldSnapshotPacket packet) {
		ChunkData[] chunks = new ChunkData[WorldSnapshotPacket.NUM_OF_CHUNKS];
		for (int i = 0; i < chunks.length; i++)
			chunks[i] = json.fromJson(ChunkData.class, packet.chunks[i]);
		
		MapSnapshot mapSnapshot = json.fromJson(MapSnapshot.class, packet.mapSnapshot);
		
		return new WorldSnapshot(packet.timeCreatedMillis, packet.newMap, packet.time, packet.type, chunks, mapSnapshot);
	}

	@Override
	public boolean handlePacket (Packet packet) {
		if (packet.packetType == PacketType.WORLD_SNAPSHOT) {
			WorldSnapshotPacket worldSnapshotPacket = (WorldSnapshotPacket) packet;
			switch (worldSnapshotPacket.type) {
			case WorldSnapshot.TYPE_INTERP:
				addInterpSnapshot(decode(worldSnapshotPacket));
				return true;
			case WorldSnapshot.TYPE_CHANGES:
				addChangesSnapshot(decode(worldSnapshotPacket));
				return true;
			case WorldSnapshot.TYPE_FULL:
				addFullSnapshot(decode(worldSnapshotPacket));
				return true;
			}
		}
		return false;
	}
	
	private synchronized void addInterpSnapshot (WorldSnapshot packet) {
		worldInterpSnapshotPackets.add(packet);
	}
	
	private synchronized void addChangesSnapshot (WorldSnapshot packet) {
		worldChangesSnapshotPackets.add(packet);
	}
	
	private synchronized void addFullSnapshot (WorldSnapshot packet) {
		worldFullSnapshotPackets.add(packet);
	}
	
	public void dispose () {
		
	}
	
}
