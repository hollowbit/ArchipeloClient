/*
 * Author: vedi0boy
 * Company: Vediogames
 * Please see the Github README.md before using this code.
 */
package net.hollowbit.archipelo.network;

import java.util.ArrayList;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import com.github.czyzby.websocket.net.ExtendedNet;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.screen.screens.ErrorScreen;
import net.hollowbit.archipelo.screen.screens.MainMenuScreen;
import net.hollowbit.archipelo.tools.LM;

public class NetworkManager {
	
	private ArrayList<PacketHandler> packetHandlers;
	private ArrayList<Packet> packets;
	private Json json;
	
	private WebSocket socket;
	
	public NetworkManager () {
		packetHandlers = new ArrayList<PacketHandler>();
		packets = new ArrayList<Packet>();
		json = new Json();
	}
	
	public void update () {
		ArrayList<Packet> currentPackets = new ArrayList<Packet>();
		currentPackets.addAll(packets);
		
		ArrayList<Packet> packetsToRemove = new ArrayList<Packet>();
		for (Packet packet : currentPackets) {
			ArrayList<PacketHandler> currentPacketHandlers = new ArrayList<PacketHandler>();
			currentPacketHandlers.addAll(packetHandlers);
			
			for (PacketHandler packetHandler : currentPacketHandlers) {
				if (packetHandler.handlePacket(packet))
					packetsToRemove.add(packet);
			}
		}

		removeAllPackets(packetsToRemove);
	}
	
	private synchronized void removeAllPackets (ArrayList<Packet> packetsToRemove) {
		this.packets.removeAll(packetsToRemove);
	}
	
	private synchronized void removeAllPacketHandlers () {
		packetHandlers.clear();
	}
	
	private synchronized void addPacket (Packet packet) {
		packets.add(packet);
	}
	
	public void connect (String address, int port) {
		try {
			socket = ExtendedNet.getNet().newSecureWebSocket(address, port, Gdx.files, "keystore", (Gdx.app.getType() == ApplicationType.Android ? "BKS" : "JKS"), "changeit", "changeit");
			//socket = ExtendedNet.getNet().newWebSocket(address, port);
			socket.addListener(getWebSocketListener());
			socket.connect();
		} catch (Exception e) {
			ArchipeloClient.getGame().getScreenManager().setScreen(new ErrorScreen(LM.error("unableToConnect"), e));
		}
	}
	
	public void disconnect () {
		if (socket != null)
			socket.close();
	}
	
	public void sendPacket (Packet packet) {
		if (socket != null) {
			String packetString = packet.packetType + ";" + json.toJson(packet);
			socket.send(packetString);
		}
	}
	
	public void sendPacketString (String packetString) {
		if (socket != null)
			socket.send(packetString);
	}
	
	public String getPacketString (Packet packet) {
		return packet.packetType + ";" + json.toJson(packet);
	}
	
	public synchronized void addPacketHandler (PacketHandler packetHandler) {
		packetHandlers.add(packetHandler);
	}
	
	public synchronized void removePacketHandler (PacketHandler packetHandler) {
		packetHandlers.remove(packetHandler);
	}
	
	public boolean isConnected () {
		if (socket == null)
			return false;
		return socket.isOpen();
	}
	
	private WebSocketAdapter getWebSocketListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onOpen(final WebSocket webSocket) {
                Gdx.app.log("WS", "Connected!");
                return FULLY_HANDLED;
            }

            @Override
            public boolean onClose(final WebSocket webSocket, final WebSocketCloseCode code, final String reason) {
                Gdx.app.log("WS", "Disconnected - status: " + code + ", reason: " + reason);
                removeAllPacketHandlers();
                if (ArchipeloClient.getGame().getScreenManager().getScreenType() == ScreenType.GAME)
                	ArchipeloClient.getGame().getScreenManager().setScreen(new MainMenuScreen(LM.ui("lostConnection")));
                return FULLY_HANDLED;
            }

            @SuppressWarnings("unchecked")
			@Override
            public boolean onMessage(final WebSocket webSocket, final String packetString) {
            	try {
        			Packet packet;
        			String[] packetWrapArray = packetString.split(";");
        			int type = Integer.parseInt(packetWrapArray[0]);
        			String newPacketString = packetWrapArray[1];
        			packet = (Packet) json.fromJson(PacketType.getPacketClassByType(type), newPacketString);
        			addPacket(packet);
        		} catch (Exception e) {
        			return NOT_HANDLED;
        		}
        		return FULLY_HANDLED;
            }
            
            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
            	Gdx.app.log("WS", "Error: " + error.getMessage());
            	return FULLY_HANDLED;
            }
            
        };
    }
	
}
