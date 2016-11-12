package net.hollowbit.archipelo.network;

public class PacketWrapper {
	
	public Packet packet;
	public long time;
	
	public PacketWrapper (Packet packet) {
		this.packet = packet;
		this.time = System.currentTimeMillis();
	}
	
}
