package net.hollowbit.archipelo.screen.screens.mainmenu;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerQueryResponseHandler;
import net.hollowbit.archipelo.tools.PingGetter;

public class ServerPickerWindow extends Window {
	
	ScrollPane serverListScrollPane;
	Label infoLabel;
	PingGetter pingGetter;
	TextButton refreshButton;
	TextButton exitButton;
	Table serverListTable;
	
	public ServerPickerWindow () {
		super("Pick Server", ArchipeloClient.getGame().getUiSkin());

		serverListTable = new Table(getSkin());
		serverListScrollPane = new ScrollPane(serverListTable, getSkin());
		pingGetter = new PingGetter();
		loadServerList();
		
		setMovable(false);
		
		infoLabel = new Label("Getting server info...", getSkin(), "small");
		add(infoLabel).pad(5).colspan(2);
		
		row();
		
		add(serverListScrollPane).width(600).height(400).colspan(2);
		
		row();
		
		refreshButton = new TextButton("Refresh", getSkin());
		refreshButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				loadServerList();
				super.clicked(event, x, y);
			}
		});
		add(refreshButton).pad(5);
		
		if (!ArchipeloClient.IS_GWT) {
			exitButton = new TextButton("Exit", getSkin());
			exitButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Gdx.app.exit();
					super.clicked(event, x, y);
				}
			});
			add(exitButton).pad(5);
		}
		
		pack();

	}
	
	public void loadServerList() {
		ArchipeloClient.getGame().getHollowBitServerConnectivity().sendGetServerListQuery(new HollowBitServerQueryResponseHandler() {
			
			@Override
			public void responseReceived(int id, String[] data) {
				if (id != 6) {
					infoLabel.setText("Could not get server info!");
					pack();
					return;
				}
				
				try {
					serverListTable.clearChildren();
					
					ArrayList<ServerListing> servers = new ArrayList<ServerListing>();
					for (String serverDataRaw : data) {
						String[] serverData = serverDataRaw.split(",");
						String name = serverData[0];
						int region = Integer.parseInt(serverData[1]);
						int traffic = Integer.parseInt(serverData[2]);
						String hostname = serverData[3];
						
						int ping = pingGetter.getPing(hostname, ArchipeloClient.PORT);
						if (ping < 0)//Means there was an error and could not connect to this server at all
							continue;
						
						servers.add(new ServerListing(name, region, traffic, ping, hostname, getSkin()));
					}
					
					if (servers.size() > 0) {
						Collections.sort(servers);
						
						for (ServerListing listing : servers) {
							//serverListTable.add(listing).padTop(30);
							//serverListTable.row();
							
							int size = serverListTable.getCells().size;
							if (size > 0)
								serverListTable.getCells().get(size - 1).expand(true, false);

							boolean moveToBottom = serverListScrollPane.getScrollY() >= serverListScrollPane.getMaxY() - 10;
							
							//Add message to table
							serverListTable.row();
							serverListTable.add(listing).expandY().growX().top().left().pad(15, 5, 15, 5);
							
							//Update chatPane so that getMaxY is updated
							serverListScrollPane.layout();
							
							//Adjust scoll if scroll is at bottom, otherwise, don't.
							if (moveToBottom)
								serverListScrollPane.setScrollY(serverListScrollPane.getMaxY());
						}
						serverListTable.pack();
						
						infoLabel.setText("Pick server to connect to:");
					} else {
						infoLabel.setText("Could not find any servers.");
					}
					
					getCell(serverListScrollPane).width(serverListTable.getWidth());
					pack();
				} catch (Exception e) {
					infoLabel.setText("Could not get server info!");
					pack();
				}
			}
		});
	}
	
}
