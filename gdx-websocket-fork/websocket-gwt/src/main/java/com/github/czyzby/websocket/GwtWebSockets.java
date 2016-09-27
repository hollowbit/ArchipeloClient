package com.github.czyzby.websocket;

import com.badlogic.gdx.Files;
import com.github.czyzby.websocket.WebSockets.WebSocketFactory;
import com.github.czyzby.websocket.impl.GwtWebSocket;

/** Allows to initiate GWT web sockets module.
 *
 * @author MJ */
public class GwtWebSockets {
    private GwtWebSockets() {
    }

    /** Initiates {@link WebSocketFactory}. */
    public static void initiate() {
        WebSockets.FACTORY = new GwtWebSocketFactory();
    }

    /** Provides {@link GwtWebSocket} instances.
     *
     * @author MJ */
    protected static class GwtWebSocketFactory implements WebSocketFactory {
        @Override
        public WebSocket newWebSocket(final String url) {
            return new GwtWebSocket(url);
        }

		@Override
		public WebSocket newSecureWebSocket(String url, Files gdxFiles, String keystorePath, String storeType, String keyPass, String storePass) {
			return new GwtWebSocket(url);
		}
    }
}
