package net.hollowbit.archipelo.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Prefs {
	
	private Preferences prefs;
	private boolean loggedIn, serverPicked, showedDisclaimer;
	private String username, password, serverName;
	
	public Prefs () {
		prefs = Gdx.app.getPreferences("archipelo");
		
		//Login
		this.loggedIn = prefs.getBoolean("logged-in", false);
		this.username = prefs.getString("username", "");
		this.password = prefs.getString("password", "");
		
		//Server
		this.serverPicked = prefs.getBoolean("server-picked", false);
		this.serverName = prefs.getString("server-name", "");
		
		//Other
		this.showedDisclaimer = prefs.getBoolean("showed-disclaimer", false);
	}
	
	public void resetLogin () {
		this.loggedIn = false;
		this.username = "";
		this.password = "";
		prefs.putBoolean("logged-in", false);
		prefs.putString("username", "");
		prefs.putString("password", "");
		prefs.flush();
	}
	
	public void setLogin (String username, String password) {
		this.loggedIn = true;
		this.username = username;
		this.password = password;
		prefs.putBoolean("logged-in", true);
		prefs.putString("username", username);
		prefs.putString("password", password);
		prefs.flush();
	}
	
	public void resetServer () {
		this.serverPicked = false;
		this.serverName = "";
		prefs.putBoolean("server-picked", false);
		prefs.putString("server-name", "");
		prefs.flush();
	}
	
	public void setServer (String serverName) {
		this.serverPicked = true;
		this.serverName = serverName;
		prefs.putBoolean("server-picked", true);
		prefs.putString("server-name", serverName);
		prefs.flush();
	}
	
	public boolean isLoggedIn () {
		return loggedIn;
	}
	
	public boolean isServerPicked () {
		return serverPicked;
	}
	
	public String getUsername () {
		return username;
	}
	
	public String getPassword () {
		return password;
	}
	
	public String getServerName () {
		return serverName;
	}
	
	public boolean hasShownDisclaimer () {
		return showedDisclaimer;
	}
	
	public void setShowedDisclaimer () {
		this.showedDisclaimer = true;
		prefs.putBoolean("showed-disclaimer", true);
		prefs.flush();
	}
	
}
