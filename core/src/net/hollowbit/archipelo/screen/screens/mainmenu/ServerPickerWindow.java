package net.hollowbit.archipelo.screen.screens.mainmenu;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerQueryResponseHandler;
import net.hollowbit.archipelo.tools.PingGetter;

public class ServerPickerWindow extends Window {
	
	List<ServerListing> serverList;
	ScrollPane serverListScrollPane;
	Label infoLabel;
	PingGetter pingGetter;
	TextButton connectButton;
	
	public ServerPickerWindow(Skin skin) {
		super("Pick Server", skin);
		
		ArchipeloClient.getGame().getHollowBitServerConnectivity().sendGetServerListQuery(getHollowBitQueryResponseHandler());
		
		setBounds(0, 0, 300, 420);
		
		pingGetter = new PingGetter();
		
		infoLabel = new Label("Getting server info...", skin);
		add(infoLabel);
		
		row();
		
		serverList = new List<ServerListing>(skin);
		serverListScrollPane = new ScrollPane(serverList, skin);
		add(serverListScrollPane).width(300).height(400);
		
		row();
		
		connectButton = new TextButton("Connect", skin);
		add(connectButton);
	}
	
	private HollowBitServerQueryResponseHandler getHollowBitQueryResponseHandler () {
		return new HollowBitServerQueryResponseHandler() {
			
			@Override
			public void responceReceived(int id, String[] data) {
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
					serverList.setItems((ServerListing[]) servers.toArray());
					infoLabel.setText("Pick server to connect to:");
				} catch (Exception e) {
					infoLabel.setText("Could not get server info!");
				}
			}
		};
	}
	
}
