package net.hollowbit.archipelo.network;

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
	private PingGetterListener listener;
	
	public void getPing (String address, int port, PingGetterListener listener) {
		this.listener = listener;
		try {
			gotPing = false;
			timeSent = 0;
			socket = ExtendedNet.getNet().newSecureWebSocket(address, port, Gdx.files, "keystore", (Gdx.app.getType() == ApplicationType.Android ? "BKS" : "JKS"), "changeit", "changeit");
			//socket = ExtendedNet.getNet().newWebSocket(address, port);
			socket.addListener(getWebSocketListener());
			socket.connect();
		} catch (Exception e) {}
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
            	if (!gotPing) {
            		listener.pingRecieved(-1);
            	}
                return FULLY_HANDLED;
            }
            
			@Override
            public boolean onMessage(final WebSocket webSocket, final String message) {
        		if (message.equals("pong")) {
        			gotPing = true;
        			listener.pingRecieved((int) (System.currentTimeMillis() - timeSent));
        			socket.close();
        		}
        		return FULLY_HANDLED;
            }
            
            @Override
            public boolean onError(WebSocket webSocket, Throwable error) {
            	if (!gotPing) {
            		listener.pingRecieved(-1);
            	}
            	return FULLY_HANDLED;
            }
            
        };
    }
	
	public interface PingGetterListener {
		
		public abstract void pingRecieved (int ping);
		
	}
	
}
