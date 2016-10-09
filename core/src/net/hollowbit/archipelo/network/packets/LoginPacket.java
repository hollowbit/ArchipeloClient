package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class LoginPacket extends Packet {
	
	public static final int RESULT_LOGIN_SUCCESSFUL = 0;
	public static final int RESULT_LOGIN_ERROR = 1;
	public static final int RESULT_BAD_VERSION = 2;
	
	public String username;
	public String password;
	public int result;
	public String version;
	
	public LoginPacket () {
		super(PacketType.LOGIN);
	}
	
	public LoginPacket (String username, String password) {
		super(PacketType.LOGIN);
		this.username = username;
		this.password = password;
		this.version = ArchipeloClient.VERSION;
		this.version = ArchipeloClient.VERSION;
	}

}
