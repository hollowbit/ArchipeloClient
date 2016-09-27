package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class ControlsPacket extends Packet {
	
	public boolean[] controls;

	public ControlsPacket (boolean[] controls) {
		super(PacketType.CONTROLS);
		this.controls = controls;
	}
	
}
