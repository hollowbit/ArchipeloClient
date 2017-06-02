package net.hollowbit.archipelo.items;

import java.io.IOException;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.tools.AssetManager;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.ItemUseAnimationData;
import net.hollowbit.archipeloshared.rangeindexer.RangeMap;

public class ItemUseAnimation {
	
	public static final ItemUseAnimation DEFAULT = new ItemUseAnimation();
	
	private boolean canEndEarly;
	private boolean thrust;
	private boolean stick;
	private Animation[][] runtimeAnimation = null;
	private float totalRuntime;
	private RangeMap<Float, TextureRegion>[][] frames = null;
	private TextureRegion[][] lastFrames = null;
	
	private ItemUseAnimation() {
		this.canEndEarly = true;
		this.stick = true;
	}
	
	public ItemUseAnimation(ItemType item, int useAnim, ItemUseAnimationData data) throws IllegalItemUseAnimationDataException {
		this.stick = data.stick;
		this.thrust = data.thrust;
		this.canEndEarly = data.canEndEarly;
		this.lastFrames = new TextureRegion[Direction.TOTAL][item.numOfStyles];
		if (data.timings == null && data.runtime > 0)
			createRuntimeAnimation(item, useAnim, data);
		else if (data.runtime <= 0 && data.timings != null)
			createFrameByFrameAnimation(item, useAnim, data);
		else
			throw new IllegalItemUseAnimationDataException(item.id);
	}
	
	private void createRuntimeAnimation(ItemType item, int useAnim, ItemUseAnimationData data) {
		this.totalRuntime = data.runtime;
		runtimeAnimation = new Animation[Direction.TOTAL][item.numOfStyles];
		for (int style = 0; style < item.numOfStyles; style++) {
			TextureRegion[][] useSheet = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(new Texture("items/" + item.id + "/use_" + style + "_" + useAnim + ".png"), ArchipeloClient.PLAYER_SIZE, ArchipeloClient.PLAYER_SIZE));
			for (int direction = 0; direction < Direction.TOTAL; direction++) {
				runtimeAnimation[direction][style] = new Animation(data.runtime / useSheet[direction].length, useSheet[direction]);
				lastFrames[direction][style] = useSheet[direction][useSheet[direction].length - 1];
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void createFrameByFrameAnimation(ItemType item, int useAnim, ItemUseAnimationData data) {
		frames = new RangeMap[Direction.TOTAL][item.numOfStyles];
		this.totalRuntime = 0;
		for (float timing : data.timings)
			totalRuntime += timing;
		
		for (int style = 0; style < item.numOfStyles; style++) {
			TextureRegion[][] useSheet = AssetManager.fixBleedingSpriteSheet(TextureRegion.split(new Texture("items/" + item.id + "/use_" + style + "_" + useAnim + ".png"), ArchipeloClient.PLAYER_SIZE, ArchipeloClient.PLAYER_SIZE));
			for (int direction = 0; direction < Direction.TOTAL; direction++) {
				frames[direction][style] = new RangeMap<Float, TextureRegion>(0f);
				lastFrames[direction][style] = useSheet[direction][useSheet[direction].length - 1];
				
				//Define timing ranges for this animation
				float limit = 0;
				for (int timing = 0; timing < data.timings.length; timing++) {
					limit += data.timings[timing];
					frames[direction][style].add(limit, useSheet[direction][timing]);
				}
			}
		}
	}
	
	public TextureRegion getFrame(float statetime, Direction direction, int style) {
		if (statetime >= totalRuntime)
			return lastFrames[direction.ordinal()][style];
		
		if (isRuntime()) {
			return runtimeAnimation[direction.ordinal()][style].getKeyFrame(statetime);
		} else {
			return frames[direction.ordinal()][style].getValue(statetime);
		}
	}
	
	/**
	 * Returns whether the animation sticks on the last frame until the attack button is released.
	 * @return
	 */
	public boolean doesStick() {
		return stick;
	}
	
	public boolean usesThrust() {
		return thrust;
	}
	
	public boolean canEndEarly() {
		return canEndEarly;
	}
	
	public boolean isRuntime() {
		return runtimeAnimation != null;
	}
	
	public boolean isFrameByFrame() {
		return frames != null;
	}
	
	public float getTotalRuntime() {
		return totalRuntime;
	}
	
	public class IllegalItemUseAnimationDataException extends IOException {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public IllegalItemUseAnimationDataException(String itemId) {
			super("Invalid item use animation data found for " + itemId + ". Must either use runtime or timings.");
		}
		
	}
	
}
