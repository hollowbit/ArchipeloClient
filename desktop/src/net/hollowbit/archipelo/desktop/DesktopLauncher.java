package net.hollowbit.archipelo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.czyzby.websocket.CommonWebSockets;

import net.hollowbit.archipelo.ArchipeloClient;

public class DesktopLauncher {
	public static void main (String[] args) {
		
		if (args.length < 4) {
			System.out.println("Invalid arguments!");
			return;
		}
		
		try {
			boolean vsync = Boolean.parseBoolean(args[0]);
			int width = Integer.parseInt(args[1]);
			int height = Integer.parseInt(args[2]);
			int maxFps = Integer.parseInt(args[3]);
			
			CommonWebSockets.initiate();
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			
			config.title = "Archipelo Client " + ArchipeloClient.VERSION;
			config.width = width;
			config.height = height;
			config.vSyncEnabled = vsync;
			config.foregroundFPS = maxFps;
			config.backgroundFPS = maxFps;
			
			new LwjglApplication(new ArchipeloClient(), config);
		} catch (Exception e) {
			System.out.println("Invalid arguments!");
		}
		
	}
}
