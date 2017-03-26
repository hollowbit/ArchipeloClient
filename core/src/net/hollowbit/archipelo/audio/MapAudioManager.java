package net.hollowbit.archipelo.audio;

import com.badlogic.gdx.audio.Sound;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipelo.world.MapSnapshot;
import net.hollowbit.archipeloshared.SoundPlayData;

public class MapAudioManager {
	
	@SuppressWarnings("unused")
	private Map map;

	public MapAudioManager(Map map) {
		this.map = map;
	}
	
	public void applyChangesSnapshot (MapSnapshot snapshot) {
		for (SoundPlayData data : snapshot.sounds) {
			Sound sound = ArchipeloClient.getGame().getSoundManager().getSound(data.path);
			if (sound != null) {
				float volume = SoundCalculator.calculateVolume(data.x, data.y);
				float pan = SoundCalculator.calculatePan(data.x, data.y);
				sound.play(volume, 1, pan);
			}
		}
	}
	
}
