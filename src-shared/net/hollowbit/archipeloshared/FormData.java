package net.hollowbit.archipeloshared;

import java.util.HashMap;

public class FormData {
	
	public String type = "inventory";
	public String id = "";
	public HashMap<String, String> data;
	
	public FormData () {}
	
	public FormData (String type, String id, HashMap<String, String> data) {
		this.type = type;
		this.id = id;
		this.data = data;
	}
	
}
