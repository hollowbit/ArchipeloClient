package net.hollowbit.archipelo.screen.screens.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;

public class ServerListing extends Table implements Comparable<ServerListing>{
	
	private static final String[] REGION_NAME = {"World", "North America East", "North America West", "South America East", "South America West", "East Asia", "West Asia", "South Asia", "Eastern Europe", "Western Europe", "Northern Africa", "Southern Africa", "Oceania"};
	private static final String[] TRAFFIC_NAME = {"Low", "Medium", "High"};
	
	private Label nameLabel;
	private Label regionLabel;
	private Label trafficLabel;
	private Label pingLabel;
	private TextButton connectButton;
	private int ping;
	
	public ServerListing (final String name, int region, int traffic, int ping, final String address, Skin skin) {
		setBounds(0, 0, 300, 25);
		this.ping = ping;
		
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
		connectButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ArchipeloClient.getGame().getNetworkManager().connect(address, ArchipeloClient.PORT);
				ArchipeloClient.SERVER_PICKED = true;
				ArchipeloClient.SERVER = name;
				
				Preferences prefs = Gdx.app.getPreferences(ArchipeloClient.PREFS_NAME);
				prefs.putBoolean("server-picked", true);
				prefs.putString("server-name", name);
				prefs.flush();
				
				super.clicked(event, x, y);
			}
		});
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
	
	@Override
	public int compareTo (ServerListing o) {
		if (this.ping > o.ping)
			return 1;
		if (this.ping < o.ping)
			return -1;
		return 0;
	}
	
}
