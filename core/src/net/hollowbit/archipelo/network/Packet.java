/*
 * Author: vedi0boy
 * Company: Vediogames
 * Please see the Github README.md before using this code.
 */
package net.hollowbit.archipelo.network;

import net.hollowbit.archipelo.ArchipeloClient;

public abstract class Packet {
	
	public int packetType;
	
	public Packet (int type) {
		this.packetType = type;
	}
	
	public void send () {
		ArchipeloClient.getGame().getNetworkManager().sendPacket(this);
	}
	
}
