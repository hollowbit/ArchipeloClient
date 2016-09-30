package net.hollowbit.archipelo.hollowbitserver;

import java.util.HashMap;
import java.util.UUID;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import com.github.czyzby.websocket.net.ExtendedNet;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.screen.screens.ErrorScreen;

public class HollowBitServerConnectivity {
	
	public static final String ADDRESS = "localhost";
	private static final int PORT = 22123;
	
	private HashMap<String, HollowBitServerQueryResponseHandler> handlerMap;
	private WebSocket socket;
	
	public HollowBitServerConnectivity () {
		handlerMap = new HashMap<String, HollowBitServerQueryResponseHandler>();//Create map for handlers
		try {
			//Connect to HollowBit server and set listener
			socket = ExtendedNet.getNet().newSecureWebSocket(ADDRESS, PORT, Gdx.files, "keystore", (Gdx.app.getType() == ApplicationType.Android ? "BKS" : "JKS"), "changeit", "changeit");
			socket.addListener(getWebSocketListener());
			socket.connect();
		} catch (Exception e) {
			ArchipeloClient.getGame().getScreenManager().setScreen(new ErrorScreen("Unable to connect to HollowBit login server!", e));
		}
	}
	
	/**
	 * Sends the raw query to HollowBit server and puts the listener in the handlerMap
	 * @param query Query string to send to server
	 * @param handler Handles response to queries
	 */
	private void sendQuery (String query, HollowBitServerQueryResponseHandler handler) {
		String uuid = UUID.randomUUID().toString();
		handlerMap.put(uuid, handler);
		String finalQuery = uuid + "/" + query;
		socket.send(finalQuery);
	}
	
	/**
	 * Send query to HollowBit server to create a new user.
	 * @param name Name of HollowBit user
	 * @param password Password for user used to authenticate
	 * @param email E-mail address of user
	 * @param handler Handles response to queries
	 */
	public void sendCreateQuery (String name, String password, String email, HollowBitServerQueryResponseHandler handler) {
		String query = "0;" + name + ";" + password + ";" + email;
		sendQuery(query, handler);
	}
	
	/**
	 * Send query to HollowBit server to update user data, such as password, email & name.
	 * @param name Name of HollowBit user
	 * @param password Password for user used to authenticate
	 * @param newPassword New password, if it is different
	 * @param email E-mail address of user
	 * @param newName New name of user, if it is different
	 * @param handler Handles response to queries
	 */
	public void sendUpdateQuery (String name, String password, String newPassword, String email, String newName, HollowBitServerQueryResponseHandler handler) {
		String query = "1;" + name + ";" + password + ";" + newPassword + ";" + email + ";" + newName;
		sendQuery(query, handler);
	}
	
	/**
	 * Send query to HollowBit server to see if login credentials are correct.
	 * @param name Name of HollowBit user
	 * @param password Password for user used to authenticate
	 * @param handler Handles response to queries
	 */
	public void sendVerifyQuery (String name, String password, HollowBitServerQueryResponseHandler handler) {
		String query = "2;" + name + ";" + password;
		sendQuery(query, handler);
	}
	
	/**
	 * Send query to HollowBit server to request a new temporary password if password was forgotten
	 * @param name Name of HollowBit user
	 * @param email E-mail address of user
	 * @param handler Handles response to queries
	 */
	public void sendForgotQuery (String name, String email, HollowBitServerQueryResponseHandler handler) {
		String query = "3;" + name + ";" + email;
		sendQuery(query, handler);
	}
	
	/**
	 * Send query to HollowBit server to get all server for Archipelo
	 * @param handler Handles response to queries
	 */
	public void sendGetServerListQuery (HollowBitServerQueryResponseHandler handler) {
		String query = "4;0";//Archipelo game id is 0
		sendQuery(query, handler);
	}
	
	/**
	 * Send query to HollowBit server to get data about a user.
	 * @param name Name of HollowBit user
	 * @param password Password for user used to authenticate
	 * @param handler Handles response to queries
	 */
	public void sendGetUserDataQuery (String name, String password, HollowBitServerQueryResponseHandler handler) {
		String query = "5;" + name + ";" + password;
		sendQuery(query, handler);
	}
	
	private WebSocketAdapter getWebSocketListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onOpen(final WebSocket webSocket) {
                Gdx.app.log("WS", "Connected to HB!");
                return FULLY_HANDLED;
            }

            @Override
            public boolean onClose(final WebSocket webSocket, final WebSocketCloseCode code, final String reason) {
                Gdx.app.log("WS", "Disconnected from HB - status: " + code + ", reason: " + reason);
                return FULLY_HANDLED;
            }
            
			@Override
            public boolean onMessage(final WebSocket webSocket, final String message) {
            	try {
            		String[] splitter = message.split("/");//Split at bracket to seperate uuid
            		String packetId = splitter[0];//Get uuid
            		
            		String[] data = splitter[1].split(";");//split at semi-colon to get data parts
            		int dataId = Integer.parseInt(data[0]);//Set dataid to first entry in data
        			
            		String[] newData = new String[1];
            		if (data.length > 1) {//If there is some extra data
            			//Create new data array, basically using data, but with dataId removed
            			newData = new String[data.length - 1];
        				for (int i = 0; i < newData.length; i++)
        					newData[i] = data[i + 1];
            		} else {//Otherwise set some default data saying there isn't any
            			newData[0] = "No data";
            		}
        			
        			handlerMap.get(packetId).responceReceived(dataId, newData);//Handle response packet
        			handlerMap.remove(packetId);//Remove handler
        		} catch (Exception e) {
        			return NOT_HANDLED;
        		}
        		return FULLY_HANDLED;
            }
            
            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
            	Gdx.app.log("WS", "Error from HB: " + error.getMessage());
            	return FULLY_HANDLED;
            }
            
        };
    }
	
}
