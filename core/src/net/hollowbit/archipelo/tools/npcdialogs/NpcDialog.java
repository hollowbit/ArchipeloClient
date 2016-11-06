package net.hollowbit.archipelo.tools.npcdialogs;

import java.util.ArrayList;

public class NpcDialog {
	//Npc dialog with some default messages
	public String id = "";
	public String name = "?";//Npc's name
	public String message = "!?!!?";
	public ArrayList<String> choices;
	
	/**
	 * Used to make ids uppercase and add category name
	 * @param name
	 */
	public void processIds (String name) {
		this.id = (name + this.id).toUpperCase();
	}
}