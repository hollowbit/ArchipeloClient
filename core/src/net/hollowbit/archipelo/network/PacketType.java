package net.hollowbit.archipelo.network;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;

@SuppressWarnings({ "unchecked" })
public class PacketType {
	
	public static final int MESSAGE = 0;
	public static final int LOGIN = 1;
	public static final int LOGOUT = 2;
	public static final int WORLD_SNAPSHOT = 3;
	public static final int ENTITY_ADD = 4;
	public static final int ENTITY_REMOVE = 5;
	public static final int CONTROLS = 6;
	////////Empty spot for 7
	public static final int POPUP_TEXT = 8;
	public static final int CHAT_MESSAGE = 9;
	public static final int TELEPORT = 10;
	public static final int PLAYER_PICK = 11;
	public static final int PLAYER_LIST = 12;
	public static final int PLAYER_DELETE = 13;
	public static final int NPC_DIALOG = 14;
	public static final int NPC_DIALOG_REQUEST = 15;
	public static final int FLAGS_ADD = 16;
	public static final int FORM_INTERACT = 17;
	public static final int FORM_REQUEST = 18;
	public static final int FORM_DATA = 19;
	public static final int POSITION_CORRECTION = 20;
	public static final int PLAYER_STATS = 21;
	
	private static HashMap<Integer, Class<Packet>> registeredPackets;
	
	static {
		registeredPackets = new HashMap<Integer, Class<Packet>>();
		try {
			registeredPackets.put(MESSAGE, ClassReflection.forName("net.hollowbit.archipelo.network.packets.MessagePacket"));
			registeredPackets.put(LOGIN, ClassReflection.forName("net.hollowbit.archipelo.network.packets.LoginPacket"));
			registeredPackets.put(LOGOUT, ClassReflection.forName("net.hollowbit.archipelo.network.packets.LogoutPacket"));
			registeredPackets.put(WORLD_SNAPSHOT, ClassReflection.forName("net.hollowbit.archipelo.network.packets.WorldSnapshotPacket"));
			registeredPackets.put(ENTITY_ADD, ClassReflection.forName("net.hollowbit.archipelo.network.packets.EntityAddPacket"));
			registeredPackets.put(ENTITY_REMOVE, ClassReflection.forName("net.hollowbit.archipelo.network.packets.EntityRemovePacket"));
			registeredPackets.put(CONTROLS, ClassReflection.forName("net.hollowbit.archipelo.network.packets.ControlsPacket"));
			registeredPackets.put(POPUP_TEXT, ClassReflection.forName("net.hollowbit.archipelo.network.packets.PopupTextPacket"));
			registeredPackets.put(CHAT_MESSAGE, ClassReflection.forName("net.hollowbit.archipelo.network.packets.ChatMessagePacket"));
			registeredPackets.put(TELEPORT, ClassReflection.forName("net.hollowbit.archipelo.network.packets.TeleportPacket"));
			registeredPackets.put(PLAYER_PICK, ClassReflection.forName("net.hollowbit.archipelo.network.packets.PlayerPickPacket"));
			registeredPackets.put(PLAYER_LIST, ClassReflection.forName("net.hollowbit.archipelo.network.packets.PlayerListPacket"));
			registeredPackets.put(PLAYER_DELETE, ClassReflection.forName("net.hollowbit.archipelo.network.packets.PlayerDeletePacket"));
			registeredPackets.put(NPC_DIALOG, ClassReflection.forName("net.hollowbit.archipelo.network.packets.NpcDialogPacket"));
			registeredPackets.put(NPC_DIALOG_REQUEST, ClassReflection.forName("net.hollowbit.archipelo.network.packets.NpcDialogRequestPacket"));
			registeredPackets.put(FLAGS_ADD, ClassReflection.forName("net.hollowbit.archipelo.network.packets.FlagsAddPacket"));
			registeredPackets.put(FORM_INTERACT, ClassReflection.forName("net.hollowbit.archipelo.network.packets.FormInteractPacket"));
			registeredPackets.put(FORM_REQUEST, ClassReflection.forName("net.hollowbit.archipelo.network.packets.FormRequestPacket"));
			registeredPackets.put(FORM_DATA, ClassReflection.forName("net.hollowbit.archipelo.network.packets.FormDataPacket"));
			registeredPackets.put(POSITION_CORRECTION, ClassReflection.forName("net.hollowbit.archipelo.network.packets.PositionCorrectionPacket"));
			registeredPackets.put(PLAYER_STATS, ClassReflection.forName("net.hollowbit.archipelo.network.packets.PlayerStatsPacket"));
		} catch (Exception e) {
			System.out.println("Was unable to register all packet.");
			Gdx.app.exit();
		}
	}
	
	public static HashMap<Integer, Class<Packet>> getRegisteredPackets () {
		return registeredPackets;
	}
	
	public static Class<Packet> getPacketClassByType (int type) {
		return registeredPackets.get(type);
	}
	
	public static String getPacketNameByType (int type) {
		return registeredPackets.get(type).getSimpleName();
	}
	
}
