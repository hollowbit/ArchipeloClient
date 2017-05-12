package net.hollowbit.archipelo.audio;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.tools.StaticTools;

public class MusicManager {

	
	private HashMap<String, Music> songs;
	
	public MusicManager () {
		songs = new HashMap<String, Music>();
		
		//Load all sounds form the sound list file
		String[] songPaths = StaticTools.getJson().fromJson(String[].class, Gdx.files.internal("shared/music.json"));
		for (String path : songPaths) {
			songs.put(path, Gdx.audio.newMusic(Gdx.files.internal("music/" + path + ".ogg")));
		}
	}
	
	public boolean hasSong(String path) {
		return songs.containsKey(path);
	}
	
	public void play(String path) {
		if (hasSong(path)) {
			songs.get(path).setVolume(getVolume());
			songs.get(path).setLooping(false);
			songs.get(path).play();
		}
	}
	
	public void loop(String path) {
		if (hasSong(path)) {
			songs.get(path).setVolume(getVolume());
			songs.get(path).setLooping(true);
			songs.get(path).play();
		}
	}
	
	public void pause(String path) {
		if (hasSong(path))
			songs.get(path).pause();
	}

	public void stop(String path) {
		if (hasSong(path))
			songs.get(path).stop();
	}
	
	public float getVolume() {
		return ArchipeloClient.getGame().getPrefs().getMasterVolume() * ArchipeloClient.getGame().getPrefs().getMusicVolume();
	}
	
}
