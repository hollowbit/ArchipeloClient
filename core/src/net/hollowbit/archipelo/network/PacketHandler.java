package net.hollowbit.archipelo.network;

public interface PacketHandler {
	
	public abstract boolean handlePacket (Packet packet);
	
}
