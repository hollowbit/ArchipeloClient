package net.hollowbit.archipelo.world.tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.world.Tile;
import net.hollowbit.archipeloshared.TileData;

public class PlainTile extends Tile {
	
	private TextureRegion image;
	
	public PlainTile (TileData tileData) {
		super(tileData.id, tileData.name, tileData.speedMultiplier, tileData.swimmable, tileData.collisionTable, tileData.flipX, tileData.flipY, tileData.rotation, tileData.footstepSound);
		this.image = ArchipeloClient.getGame().getAssetManager().getTextureMap("tiles")[tileData.y][tileData.x];
	}
	
	public void draw (SpriteBatch batch, float x, float y) {
		batch.draw(image, getDrawX(x), getDrawY(y), getOriginX(), getOriginY(), getDrawWidth(), getDrawHeight(), 1, 1, rotation * 90);
	}
	
}
