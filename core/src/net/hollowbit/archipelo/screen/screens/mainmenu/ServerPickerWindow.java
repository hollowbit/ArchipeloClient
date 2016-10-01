package net.hollowbit.archipelo.screen.screens.mainmenu;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerQueryResponseHandler;
import net.hollowbit.archipelo.tools.PingGetter;

public class ServerPickerWindow extends Window {
	
	List<ServerListing> serverList;
	ScrollPane serverListScrollPane;
	Label infoLabel;
	PingGetter pingGetter;
	TextButton connectButton;
	
	public ServerPickerWindow () {
		super("Pick Server", ArchipeloClient.getGame().getUiSkin());
		
		setMovable(false);
		
		pingGetter = new PingGetter();
		
		infoLabel = new Label("Getting server info...", getSkin());
		add(infoLabel);
		
		row();
		
		serverList = new List<ServerListing>(getSkin());
		serverListScrollPane = new ScrollPane(serverList, getSkin());
		add(serverListScrollPane).width(300).height(400);
		
		row();
		
		connectButton = new TextButton("Connect", getSkin());
		connectButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				connect();
				super.clicked(event, x, y);
			}
		});
		add(connectButton);
	}
	
	public void loadServerList() {
		ArchipeloClient.getGame().getHollowBitServerConnectivity().sendGetServerListQuery(new HollowBitServerQueryResponseHandler() {
			
			@Override
			public void responseReceived(int id, String[] data) {
				if (id != 6) {
					infoLabel.setText("Could not get server info!");
					return;
				}
				
				try {
					serverList.clearItems();
					
					ArrayList<ServerListing> servers = new ArrayList<ServerListing>();
					for (String serverDataRaw : data) {
						String[] serverData = serverDataRaw.split(",");
						String name = serverData[0];
						int region = Integer.parseInt(serverData[1]);
						int traffic = Integer.parseInt(serverData[2]);
						String hostname = serverData[3];
						
						servers.add(new ServerListing(name, region, traffic, pingGetter.getPing(hostname, ArchipeloClient.PORT), getSkin()));
					}
					Collections.sort(servers);
					serverList.setItems((ServerListing[]) servers.toArray());
					infoLabel.setText("Pick server to connect to:");
				} catch (Exception e) {
					infoLabel.setText("Could not get server info!");
				}
			}
		});
	}
	
	public void connect () {
		
	}
	
}
