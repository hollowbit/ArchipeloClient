package net.hollowbit.archipelo.tools;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.github.czyzby.websocket.WebSocket;
import com.github.czyzby.websocket.WebSocketAdapter;
import com.github.czyzby.websocket.data.WebSocketCloseCode;
import com.github.czyzby.websocket.net.ExtendedNet;

public class PingGetter {
	
	private WebSocket socket;
	private volatile boolean gotPing = false;
	private long timeSent = 0;
	private int ping = 0;
	
	public int getPing (String address, int port) {
		try {
			gotPing = false;
			timeSent = 0;
			socket = ExtendedNet.getNet().newSecureWebSocket(address, port, Gdx.files, "keystore", (Gdx.app.getType() == ApplicationType.Android ? "BKS" : "JKS"), "changeit", "changeit");
			//socket = ExtendedNet.getNet().newWebSocket(address, port);
			socket.addListener(getWebSocketListener());
			socket.connect();
			
			while (!gotPing) {}//Wait until we get a ping
			return ping;
		} catch (Exception e) {
			return 9999999;
		}
	}
	
	private WebSocketAdapter getWebSocketListener() {
        return new WebSocketAdapter() {
            @Override
            public boolean onOpen(final WebSocket webSocket) {
            	socket.send("ping");
            	timeSent = System.currentTimeMillis();
                return FULLY_HANDLED;
            }

            @Override
            public boolean onClose(final WebSocket webSocket, final WebSocketCloseCode code, final String reason) {
                return FULLY_HANDLED;
            }
            
			@Override
            public boolean onMessage(final WebSocket webSocket, final String message) {
        		if (message.equals("pong")) {
        			ping = (int) (System.currentTimeMillis() - timeSent);
        			gotPing = true;
        			socket.close();
        		}
        		return FULLY_HANDLED;
            }
            
            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
            	return FULLY_HANDLED;
            }
            
        };
    }
	
}
