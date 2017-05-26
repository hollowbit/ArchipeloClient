package net.hollowbit.archipelo.tools;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.PlayerStatsPacket;

public class PlayerInformationManager implements PacketHandler {
	
	private String name;
	private float health;
	
	public PlayerInformationManager() {
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getHealth() {
		return health;
	}
	public void setHealth(float health) {
		this.health = health;
	}
	@Override
	public boolean handlePacket(Packet packet) {
		if (packet.packetType == PacketType.PLAYER_STATS) {
			PlayerStatsPacket statsPacket = (PlayerStatsPacket) packet;
			this.health = statsPacket.health;
			return true;
		}
		return false;
	}
	
}
