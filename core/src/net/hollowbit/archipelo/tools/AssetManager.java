package net.hollowbit.archipelo.tools;

import java.util.HashMap;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetManager {
	
	HashMap<String, Texture> textureMap;
	HashMap<String, TextureRegion[][]> textureRegionMap;
	HashMap<String, Animation> animationMap;
	HashMap<String, Sound> soundMap;
	HashMap<String, Music> musicMap;
	
	public AssetManager () {
		textureMap = new HashMap<String, Texture>();
		textureRegionMap = new HashMap<String, TextureRegion[][]>();
		animationMap = new HashMap<String, Animation>();
		soundMap = new HashMap<String, Sound>();
		musicMap = new HashMap<String, Music>();
	}
	
	public void putTexture (String key, Texture texture) {
		textureMap.put(key, texture);
	}
	
	public Texture getTexture (String key) {
		return textureMap.get(key);
	}
	
	public void putTextureMap (String key, Texture texture, int tileWidth, int tileHeight) {
		TextureRegion[][] textureMap = TextureRegion.split(texture, tileWidth, tileHeight);
		for (int r = 0; r < textureMap.length; r++) {
			for (int c = 0; c < textureMap[0].length; c++) {
				fixBleeding(textureMap[r][c]);
			}
		}
		textureRegionMap.put(key, textureMap);
	}
	
	public TextureRegion[][] getTextureMap (String key) {
		return textureRegionMap.get(key);
	}
	
	public void putAnimation (String key, Animation animation) {
		animationMap.put(key, animation);
	}
	
	public TextureRegion getAnimationKeyFrame (String key, float stateTime) {
		return getAnimationKeyFrame(key, stateTime, false);
	}
	
	public TextureRegion getAnimationKeyFrame (String key, float stateTime, boolean looping) {
		return animationMap.get(key).getKeyFrame(stateTime, looping);
	}
	
	public void putSound (String key, Sound sound) {
		soundMap.put(key, sound);
	}
	
	public Sound getSound (String key) {
		return soundMap.get(key);
	}
	
	public void putMusic (String key, Music music) {
		musicMap.put(key, music);
	}
	
	public Music getMusic (String key) {
		return musicMap.get(key);
	}
	
	//Credit to awilki01 from the LibGDX Forums. http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=16368
	private void fixBleeding (TextureRegion region) {
	    float fix = 0.01f;
	    float x = region.getRegionX();
	    float y = region.getRegionY();
	    float width = region.getRegionWidth();
	    float height = region.getRegionHeight();
	    float invTexWidth = 1f / region.getTexture().getWidth();
	    float invTexHeight = 1f / region.getTexture().getHeight();
	    region.setRegion((x + fix) * invTexWidth, (y + fix) * invTexHeight, (x + width - fix) * invTexWidth, (y + height - fix) * invTexHeight);
	}
	
}
