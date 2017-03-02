package net.hollowbit.archipelo.screen.screens.mainmenu;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.form.MobileCompatibleWindow;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerConnectivity;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerQueryResponseHandler;
import net.hollowbit.archipelo.network.PingGetter;
import net.hollowbit.archipelo.tools.LM;

public class ServerPickerWindow extends MobileCompatibleWindow {
	
	ScrollPane serverListScrollPane;
	Label infoLabel;
	PingGetter pingGetter;
	TextButton refreshButton;
	TextButton exitButton;
	Table serverListTable;
	
	public ServerPickerWindow () {
		super(LM.ui("pickServer"), ArchipeloClient.getGame().getUiSkin());
		pingGetter = new PingGetter();
		
		setMovable(false);
		
		infoLabel = new Label(LM.ui("gettingServerInfo"), getSkin(), "small");
		add(infoLabel).pad(5).colspan(2);
		
		row();

		serverListTable = new Table(getSkin());
		serverListScrollPane = new ScrollPane(serverListTable, getSkin());
		serverListScrollPane.setScrollbarsOnTop(true);
		add(serverListScrollPane).width(600).height(400).colspan(2);
		
		row();
		
		refreshButton = new TextButton(LM.ui("refresh"), getSkin());
		refreshButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				loadServerList();
				super.clicked(event, x, y);
			}
		});
		add(refreshButton).pad(5);
		
		if (!ArchipeloClient.IS_GWT) {
			exitButton = new TextButton(LM.ui("exit"), getSkin());
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
		
		loadServerList();
	}
	
	public void loadServerList() {
		serverListTable.clearChildren();
		ArchipeloClient.getGame().getHollowBitServerConnectivity().sendGetServerListQuery(new HollowBitServerQueryResponseHandler() {
			
			@Override
			public void responseReceived(int id, String[] data) {
				if (id != HollowBitServerConnectivity.SERVER_LIST_RESPONSE_PACKET_ID) {
					infoLabel.setText(LM.ui("couldNotGetServerInfo"));
					pack();
					return;
				}
				
				try {
					ArrayList<ServerListing> servers = new ArrayList<ServerListing>();
					for (String serverDataRaw : data) {
						String[] serverData = serverDataRaw.split(",");
						String name = serverData[0];
						int region = Integer.parseInt(serverData[1]);
						int traffic = Integer.parseInt(serverData[2]);
						String hostname = serverData[3];
						
						servers.add(new ServerListing(name, region, traffic, hostname, getSkin()));
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
						
						infoLabel.setText(LM.ui("pickServerToCon") + ":");
					} else {
						infoLabel.setText(LM.ui("couldNotfindServers"));
					}
					
					getCell(serverListScrollPane).width(serverListTable.getWidth() + 5);
					pack();
				} catch (Exception e) {
					infoLabel.setText(LM.ui("couldNotGetServerInfo"));
					pack();
				}
			}
		});
	}
	
}
