package net.hollowbit.archipelo.world.tiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.world.Tile;
import net.hollowbit.archipeloshared.TileData;

public class AnimatedTile extends Tile {

	Animation animation;
	
	public AnimatedTile (TileData tileData) {
		super(tileData.id, tileData.name, tileData.speedMultiplier, tileData.swimmable, tileData.collisionTable, tileData.flipX, tileData.flipY, tileData.rotation, tileData.footstepSound);
		
		//Get animation frames
		TextureRegion[] frames = new TextureRegion[tileData.animationFrames];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = ArchipeloClient.getGame().getAssetManager().getTextureMap("tiles")[tileData.y][tileData.x + i];
		}
		this.animation = new Animation(tileData.animationTime, frames);
	}

	@Override
	public void draw(SpriteBatch batch, float x, float y) {
		batch.draw(animation.getKeyFrame(ArchipeloClient.STATE_TIME, true), getDrawX(x), getDrawY(y), getOriginX(), getOriginY(), getDrawWidth(), getDrawHeight(), 1, 1, rotation * 90);
	}

}
