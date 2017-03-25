package net.hollowbit.archipelo.entity;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipelo.audio.SoundCalculator;
import net.hollowbit.archipelo.audio.Soundlet;

public class EntityAudioManager {
	
	//Keep track of current audio manager
	private static ArrayList<EntityAudioManager> audioManagers;
	
	static {
		audioManagers = new ArrayList<EntityAudioManager>();
	}
	
	public static void moveAll () {
		for (EntityAudioManager manager : audioManagers)
			manager.moved();
	}
	/////////////////////
	
	private String continuousName;
	private Soundlet continuousSound;
	private Entity entity;
	
	public EntityAudioManager (Entity entity, String sound) {
		this.entity = entity;
		this.continuousSound = new Soundlet(entity.getEntityType().getSound(sound));
		this.continuousName = sound;
		audioManagers.add(this);
	}
	
	/**
	 * Call on entity interpolation.
	 * @param snapshotTo
	 */
	public void change (EntitySnapshot snapshotTo) {
		setContinuousSound(snapshotTo.sound);
	}
	
	/**
	 * Call when a new changes snapshot is available.
	 * @param snapshot
	 */
	public void handleChanges(EntitySnapshot snapshot) {
		if (snapshot.sounds != null && !snapshot.sounds.isEmpty()) {
			Vector2 entityPos = entity.getCenterPointTile();
			float volume = SoundCalculator.calculateVolume((int) entityPos.x, (int) entityPos.y);
			float pan = SoundCalculator.calculatePan((int) entityPos.x);
			
			//Loop through sounds to play and play them all
			for (String soundName : snapshot.sounds) {
				if (entity.getEntityType().hasSound(soundName))
					entity.getEntityType().getSound(soundName).play(volume, 1, pan);
			}
		}
	}
	
	public void playSound (String sound) {
		Vector2 entityPos = entity.getCenterPointTile();
		float volume = SoundCalculator.calculateVolume((int) entityPos.x, (int) entityPos.y);
		float pan = SoundCalculator.calculatePan((int) entityPos.x);
		
		if (entity.getEntityType().hasSound(sound))
			entity.getEntityType().getSound(sound).play(volume, 1, pan);
	}
	
	public void setContinuousSound (String sound) {
		if (!sound.equals(continuousName)) {//Sound changed
			if (sound.equals("")) {
				continuousSound.redefine(null);
				continuousName = "";
			} else {
				if (entity.getEntityType().hasSound(sound)) {
					continuousSound.redefine(entity.getEntityType().getSound(sound));
					
					Vector2 entityPos = entity.getCenterPointTile();
					float volume = SoundCalculator.calculateVolume((int) entityPos.x, (int) entityPos.y);
					float pan = SoundCalculator.calculatePan((int) entityPos.x);
					continuousSound.loop(volume, 1, pan);
					continuousName = sound;
				}
			}
		}
	}
	
	/**
	 * Call this method if either the player or this entity has moved.
	 * Use this sparingly since its calculations are costly.
	 */
	public void moved () {
		Vector2 entityPos = entity.getCenterPointTile();
		float volume = SoundCalculator.calculateVolume((int) entityPos.x, (int) entityPos.y);
		float pan = SoundCalculator.calculatePan((int) entityPos.x);
		continuousSound.setPan(pan, volume);
	}
	
	public void dispose () {
		audioManagers.remove(this);
	}
	
}
