package net.hollowbit.archipelo.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.WorldSnapshotPacket;
import net.hollowbit.archipelo.world.World;
import net.hollowbit.archipelo.world.WorldSnapshot;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.MapSnapshot;

public class WorldSnapshotManager implements PacketHandler {
	
	public static final int DELAY = 100;//milliseconds  This delay is a set delay between server and client to keep it consistent.
	
	//Stored in packets, rather than the snapshots themselves.
	//The reason for this is to prevent decoding of packets that won't be used.
	private ArrayList<WorldSnapshot> worldInterpSnapshotPackets;
	private ArrayList<WorldSnapshot> worldChangesSnapshotPackets;
	private World world;
	
	public WorldSnapshotManager (World world) {
		this.world = world;
		this.worldInterpSnapshotPackets = new ArrayList<WorldSnapshot>();
		this.worldChangesSnapshotPackets = new ArrayList<WorldSnapshot>();
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
	}
	
	/**
	 * Perform interpolation update
	 * @param delta
	 */
	public void update (float delta) {
		double timeOfPacket = System.currentTimeMillis() - DELAY;
		updateInterp(timeOfPacket);
		updateChange(timeOfPacket);
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
	
	private synchronized void updateChange (double timeOfPacket) {
		//Apply all changes packets to world that are at proper time
		ArrayList<WorldSnapshot> packetsToApply = getPacketsFromBeforeMillis(timeOfPacket, worldChangesSnapshotPackets);
		for (WorldSnapshot packet : packetsToApply) {
			world.applyChangesWorldSnapshot(packet);
		}
		worldChangesSnapshotPackets.removeAll(packetsToApply);
	}
	
	private ArrayList<WorldSnapshot> getPacketsFromBeforeMillis (double millis, ArrayList<WorldSnapshot> packetList) {
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
		Json json = new Json();
		double timeCreatedMillis = packet.timeCreatedMillis;
		int time = packet.time;
		int type = packet.type;
		HashMap<String, EntitySnapshot> entitySnapshots = new HashMap<String, EntitySnapshot>();
		if (packet.entitySnapshots != null) {//Possibility of being null if there were no entity changes (on server) in that tick.
			for (Entry<String, String> entry : packet.entitySnapshots.entrySet()) {
				entitySnapshots.put(entry.getKey(), json.fromJson(EntitySnapshot.class, entry.getValue()));
			}
		}
		
		MapSnapshot mapSnapshot = json.fromJson(MapSnapshot.class, packet.mapSnapshot);
		
		return new WorldSnapshot(timeCreatedMillis, time, type, entitySnapshots, mapSnapshot);
	}

	@Override
	public boolean handlePacket (Packet packet) {
		if (packet.packetType == PacketType.WORLD_SNAPSHOT) {
			WorldSnapshotPacket worldSnapshotPacket = (WorldSnapshotPacket) packet;
			switch (worldSnapshotPacket.type) {
			case WorldSnapshot.TYPE_INTERP:
				addInterpSnapshot(worldSnapshotPacket);
				return true;
			case WorldSnapshot.TYPE_CHANGES:
				addChangesSnapshot(worldSnapshotPacket);
				return true;
			case WorldSnapshot.TYPE_FULL:
				world.applyFullWorldSnapshot(decode(worldSnapshotPacket));
				clearLists();
				return true;
			}
		}
		return false;
	}
	
	private synchronized void addInterpSnapshot (WorldSnapshotPacket packet) {
		worldInterpSnapshotPackets.add(decode(packet));
	}
	
	private synchronized void addChangesSnapshot (WorldSnapshotPacket packet) {
		worldChangesSnapshotPackets.add(decode(packet));
	}
	
	private synchronized void clearLists () {
		worldInterpSnapshotPackets.clear();
		worldChangesSnapshotPackets.clear();
	}
	
	public void dispose () {
		
	}
	
}
