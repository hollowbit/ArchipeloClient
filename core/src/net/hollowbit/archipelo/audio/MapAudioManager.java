package net.hollowbit.archipelo.audio;

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
			if (ArchipeloClient.getGame().getSoundManager().hasSound(data.path)) {
				float volume = SoundCalculator.calculateVolume(data.x, data.y);
				float pan = SoundCalculator.calculatePan(data.x, data.y);
				ArchipeloClient.getGame().getSoundManager().play(data.path, volume, 1, pan);
			}
		}
	}
	
}
