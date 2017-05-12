package net.hollowbit.archipelo.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import net.hollowbit.archipelo.tools.LanguageSpecificMessageManager.Language;

public class Prefs {
	
	private Preferences prefs;
	private boolean loggedIn, serverPicked, showedDisclaimer;
	private String email, password, serverName;
	private int chosenLanguage;
	
	private float masterVolume;
	private float sfxVolume;
	private float musicVolume;
	
	public Prefs () {
		prefs = Gdx.app.getPreferences("archipelo");
		
		//Login
		this.loggedIn = prefs.getBoolean("logged-in", false);
		this.email = prefs.getString("email", "");
		this.password = prefs.getString("password", "");
		
		//Server
		this.serverPicked = prefs.getBoolean("server-picked", false);
		this.serverName = prefs.getString("server-name", "");
		
		//Sound
		this.masterVolume = prefs.getFloat("master-volume", 1);
		this.sfxVolume = prefs.getFloat("sfx-volume", 1);
		this.musicVolume = prefs.getFloat("music-volume", 1);
		
		//Other
		this.showedDisclaimer = prefs.getBoolean("showed-disclaimer", false);
		this.chosenLanguage = prefs.getInteger("chosen-language", 0);
	}
	
	public void resetLogin () {
		this.loggedIn = false;
		this.email = "";
		this.password = "";
		prefs.putBoolean("logged-in", false);
		prefs.putString("email", "");
		prefs.putString("password", "");
		prefs.flush();
	}
	
	public void setLogin (String username, String password) {
		this.loggedIn = true;
		this.email = username;
		this.password = password;
		prefs.putBoolean("logged-in", true);
		prefs.putString("email", username);
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
	
	public String getEmail () {
		return email;
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
	
	public Language getChosenLanguage () {
		return LanguageSpecificMessageManager.Language.values()[this.chosenLanguage];
	}
	
	public void setChosenLanguage (int chosenLanguage) {
		this.chosenLanguage = chosenLanguage;
	}
	
	public int getChosenLanguageRaw () {
		return this.chosenLanguage;
	}
	
	public float getMasterVolume() {
		return masterVolume;
	}

	public void setMasterVolume(float masterVolume) {
		this.masterVolume = masterVolume;
		prefs.putFloat("master-volume", masterVolume);
		prefs.flush();
	}

	public float getSfxVolume() {
		return sfxVolume;
	}

	public void setSfxVolume(float sfxVolume) {
		this.sfxVolume = sfxVolume;
		prefs.putFloat("sfx-volume", sfxVolume);
		prefs.flush();
	}

	public float getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(float musicVolume) {
		this.musicVolume = musicVolume;
		prefs.putFloat("music-volume", musicVolume);
		prefs.flush();
	}
	
}
