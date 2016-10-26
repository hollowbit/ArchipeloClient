package net.hollowbit.archipelo.hollowbitserver;

import java.util.ArrayList;
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
	private static final int TIMEOUT_LENGTH = 2000;//Time in milliseconds to wait to connect to HollowBitServer
	
	private static final float TIME_BETWEEN_QUERIES = 0.15f;
	
	public static final int CREATE_PACKET_ID = 0;
	public static final int UPDATE_PACKET_ID = 1;
	public static final int VERIFY_PACKET_ID = 2;
	public static final int FORGOT_PASSWORD_PACKET_ID = 3;
	public static final int GET_USER_DATA_PACKET_ID = 4;
	public static final int GET_SERVER_LIST_PACKET_ID = 5;
	public static final int GET_SERVER_BY_NAME_PACKET_ID = 6;
	
	//Response packet ids
	public static final int CORRECT_LOGIN_RESPONSE_PACKET_ID = 0;
	public static final int CREATE_SUCCESSFUL_RESPONSE_PACKET_ID = 1;
	public static final int UPDATE_SUCCESSFUL_RESPONSE_PACKET_ID = 2;
	public static final int USER_DATA_RESPONSE_PACKET_ID = 3;
	public static final int SERVER_LIST_RESPONSE_PACKET_ID = 4;
	public static final int SERVER_GET_RESPONSE_PACKET_ID = 5;
	
	public static final int INVALID_PACKET_REPONSE_PACKET_ID = 6;
	public static final int USER_ALREADY_EXISTS_ERROR_RESPONSE_PACKET_ID = 7;
	public static final int USER_DOESNT_EXIST_ERROR_RESPONSE_PACKET_ID = 8;
	public static final int WRONG_PASSWORD_RESPONSE_PACKET_ID = 9;
	public static final int REQUEST_IGNORED_RESPONSE_PACKET_ID = 10;
	public static final int INVALID_EMAIL_RESPONSE_PACKET_ID = 11;
	public static final int INVALID_PASSWORD_RESPONSE_PACKET_ID = 12;
	public static final int SERVER_NOT_FOUND_RESPONSE_PACKET_ID = 13;
	public static final int TEMP_BAN_RESPONSE_PACKET_ID = 15;
	
	private HashMap<String, HollowBitServerQueryResponseHandler> handlerMap;
	private ArrayList<String> queries;
	private WebSocket socket;
	
	private volatile boolean isConnected = false;
	
	private long startTime;
	private float timer = 0;
	
	public HollowBitServerConnectivity () {
		handlerMap = new HashMap<String, HollowBitServerQueryResponseHandler>();//Create map for handlers
		queries = new ArrayList<String>();
	}
	
	public boolean connect () {
		try {
			//Connect to HollowBit server and set listener
			socket = ExtendedNet.getNet().newSecureWebSocket(ADDRESS, PORT, Gdx.files, "keystore", (Gdx.app.getType() == ApplicationType.Android ? "BKS" : "JKS"), "changeit", "changeit");
			socket.addListener(getWebSocketListener());
			socket.connect();
			
			startTime = System.currentTimeMillis();
			while (!isConnected) {//Wait until connected or until timeout
				if (System.currentTimeMillis() - startTime > TIMEOUT_LENGTH)
					return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Executes queries in order to avoid a too many queries ban
	 * @param deltaTime
	 */
	public synchronized void update (float deltaTime) {
		//If not connected by this point, open ErrorScreen
		if (!isConnected)
			ArchipeloClient.getGame().getScreenManager().setScreen(new ErrorScreen("Could not connect to login server. Try another time."));
		
		timer += deltaTime;
		if (timer >= TIME_BETWEEN_QUERIES && queries.size() > 0) {
			timer -= TIME_BETWEEN_QUERIES;
			
			//Execute next query
			socket.send(queries.get(0));
			queries.remove(0);
		}
	}
	
	/**
	 * Sends the raw query to HollowBit server and puts the listener in the handlerMap
	 * @param query Query string to send to server
	 * @param handler Handles response to queries
	 */
	private synchronized void sendQuery (String query, HollowBitServerQueryResponseHandler handler) {
		String uuid = UUID.randomUUID().toString();
		handlerMap.put(uuid, handler);
		String finalQuery = uuid + "/" + query;
		if (isConnected)
			queries.add(finalQuery);
		else
			handler.responseReceived(-1, new String[]{"No data"});
	}
	
	/**
	 * Send query to HollowBit server to create a new user.
	 * @param email Email of user
	 * @param password Password for user used to authenticate
	 * @param handler Handles response to queries
	 */
	public void sendCreateQuery (String email, String password, HollowBitServerQueryResponseHandler handler) {
		String query = CREATE_PACKET_ID + ";" + email + ";" + password;
		sendQuery(query, handler);
	}
	
	/**
	 * Send query to HollowBit server to update user data, such as password, email & name.
	 * @param email Email of user
	 * @param newEmail New email of user
	 * @param password Password for user used to authenticate
	 * @param newPassword New password, if it is different
	 * @param handler Handles response to queries
	 */
	public void sendUpdateQuery (String email, String newEmail, String password, String newPassword, HollowBitServerQueryResponseHandler handler) {
		String query = UPDATE_PACKET_ID + ";" + email + ";" + newEmail + ";" + password + ";" + newPassword;
		sendQuery(query, handler);
	}
	
	/**
	 * Send query to HollowBit server to see if login credentials are correct.
	 * @param email Email of user
	 * @param password Password for user used to authenticate
	 * @param handler Handles response to queries
	 */
	public void sendVerifyQuery (String email, String password, HollowBitServerQueryResponseHandler handler) {
		String query = VERIFY_PACKET_ID + ";" + email + ";" + password;
		sendQuery(query, handler);
	}
	
	/**
	 * Send query to HollowBit server to request a new temporary password if password was forgotten
	 * @param email Email of user
	 * @param handler Handles response to queries
	 */
	public void sendForgotQuery (String email, HollowBitServerQueryResponseHandler handler) {
		String query = FORGOT_PASSWORD_PACKET_ID + ";" + email;
		sendQuery(query, handler);
	}
	
	/**
	 * Send query to HollowBit server to get all server for Archipelo
	 * @param handler Handles response to queries
	 */
	public void sendGetServerListQuery (HollowBitServerQueryResponseHandler handler) {
		String query =  GET_SERVER_LIST_PACKET_ID + ";0";//Archipelo game id is 0
		sendQuery(query, handler);
	}
	
	/**
	 * Get server address by name
	 * @param name
	 * @param handler
	 */
	public void sendGetServerByNameQuery (String name, HollowBitServerQueryResponseHandler handler) {
		String query = GET_SERVER_BY_NAME_PACKET_ID + ";0;" + name;
		sendQuery(query, handler);
	}
	
	/**
	 * Send query to HollowBit server to get data about a user.
	 * @param email Email of user
	 * @param password Password for user used to authenticate
	 * @param handler Handles response to queries
	 */
	public void sendGetUserDataQuery (String email, String password, HollowBitServerQueryResponseHandler handler) {
		String query = GET_USER_DATA_PACKET_ID + ";" + email + ";" + password;
		sendQuery(query, handler);
	}
	
	private WebSocketAdapter getWebSocketListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onOpen(final WebSocket webSocket) {
                Gdx.app.log("WS", "Connected to HB!");
                isConnected = true;
                return FULLY_HANDLED;
            }

            @Override
            public boolean onClose(final WebSocket webSocket, final WebSocketCloseCode code, final String reason) {
                isConnected = false;
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
        			
        			handlerMap.get(packetId).responseReceived(dataId, newData);//Handle response packet
        			handlerMap.remove(packetId);//Remove handler
        		} catch (Exception e) {
        			return NOT_HANDLED;
        		}
        		return FULLY_HANDLED;
            }
            
            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
            	Gdx.app.log("WS", "Error from HB: " + error.getMessage());
				ArchipeloClient.getGame().getScreenManager().setScreen(new ErrorScreen("Could not connect to login server. Try another time."));
            	return FULLY_HANDLED;
            }
            
        };
    }
	
	public boolean isConnected () {
		return isConnected;
	}
	
}
