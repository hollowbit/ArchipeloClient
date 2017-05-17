package net.hollowbit.archipelo.screen.screens.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.tools.StaticTools;
import net.hollowbit.archipelo.world.World;

public class HealthBar {
	
	public static final int HEALTH_BAR_Y = 100;
	public static final int HEALTH_BAR_WIDTH = 200;
	
	public static final int OVERLAY_Y = 135;
	public static final int OVERLAY_HEIGHT = 38;
	public static final int OVERLAY_WIDTH = 161;
	
	private World world;
	
	public HealthBar (World world) {
		this.world = world;
	}
	
	public void render(SpriteBatch batch) {
		if (world.getPlayer() != null) {
			float fraction = StaticTools.singleDimentionLerpFraction(0, world.getPlayer().getMaxHealth(), world.getPlayer().getHealth());
			batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("health-bar"), Gdx.graphics.getWidth() / 2 - HEALTH_BAR_WIDTH / 2, HEALTH_BAR_Y);
			batch.setColor(1, 0, 0, 1);
			batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("blank"), Gdx.graphics.getWidth() / 2 - OVERLAY_WIDTH / 2, OVERLAY_Y, fraction * OVERLAY_WIDTH, OVERLAY_HEIGHT);
			batch.setColor(1, 1, 1, 1);
		}
	}
	
}
