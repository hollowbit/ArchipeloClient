package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class ControlsPacket extends Packet {
	
	private static int idNext = 0;
	
	public String c;
	public int id;

	public ControlsPacket (boolean[] controls) {
		super(PacketType.CONTROLS);
		this.c = "";
		for (int i = 0; i < controls.length; i++) {
			this.c += (controls[i] ? 1:0);
		}
		this.id = idNext++;
	}
	
	public boolean[] parse() {
		boolean[] controls = new boolean[c.length()];
		for (int i = 0; i < controls.length; i++) {
			controls[i] = (c.charAt(i) == '0' ? false : true);
		}
		return controls;
	}
	
}
