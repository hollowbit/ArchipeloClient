package net.hollowbit.archipelo.audio;

import com.badlogic.gdx.audio.Sound;

/**
 * Simple sound wrapper class that keeps track of a specific id so you don't have to.
 * @author vedi0boy
 *
 */
public class Soundlet {
	
	private Sound sound;
	private long id;
	
	public Soundlet (Sound sound) {
		this.sound = sound;
		id = -1;
	}
	
	public void play() {
		if (sound != null)
			id = sound.play();
	}
	
	public void play(float volume) {
		if (sound != null)
			id = sound.play(volume);
	}
	
	public void play(float volume, float pitch, float pan) {
		if (sound != null)
			id = sound.play(volume, pitch, pan);
	}
	
	public void loop() {
		if (sound != null)
			id = sound.loop();
	}
	
	public void loop(float volume) {
		if (sound != null)
			id = sound.loop(volume);
	}
	
	public void loop(float volume, float pitch, float pan) {
		if (sound != null)
			id = sound.loop(volume, pitch, pan);
	}
	
	public void setPan(float pan, float volume) {
		if (sound != null)
			sound.setPan(id, pan, volume);
	}
	
	public void setPitch(float pitch) {
		if (sound != null)
			sound.setPitch(id, pitch);
	}
	
	public void stop() {
		if (sound != null)
			sound.stop(id);
	}
	
	public void resume() {
		if (sound != null)
			sound.resume(id);
	}
	
	public void pause() {
		if (sound != null)
			sound.pause(id);
	}
	
	/**
	 * Give the soundlet a new sound to play. Will stop the old one from playing.
	 * @param newSound
	 */
	public void redefine(Sound newSound) {
		if (sound != null)
			sound.stop(id);
		sound = newSound;
		id = -1;
	}
	
}
