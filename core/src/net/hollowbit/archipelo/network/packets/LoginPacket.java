package net.hollowbit.archipelo.network.packets;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class LoginPacket extends Packet {
	
	public static final int RESULT_SUCCESS= 0;
	public static final int RESULT_ALREADY_LOGGED_IN = 1;
	public static final int RESULT_NO_USER_WITH_NAME = 2;
	public static final int RESULT_PASSWORD_WRONG = 3;
	public static final int RESULT_USERNAME_TAKEN = 4;
	public static final int RESULT_BAD_VERSION = 5;
	public static final int RESULT_INVALID_USERNAME = 6;
	public static final int RESULT_INVALID_PASSWORD = 7;
	
	public String username;
	public String password;
	public int result;
	public boolean registering;
	public String version;
	public boolean hasCreatedPlayer;
	
	public LoginPacket () {
		super(PacketType.LOGIN);
	}
	
	public LoginPacket (String username, String password, boolean registering) {
		super(PacketType.LOGIN);
		this.username = username;
		this.password = password;
		this.registering = registering;
		this.version = ArchipeloClient.VERSION;
	}

}
