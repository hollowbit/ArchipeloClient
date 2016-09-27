package net.hollowbit.archipelo.tools;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.EntitySnapshot;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.WorldSnapshotPacket;
import net.hollowbit.archipelo.world.MapSnapshot;
import net.hollowbit.archipelo.world.World;
import net.hollowbit.archipelo.world.WorldSnapshot;

public class WorldSnapshotManager implements PacketHandler {
	
	private static final int DELAY = 100;//milliseconds  This delay is a set delay between server and client to keep it consistent. 
	
	//Stored in packets, rather than the snapshots themselves.
	//The reason for this is to prevent decoding of packets that won't be used.
	private ArrayList<WorldSnapshotPacket> worldInterpSnapshotPackets;
	private ArrayList<WorldSnapshotPacket> worldChangesSnapshotPackets;
	private World world;
	
	public WorldSnapshotManager (World world) {
		this.world = world;
		this.worldInterpSnapshotPackets = new ArrayList<WorldSnapshotPacket>();
		this.worldChangesSnapshotPackets = new ArrayList<WorldSnapshotPacket>();
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
	}
	
	public synchronized void update () {
		if (worldInterpSnapshotPackets.size() <= 0)
			return;
		
		double timeOfPacket = worldInterpSnapshotPackets.get(worldInterpSnapshotPackets.size() - 1).timeCreatedMillis - DELAY;
		
		ArrayList<WorldSnapshotPacket> currentWorldSnapshotPackets = new ArrayList<WorldSnapshotPacket>();
		currentWorldSnapshotPackets.addAll(worldInterpSnapshotPackets);

		worldInterpSnapshotPackets.removeAll(getPacketsFromBeforeMillis(timeOfPacket, worldInterpSnapshotPackets));
		
		//Find the latest packet
		if (!worldInterpSnapshotPackets.isEmpty()) {
			WorldSnapshotPacket latestPacket = worldInterpSnapshotPackets.get(0);
			for (WorldSnapshotPacket packet : worldInterpSnapshotPackets) {
				if (packet.timeCreatedMillis < latestPacket.timeCreatedMillis) {
					latestPacket = packet;
					break;
				}
			}
			if (latestPacket != null) {
				world.applyInterpWorldSnapshot(decode(latestPacket));
				worldInterpSnapshotPackets.remove(latestPacket);
			}
		}
		
		//Apply all changes packets to world that are at proper time
		ArrayList<WorldSnapshotPacket> packetsToApply = getPacketsFromBeforeMillis(timeOfPacket, worldChangesSnapshotPackets);
		for (WorldSnapshotPacket packet : packetsToApply) {
			world.applyChangesWorldSnapshot(decode(packet));
		}
		worldChangesSnapshotPackets.removeAll(packetsToApply);
	}
	
	private ArrayList<WorldSnapshotPacket> getPacketsFromBeforeMillis (double millis, ArrayList<WorldSnapshotPacket> packetList) {
		ArrayList<WorldSnapshotPacket> packets = new ArrayList<WorldSnapshotPacket>();
		for (WorldSnapshotPacket packet : packetList) {
			if (packet.timeCreatedMillis < millis)
				packets.add(packet);
		}
		return packets;
	}
	
	private WorldSnapshot decode (WorldSnapshotPacket packet) {
		Json json = new Json();
		double timeCreatedMillis = packet.timeCreatedMillis;
		int time = packet.time;
		int type = packet.type;
		ArrayList<EntitySnapshot> entitySnapshots = new ArrayList<EntitySnapshot>();
		if (packet.entitySnapshots != null) {//Possibility of being null if there were no entity changes (on server) in that tick.
			for (String entitySnapshotString : packet.entitySnapshots) {
				entitySnapshots.add(json.fromJson(EntitySnapshot.class, entitySnapshotString));
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
		worldInterpSnapshotPackets.add(packet);
	}
	
	private synchronized void addChangesSnapshot (WorldSnapshotPacket packet) {
		worldChangesSnapshotPackets.add(packet);
	}
	
	private synchronized void clearLists () {
		worldInterpSnapshotPackets.clear();
		worldChangesSnapshotPackets.clear();
	}
	
}
