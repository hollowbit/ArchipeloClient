package net.hollowbit.archipelo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.czyzby.websocket.CommonWebSockets;

import net.hollowbit.archipelo.ArchipeloClient;

public class DesktopLauncher {
	public static void main (String[] arg) {
		CommonWebSockets.initiate();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Archipelo Client " + ArchipeloClient.VERSION;
		config.width = 1280;
		config.height = 720;
		config.vSyncEnabled = true;
		
		//Fullscreen
		//config.width = 1920;
		//config.height = 1080;
		//config.fullscreen = true;
		
		//TODO: Test with unlimited FPS when optimizing
		config.foregroundFPS = 0;
		config.backgroundFPS = 0;
		//config.foregroundFPS = 60;
		//config.backgroundFPS = 60;
		
		new LwjglApplication(new ArchipeloClient(), config);
	}
}
