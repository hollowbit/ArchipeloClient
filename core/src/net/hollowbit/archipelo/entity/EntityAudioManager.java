package net.hollowbit.archipelo.entity;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipelo.ArchipeloClient;
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
	
	private String footstepName;
	private Soundlet footstepSound;
	private Entity entity;
	
	public EntityAudioManager (Entity entity, String sound) {
		this.entity = entity;
		this.footstepSound = new Soundlet(entity.getEntityType().getSound(sound));
		this.footstepName = sound;
		audioManagers.add(this);
	}
	
	/**
	 * Call on entity interpolation.
	 * @param snapshotTo
	 */
	public void change (EntitySnapshot snapshotTo) {
		setFootstepSound(snapshotTo.footSound, snapshotTo.footPitch);
	}
	
	/**
	 * Call when a new changes snapshot is available.
	 * @param snapshot
	 */
	public void handleChanges(EntitySnapshot snapshot) {
		if (snapshot.sounds != null && !snapshot.sounds.isEmpty()) {
			Vector2 entityPos = entity.getCenterPointTile();
			float volume = SoundCalculator.calculateVolume((int) entityPos.x, (int) entityPos.y);
			float pan = SoundCalculator.calculatePan((int) entityPos.x, (int) entityPos.y);
			
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
		float pan = SoundCalculator.calculatePan((int) entityPos.x, (int) entityPos.y);
		
		if (entity.getEntityType().hasSound(sound))
			entity.getEntityType().getSound(sound).play(volume, 1, pan);
	}
	
	public void stopFootstepSound() {
		this.setFootstepSound("", 1);
	}
	
	public void setFootstepSound (String sound, float pitch) {
		if (!sound.equals(footstepName)) {//Sound changed
			if (sound.equals("")) {
				footstepSound.redefine(null);
				footstepName = "";
			} else {
				if (entity.getEntityType().hasFootstepSound()) {
					footstepSound.redefine(ArchipeloClient.getGame().getSoundManager().getSound("footsteps/" + sound));
					
					Vector2 entityPos = entity.getFeetTile();
					float volume = SoundCalculator.calculateVolume((int) entityPos.x, (int) entityPos.y);
					float pan = SoundCalculator.calculatePan((int) entityPos.x, (int) entityPos.y);
					footstepSound.loop(volume, pitch, pan);
					footstepName = sound;
				}
			}
		}
		footstepSound.setPitch(pitch);
	}
	
	/**
	 * Call this method if either the player or this entity has moved.
	 * Use this sparingly since its calculations are costly.
	 */
	public void moved () {
		Vector2 entityPos = entity.getFeetTile();
		float volume = SoundCalculator.calculateVolume((int) entityPos.x, (int) entityPos.y);
		float pan = SoundCalculator.calculatePan((int) entityPos.x, (int) entityPos.y);
		footstepSound.setPan(pan, volume);
	}
	
	public void dispose () {
		footstepSound.stop();
		audioManagers.remove(this);
	}
	
}
