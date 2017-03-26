package net.hollowbit.archipelo.audio;

import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.tools.StaticTools;

public class SoundCalculator {
	
	public static final int HEARING_DISTANCE = 50;//tiles
	
	public static float calculateDistanceBetweenEntity(int x, int y) {
		Vector2 playerPos = ArchipeloClient.getGame().getWorld().getPlayer().getCenterPointTile();
		int xDif = (int) (playerPos.x - x);
		int yDif = (int) (playerPos.y - y);
		return StaticTools.imperfectSqrt((xDif * xDif) + (yDif * yDif));
	}
	
	/**
	 * Calculates the volume to play a sound given a relative location to the player.
	 * @param tileX
	 * @param tileY
	 * @return
	 */
	public static float calculateVolume(int tileX, int tileY) {
		float distance = calculateDistanceBetweenEntity(tileX, tileY);
		if (distance > HEARING_DISTANCE)
			return 0;
		
		if (distance < 2)
			return 1;
		
		return 1 / (distance / 2);
	}
	
	/**
	 * Calculate the pan to play a sound given a relative location to the player.
	 * @param tileX
	 * @param tileY
	 * @return
	 */
	public static float calculatePan(int tileX, int tileY) {
		int xDif = (int) (tileX - ArchipeloClient.getGame().getWorld().getPlayer().getCenterPointTile().x);
		
		if (xDif < -HEARING_DISTANCE)
			return -1;
		if (xDif > HEARING_DISTANCE)
			return 1;
		
		return xDif / HEARING_DISTANCE;
	}
	
}
