package net.hollowbit.archipelo.screen.screens.gamescreen.windows;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.screen.screens.gamescreen.ChatManager;
import net.hollowbit.archipelo.tools.FontManager.Fonts;
import net.hollowbit.archipelo.tools.FontManager.Sizes;
import net.hollowbit.archipelo.tools.QuickUi;

public class ChatMessage {
	
	public static float WIDTH = (ArchipeloClient.IS_MOBILE ? 250 : 525);
	
	private static final int START_Y = 50;
	private static final int MOBILE_BUMP = 250;
	private static final float FADE_TIME = 1;//seconds
	private static final float LIFE_TIME = 8;//seconds
	private static final float BOX_TRANSPARENCY = 0.4f;
	private static final float SPACING = 0;
	private static final float MAX_HEIGHT = 600;
	private static final int PADDING = 2;
	public static final int X_POSITION = PADDING;
	
	String message;
	Player sender;
	float timer;
	float y;
	float height;
	boolean show = true;
	boolean remove = false;
	
	public ChatMessage (String prefix, String message, String sender, ChatManager manager) {
		this.message = message;
		if (sender.equals("server"))//Only process message if sent by server
			this.message = QuickUi.processMessageString(this.message);
		else
			this.message = QuickUi.stripAllNewlinesAndTabs(this.message);
		prefix = QuickUi.processMessageString(prefix);
		
		this.message = prefix + " " +  this.message;
		
		if (sender.startsWith("@"))
			this.sender = ArchipeloClient.getGame().getWorld().getPlayer(sender.substring(1, sender.length() - 1));
		y = START_Y + (ArchipeloClient.IS_MOBILE ? MOBILE_BUMP : 0);
		GlyphLayout layout = new GlyphLayout(ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.VERY_SMALL), this.message, Color.WHITE, WIDTH, Align.bottomLeft, true);
		this.height = layout.height;
		
		for (ChatMessage chatMessage : manager.getChatMessages()) {
			chatMessage.addToY(height + SPACING + PADDING * 2);
		}
	}
	
	public void update (float deltatime) {
		timer += deltatime;
		
		if (timer >= LIFE_TIME)
			show = false;
	}
	
	public void render (SpriteBatch batch, boolean noTransparency) {
		//Draw box
		if (timer >= LIFE_TIME - FADE_TIME && !noTransparency)
			batch.setColor(0, 0, 0, BOX_TRANSPARENCY - (BOX_TRANSPARENCY / FADE_TIME) * (timer - (LIFE_TIME - FADE_TIME)));
		else 
			batch.setColor(0, 0, 0, BOX_TRANSPARENCY);
		batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("blank"), X_POSITION - PADDING, y - PADDING, WIDTH + PADDING * 2, height + PADDING * 2);
		
		//Draw message
		BitmapFont font = ArchipeloClient.getGame().getFontManager().getFont(Fonts.PIXELATED, Sizes.VERY_SMALL);
		if (timer >= LIFE_TIME - FADE_TIME && !noTransparency)
			font.setColor(1, 1, 1, 1 - (1 / FADE_TIME) * (timer - (LIFE_TIME - FADE_TIME)));
		else
			font.setColor(1, 1, 1, 1);
		
		BitmapFontCache cache = font.getCache();
		cache.clear();
		cache.addText(message, X_POSITION, y + height, WIDTH, Align.bottomLeft, true);
		if (timer >= LIFE_TIME - FADE_TIME && !noTransparency)
			cache.setAlphas(1 - (1 / FADE_TIME) * (timer - (LIFE_TIME - FADE_TIME)));
		cache.draw(batch);
		
		font.setColor(1, 1, 1, 1);
		batch.setColor(1, 1, 1, 1);
	}
	
	public void addToY (float amount) {
		y += amount;
		if (y + height > MAX_HEIGHT)
			remove = true;
	}
	
	public boolean isShowing () {
		return show;
	}
	
	public boolean removeFromList () {
		return remove;
	}
	
	@Override
	public String toString() {
		return message;
	}

}
