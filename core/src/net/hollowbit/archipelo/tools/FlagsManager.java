package net.hollowbit.archipelo.tools;

import java.util.ArrayList;
import java.util.HashSet;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.FlagsAddPacket;

public class FlagsManager implements PacketHandler {
	
	HashSet<String> flags;
	
	public FlagsManager () {
		flags = new HashSet<String>();
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
	}
	
	/**
	 * Checks if player has a specific flag
	 * @param flag
	 * @return
	 */
	public boolean hasFlag (String flag) {
		return flags.contains(flag);
	}
	
	/**
	 * Adds flags to list
	 * @param flags
	 */
	private void addFlags (ArrayList<String> flags) {
		for (String flag : flags)
			this.flags.add(flag);
	}
	
	/**
	 * Proper way to dispose of flagsManager.
	 * Removes link to net man
	 */
	public void dispose () {
		ArchipeloClient.getGame().getNetworkManager().removePacketHandler(this);
	}

	@Override
	public boolean handlePacket (Packet packet) {
		if (packet.packetType == PacketType.FLAGS_ADD) {
			FlagsAddPacket flagsAddPacket = (FlagsAddPacket) packet;
			this.addFlags(flagsAddPacket.flags);
			return true;
		}
		return false;
	}
	
}
