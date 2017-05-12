package net.hollowbit.archipelo.tools;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetManager {
	
	public static final TextureFilter TEXTURE_FILTER = TextureFilter.Linear;
	
	private HashMap<String, Texture> textureMap;
	private HashMap<String, TextureRegion[][]> textureRegionMap;
	private HashMap<String, Animation> animationMap;
	
	public AssetManager () {
		textureMap = new HashMap<String, Texture>();
		textureRegionMap = new HashMap<String, TextureRegion[][]>();
		animationMap = new HashMap<String, Animation>();
	}
	
	/**
	 * Adds a texture without a filter
	 * @param key
	 * @param fileLocation
	 */
	public void putTexture (String key, String fileLocation) {
		putTexture(key, fileLocation, false);
	}
	
	/**
	 * Adds a texture with possibility of a filter
	 * @param key
	 * @param fileLocation
	 * @param applyFilter
	 */
	public void putTexture (String key, String fileLocation, boolean applyFilter) {
		Texture texture = new Texture(fileLocation);
		if (applyFilter)
			applyFilter(texture);
		textureMap.put(key, texture);
	}
	
	public Texture getTexture (String key) {
		return textureMap.get(key);
	}
	
	/**
	 * Adds a texture map without a filter
	 * @param key
	 * @param fileLocation
	 * @param tileWidth
	 * @param tileHeight
	 */
	public void putTextureMap (String key, String fileLocation, int tileWidth, int tileHeight) {
		putTextureMap(key, fileLocation, tileWidth, tileHeight, false);
	}
	
	/**
	 * Adds a texture map with possibility of filter
	 * @param key
	 * @param fileLocation
	 * @param tileWidth
	 * @param tileHeight
	 * @param applyFilter
	 */
	public void putTextureMap (String key, String fileLocation, int tileWidth, int tileHeight, boolean applyFilter) {
		TextureRegion[][] textureMap = TextureRegion.split(new Texture(fileLocation), tileWidth, tileHeight);
		for (TextureRegion[] trArray : textureMap) {
			for (TextureRegion tr : trArray) {
				if (applyFilter)
					applyFilter(tr);
				fixBleeding(tr);
			}
		}
		textureRegionMap.put(key, textureMap);
	}
	
	/**
	 * Returns entire texture map
	 * @param key
	 * @return
	 */
	public TextureRegion[][] getTextureMap (String key) {
		return textureRegionMap.get(key);
	}
	
	/**
	 * Adds an animation without a filter
	 * @param key
	 * @param animation
	 */
	public void putAnimation (String key, Animation animation) {
		putAnimation(key, animation, false);
	}
	
	/**
	 * Adds animation with possibility of filter
	 * @param key
	 * @param animation
	 * @param filter
	 */
	public void putAnimation (String key, Animation animation, boolean filter) {
		for (TextureRegion tr : animation.getKeyFrames()) {
			if (filter)
				applyFilter(tr);
			fixBleeding(tr);
		}
		animationMap.put(key, animation);
	}
	
	/**
	 * Get specific from of an animation with no looping
	 * @param key
	 * @param stateTime
	 * @return
	 */
	public TextureRegion getAnimationKeyFrame (String key, float stateTime) {
		return getAnimationKeyFrame(key, stateTime, false);
	}
	
	/**
	 * Get specific from of an animation with possibility of looping
	 * @param key
	 * @param stateTime
	 * @param looping
	 * @return
	 */
	public TextureRegion getAnimationKeyFrame (String key, float stateTime, boolean looping) {
		return animationMap.get(key).getKeyFrame(stateTime, looping);
	}
	
	/**
	 * Applies a linear filter to this texture region
	 * @param tr
	 */
	private void applyFilter (TextureRegion tr) {
		applyFilter(tr.getTexture());
	}
	
	/**
	 * Applies a linear filter to this texture
	 * @param texture
	 */
	private void applyFilter (Texture texture) {
		texture.setFilter(TEXTURE_FILTER, TEXTURE_FILTER);
	}
	
	/**
	 * Removes texture bleeding from texture regions. COMMENTED OUT! NOT WORKING WELL. CAUSES RIPPLE EFFECT
	 * 
	 * Credit to awilki01 from the LibGDX Forums. http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=16368
	 * @param tr
	 */
	public static TextureRegion fixBleeding (TextureRegion tr) {
	    /*float fix = 0.01f;
	    float x = tr.getRegionX();
	    float y = tr.getRegionY();
	    float width = tr.getRegionWidth();
	    float height = tr.getRegionHeight();
	    float invTexWidth = 1f / tr.getTexture().getWidth();
	    float invTexHeight = 1f / tr.getTexture().getHeight();
	    tr.setRegion((x + fix) * invTexWidth, (y + fix) * invTexHeight, (x + width - fix) * invTexWidth, (y + height - fix) * invTexHeight);*/
	    return tr;
	}
	
	/**
	 * Removes texture bleeding from spritesheet texture regions
	 * @param tr
	 * @return
	 */
	public static TextureRegion[][] fixBleedingSpriteSheet (TextureRegion[][] tr) {
		for (int r = 0; r < tr.length; r++) {
			for (int c = 0; c < tr[0].length; c++) {
				fixBleeding(tr[r][c]);
			}
		}
		return tr;
	}
	
}
