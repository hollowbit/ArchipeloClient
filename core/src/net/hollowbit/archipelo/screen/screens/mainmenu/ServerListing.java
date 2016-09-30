package net.hollowbit.archipelo.screen.screens.mainmenu;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class ServerListing extends Table {
	
	private static final String[] REGION_NAME = {"World", "North America East", "North America West", "South America East", "South America West", "East Asia", "West Asia", "South Asia", "Eastern Europe", "Western Europe", "Northern Africa", "Southern Africa", "Oceania"};
	private static final String[] TRAFFIC_NAME = {"Low", "Medium", "High"};
	
	private Label nameLabel;
	private Label regionLabel;
	private Label trafficLabel;
	private Label pingLabel;
	private TextButton connectButton;
	
	public ServerListing (String name, int region, int traffic, int ping, Skin skin) {
		setBounds(0, 0, 300, 25);
		
		//Initialize labels
		nameLabel = new Label(name, skin);
		add(nameLabel).width(250);
		
		String color = "";
		if (ping < 50)
			color = "[GREEN]";
		else if (ping < 300)
			color = "[ORANGE]";
		else
			color = "[RED]";
		
		pingLabel = new Label(color + "" + ping + "ms", skin, "small");
		add(pingLabel).fill();

		connectButton = new TextButton("Connect", skin);
		add(connectButton);
		
		row();
		
		if (traffic == 0)
			color = "[GREEN]";
		else if (traffic == 1)
			color = "[ORANGE]";
		else
			color = "[RED]";
		
		trafficLabel = new Label("Traffic: " + color + "" + TRAFFIC_NAME[traffic], skin, "small");
		add(trafficLabel).width(250);
		
		regionLabel = new Label("Region: " + REGION_NAME[region], skin, "small");
		add(regionLabel);
		
	}
	
}
