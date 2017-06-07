package net.hollowbit.archipelo.audio;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.tools.StaticTools;

public class SoundManager {
	
	private HashMap<String, Sound> sounds;
	
	public SoundManager () {
		sounds = new HashMap<String, Sound>();
		
		//Load all sounds form the sound list file
		String[] soundPaths = StaticTools.getJson().fromJson(String[].class, Gdx.files.internal("shared/sounds.json"));
		for (String path : soundPaths) {
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("sounds/" + path + ".ogg"));
			sounds.put(path, sound);
		}
	}
	
	public void loadSound(String path) {
		if (!hasSound(path))
			sounds.put(path, Gdx.audio.newSound(Gdx.files.internal("sounds/" + path + ".ogg")));
	}
	
	public boolean hasSound(String path) {
		if (path != null && !path.equals(""))
			return sounds.containsKey(path);
		return false;
	}
	
	public long play(String path) {
		if (hasSound(path))
			return sounds.get(path).play(getVolume());
		return -1;
	}
	
	public long play(String path, float volume) {
		if (hasSound(path))
			return sounds.get(path).play(volume * getVolume());
		return -1;
	}
	
	public long play(String path, float volume, float pitch, float pan) {
		if (hasSound(path))
			return sounds.get(path).play(volume * getVolume(), pitch, pan);
		return -1;
	}
	
	public long loop(String path) {
		if (hasSound(path))
			return sounds.get(path).loop(getVolume());
		return -1;
	}
	
	public long loop(String path, float volume) {
		if (hasSound(path))
			return sounds.get(path).loop(volume * getVolume());
		return -1;
	}
	
	public long loop(String path, float volume, float pitch, float pan) {
		if (hasSound(path))
			return sounds.get(path).loop(volume * getVolume(), pitch, pan);
		return -1;
	}
	
	public void setPan(String path, long id, float pan, float volume) {
		if (hasSound(path))
			sounds.get(path).setPan(id, pan, volume * getVolume());
	}
	
	public void setPitch(String path, long id, float pitch) {
		if (hasSound(path))
			sounds.get(path).setPitch(id, pitch);
	}
	
	public void stop(String path, long id) {
		if (hasSound(path))
			sounds.get(path).stop(id);
	}
	
	public void resume(String path, long id) {
		if (hasSound(path))
			sounds.get(path).resume(id);
	}
	
	public void pause(String path, long id) {
		if (hasSound(path))
			sounds.get(path).pause(id);
	}
	
	public float getVolume() {
		return ArchipeloClient.getGame().getPrefs().getMasterVolume() * ArchipeloClient.getGame().getPrefs().getSfxVolume();
	}
	
}
