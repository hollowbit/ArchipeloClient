package net.hollowbit.archipelo.world.elements;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.world.MapElement;
import net.hollowbit.archipeloshared.ElementData;

public class PlainElement extends MapElement {

	TextureRegion image;
	
	public PlainElement (ElementData elementData) {
		super(elementData.id, elementData.name, elementData.width, elementData.height, elementData.collisionTable, elementData.flipX, elementData.flipY, elementData.rotation, elementData.offsetX, elementData.offsetY);
		this.image = new TextureRegion(ArchipeloClient.getGame().getAssetManager().getTexture("elements"), elementData.x * ArchipeloClient.TILE_SIZE, elementData.y * ArchipeloClient.TILE_SIZE, elementData.width * ArchipeloClient.TILE_SIZE, elementData.height * ArchipeloClient.TILE_SIZE);
	}

	@Override
	public void draw(SpriteBatch batch, float x, float y) {
		batch.draw(image, getDrawX(x), getDrawY(y), getOriginX(), getOriginY(), getDrawWidth(), getDrawHeight(), 1, 1, rotation * 90);
	}

}
