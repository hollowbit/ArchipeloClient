package net.hollowbit.archipelo.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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
	
	public static final int DELAY = 100;//milliseconds  This delay is a set delay between server and client to keep it consistent. 
	public static final float TIME_BETWEEN_UPDATES = 1 / 20f;
	
	//Stored in packets, rather than the snapshots themselves.
	//The reason for this is to prevent decoding of packets that won't be used.
	private ArrayList<WorldSnapshotPacket> worldInterpSnapshotPackets;
	private ArrayList<WorldSnapshotPacket> worldChangesSnapshotPackets;
	private World world;
	private float timer = 0;
	
	public WorldSnapshotManager (World world) {
		this.world = world;
		this.worldInterpSnapshotPackets = new ArrayList<WorldSnapshotPacket>();
		this.worldChangesSnapshotPackets = new ArrayList<WorldSnapshotPacket>();
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
	}
	
	public void update (float delta) {
		timer += delta;
		if (timer >= TIME_BETWEEN_UPDATES) {
			timer -= TIME_BETWEEN_UPDATES;
			
			//Perform update
			double timeOfPacket = System.currentTimeMillis() - DELAY;
			updateInterp(timeOfPacket);
			updateChange(timeOfPacket);
		}
	}
	
	private synchronized void updateInterp (double timeOfPacket) {
		if (worldInterpSnapshotPackets.isEmpty())
			return;
		
		ArrayList<WorldSnapshotPacket> oldPackets = getPacketsFromBeforeMillis(timeOfPacket, worldInterpSnapshotPackets);

		if (oldPackets.isEmpty())
			return;
		
		WorldSnapshotPacket packet1 = oldPackets.get(oldPackets.size() - 1);
		worldInterpSnapshotPackets.removeAll(oldPackets);
		
		if (worldInterpSnapshotPackets.isEmpty())
			return;
		
		WorldSnapshotPacket packet2 = worldInterpSnapshotPackets.get(0);
		
		double delta = packet2.timeCreatedMillis - packet1.timeCreatedMillis;
		double deltaF = timeOfPacket - packet1.timeCreatedMillis;
		
		double fraction = deltaF / delta;
		
		//Find the latest packet
		world.applyInterpWorldSnapshot((long) timeOfPacket, decode(packet1), decode(packet2), (float) fraction);
		worldInterpSnapshotPackets.remove(packet2);
	}
	
	private synchronized void updateChange (double timeOfPacket) {
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
			if (packet.timeCreatedMillis <= millis)
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
		worldInterpSnapshotPackets.add(packet);
	}
	
	private synchronized void addChangesSnapshot (WorldSnapshotPacket packet) {
		worldChangesSnapshotPackets.add(packet);
	}
	
	private synchronized void clearLists () {
		worldInterpSnapshotPackets.clear();
		worldChangesSnapshotPackets.clear();
	}
	
	public void dispose () {
		
	}
	
}
