package net.hollowbit.archipelo.audio;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import net.hollowbit.archipelo.tools.StaticTools;

public class SoundManager {
	
	private HashMap<String, Sound> sounds;
	
	public SoundManager () {
		sounds = new HashMap<String, Sound>();
		
		//Load all sounds form the sound list file
		String[] soundPaths = StaticTools.getJson().fromJson(String[].class, Gdx.files.internal("sounds/sounds.json"));
		for (String path : soundPaths) {
			sounds.put(path, Gdx.audio.newSound(Gdx.files.internal("sounds/" + path + ".ogg")));
		}
	}
	
	public Sound getSound(String path) {
		return sounds.get(path);
	}
	
}
