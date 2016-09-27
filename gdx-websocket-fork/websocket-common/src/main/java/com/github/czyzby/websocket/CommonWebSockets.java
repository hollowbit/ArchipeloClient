package com.github.czyzby.websocket;

import com.badlogic.gdx.Files;
import com.github.czyzby.websocket.WebSockets.WebSocketFactory;
import com.github.czyzby.websocket.impl.NvWebSocket;

/** Allows to initiate common web sockets module.
 *
 * @author MJ */
public class CommonWebSockets {
    private CommonWebSockets() {
    }

    /** Initiates {@link WebSocketFactory}. */
    public static void initiate() {
        WebSockets.FACTORY = new CommonWebSocketFactory();
    }

    /** Provides {@link NvWebSocket} instances.
     *
     * @author MJ */
    protected static class CommonWebSocketFactory implements WebSocketFactory {
        @Override
        public WebSocket newWebSocket(final String url) {
            return new NvWebSocket(url);
        }
        
        @Override
        public WebSocket newSecureWebSocket(final String url, final Files gdxFiles, final String keystorePath, final String storeType, final String keyPass, final String storePass) {
        	return new NvWebSocket(url, gdxFiles, keystorePath, storeType, keyPass, storePass);
        }
    }
}
