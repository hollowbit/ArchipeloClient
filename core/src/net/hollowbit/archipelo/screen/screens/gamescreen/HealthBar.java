package net.hollowbit.archipelo.screen.screens.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.tools.StaticTools;
import net.hollowbit.archipelo.world.World;

public class HealthBar {
	
	private static final int OVERLAY_WIDTH = 173;
	private static final int OVERLAY_HEIGHT = 68;
	private static final int OVERLAY_Y = 20;
	private static final int HEALTH_WIDTH = 163;
	private static final int HEALTH_HEIGHT = 24;
	private static final int HEALTH_Y = 22;
	private static final int MANA_WIDTH = 141;
	private static final int MANA_HEIGHT = 6;
	private static final int MANA_Y = 51;
	private static final int EXP_WIDTH = 161;
	private static final int EXP_HEIGHT = 6;
	private static final int EXP_Y = 11;
	
	private World world;
	
	public HealthBar (World world) {
		this.world = world;
	}
	
	public void render(SpriteBatch batch) {
		float mobileFactor = (ArchipeloClient.IS_MOBILE ? 2 : 1);//Make statusbar 2x as big on mobile
		if (world.getPlayer() != null) {
			float healthFraction = StaticTools.singleDimentionLerpFraction(0, world.getPlayer().getMaxHealth(), world.getPlayer().getHealth());
			float manaFraction = 0.8f;
			float expFraction = 0.3f;
			batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("status-bar-background"), Gdx.graphics.getWidth() / 2 - OVERLAY_WIDTH * mobileFactor / 2, OVERLAY_Y, OVERLAY_WIDTH * mobileFactor, OVERLAY_HEIGHT * mobileFactor);
			
			batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("health-bar"), Gdx.graphics.getWidth() / 2 - HEALTH_WIDTH * mobileFactor / 2, OVERLAY_Y + HEALTH_Y * mobileFactor, healthFraction * HEALTH_WIDTH * mobileFactor, HEALTH_HEIGHT * mobileFactor);
			batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("mana-bar"), Gdx.graphics.getWidth() / 2 - MANA_WIDTH * mobileFactor / 2, OVERLAY_Y + MANA_Y * mobileFactor, manaFraction * MANA_WIDTH * mobileFactor, MANA_HEIGHT * mobileFactor);
			batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("exp-bar"), Gdx.graphics.getWidth() / 2 - EXP_WIDTH * mobileFactor / 2, OVERLAY_Y + EXP_Y * mobileFactor, expFraction * EXP_WIDTH * mobileFactor, EXP_HEIGHT * mobileFactor);

			batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("status-bar-overlay"), Gdx.graphics.getWidth() / 2 - OVERLAY_WIDTH * mobileFactor / 2, OVERLAY_Y, OVERLAY_WIDTH * mobileFactor, OVERLAY_HEIGHT * mobileFactor);
		}
	}
	
}
