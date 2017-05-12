package net.hollowbit.archipelo.audio;

import net.hollowbit.archipelo.ArchipeloClient;

/**
 * Simple sound wrapper class that keeps track of a specific id so you don't have to.
 * @author vedi0boy
 *
 */
public class Soundlet {
	
	private String sound;
	private long id;
	
	public Soundlet (String soundId) {
		this.sound = soundId;
		id = -1;
	}
	
	public void play() {
		id = ArchipeloClient.getGame().getSoundManager().play(sound);
	}
	
	public void play(float volume) {
		id = ArchipeloClient.getGame().getSoundManager().play(sound, volume);
	}
	
	public void play(float volume, float pitch, float pan) {
		id = ArchipeloClient.getGame().getSoundManager().play(sound, volume, pitch, pan);
	}
	
	public void loop() {
		id = ArchipeloClient.getGame().getSoundManager().loop(sound);
	}
	
	public void loop(float volume) {
		id = ArchipeloClient.getGame().getSoundManager().loop(sound, volume);
	}
	
	public void loop(float volume, float pitch, float pan) {
		id = ArchipeloClient.getGame().getSoundManager().loop(sound, volume, pitch, pan);
	}
	
	public void setPan(float pan, float volume) {
		ArchipeloClient.getGame().getSoundManager().setPan(sound, id, pan, volume);
	}
	
	public void setPitch(float pitch) {
		ArchipeloClient.getGame().getSoundManager().setPitch(sound, id, pitch);
	}
	
	public void stop() {
		ArchipeloClient.getGame().getSoundManager().stop(sound, id);
	}
	
	public void resume() {
		ArchipeloClient.getGame().getSoundManager().resume(sound, id);
	}
	
	public void pause() {
		ArchipeloClient.getGame().getSoundManager().pause(sound, id);
	}
	
	/**
	 * Give the soundlet a new sound to play. Will stop the old one from playing.
	 * @param newSound
	 */
	public void redefine(String newSoundId) {
		ArchipeloClient.getGame().getSoundManager().stop(sound, id);
		sound = newSoundId;
		id = -1;
	}
	
}
