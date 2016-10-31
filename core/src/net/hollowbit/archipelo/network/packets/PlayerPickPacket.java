package net.hollowbit.archipelo.network.packets;

import com.badlogic.gdx.graphics.Color;

import net.hollowbit.archipelo.items.ItemType;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketType;

public class PlayerPickPacket extends Packet {
	
	public static final int RESULT_SUCCESSFUL = 0;
	public static final int RESULT_NAME_ALREADY_TAKEN = 1;
	public static final int RESULT_INVALID_USERNAME = 2;
	public static final int RESULT_ALREADY_LOGGED_IN = 3;
	public static final int RESULT_NO_PLAYER_WITH_NAME = 4;
	public static final int RESULT_TOO_MANY_CHARACTERS = 5;
	
	public static final ItemType[] HAIR_STYLES = {ItemType.HAIR1};
	public static final ItemType[] FACE_STYLES = {ItemType.FACE1};
	public static final ItemType BODY = ItemType.BODY;
	public static final ItemType SHIRT = ItemType.SHIRT_BASIC;
	public static final ItemType PANTS = ItemType.PANTS_BASIC;
	
	public static final Color[] HAIR_COLORS = {new Color(1, 1, 1, 1), new Color(0.627f, 0.412f, 0.071f, 1), new Color(0.843f, 0.824f, 0.275f, 1)};
	public static final Color[] EYE_COLORS = {Color.BLUE, Color.BROWN, Color.RED, Color.GREEN};
	public static final Color[] BODY_COLORS = {new Color(1, 1, 1, 1), new Color(0.7f, 0.5f, 0.08f, 1)};
	
	public String name;
	public boolean isNew = false;
	public int result = 0;
	
	//If character is new
	public int selectedHair, selectedFace;
	public int hairColor, eyeColor, bodyColor;
	
	/**
	 * Do not use this constructor!
	 */
	public PlayerPickPacket () {
		super(PacketType.PLAYER_PICK);
	}
	
	/**
	 * Used to log in with an existing character
	 * @param name
	 */
	public PlayerPickPacket (String name) {
		this();
		this.name = name;
		this.isNew = false;
	}
	
	/**
	 * Use when creating a new character
	 * @param name
	 * @param selectedHair
	 * @param selectedFace
	 * @param hairColor
	 * @param eyeColor
	 * @param bodyColor
	 */
	public PlayerPickPacket (String name, int selectedHair, int selectedFace, int hairColor, int eyeColor, int bodyColor) {
		this();
		this.name = name;
		this.selectedHair = selectedHair;
		this.selectedFace = selectedFace;
		this.hairColor = hairColor;
		this.eyeColor = eyeColor;
		this.bodyColor = bodyColor;
		this.isNew = true;
	}

}
