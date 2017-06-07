package net.hollowbit.archipelo.entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.EntityAnimationManager.EntityAnimationObject;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.tools.Location;
import net.hollowbit.archipelo.tools.StaticTools;
import net.hollowbit.archipelo.tools.ShaderManager.ShaderType;
import net.hollowbit.archipelo.tools.rendering.RenderableGameWorldObject;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.Point;

public abstract class Entity implements RenderableGameWorldObject {
	
	private static final float DAMAGE_FLASH_DURATION = 0.2f;
	private static final int HEALTHBAR_RENDER_DISTANCE = ArchipeloClient.TILE_SIZE * 6;
	
	protected String name;
	protected Location location;
	protected EntityType entityType;
	protected int style;
	protected EntityAnimationManager animationManager;
	protected ArrayList<EntityComponent> components;
	protected boolean overrideControls = false;
	protected EntityAudioManager audioManager;
	protected float health;
	protected float flashTimer = 0;
	protected boolean damageFlash;
	
	public Entity () {
		components = new ArrayList<EntityComponent>();
	}
	
	public void create (EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		this.name = fullSnapshot.name;
		this.entityType = entityType;
		this.style = fullSnapshot.getInt("style", 0);
		
		this.health = fullSnapshot.getFloat("health", entityType.getMaxHealth());
		
		Point pos = fullSnapshot.getObject("pos", new Point(), Point.class);
		this.location = new Location(map, new Vector2(pos.x, pos.y), Direction.values()[fullSnapshot.getInt("direction", 0)]);
		animationManager = new EntityAnimationManager(this, fullSnapshot);
		audioManager = new EntityAudioManager(this, fullSnapshot.footSound);
	}
	
	/**
	 * Begin rendering entity. Will handle cancelled renders and animations.
	 * @param batch
	 */
	@Override
	public void renderObject (SpriteBatch batch) {
		//CollisionRect collRect = this.getCollisionRect();
		//batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("invalid"), collRect.x, collRect.y, collRect.width, collRect.height);
		
		//Pick shader to use when rendering based on flash
		ArchipeloClient.getGame().getShaderManager().save();
		if (flashTimer > 0 && flashTimer <= DAMAGE_FLASH_DURATION / 2) {//White
			ArchipeloClient.getGame().getShaderManager().applyShader(batch, ShaderType.WHITE);
		} else if (flashTimer > 0){ //Color
			if (damageFlash)//Red
				ArchipeloClient.getGame().getShaderManager().applyShader(batch, ShaderType.RED);
			else//Green
				ArchipeloClient.getGame().getShaderManager().applyShader(batch, ShaderType.GREEN);
		}
		
		//Render components and check if they cancelled further rendering
		boolean renderCancelled = false;
		for (EntityComponent component : components
				) {
			if (component.render(batch, renderCancelled))
				renderCancelled = true;
		}
		
		if (!renderCancelled) {
			animationManager.render(batch);
			render(batch);
		}
		
		renderCancelled = false;
		for (EntityComponent component : components) {
			if (component.renderAfter(batch, renderCancelled))
				renderCancelled = true;
		}
		
		ArchipeloClient.getGame().getShaderManager().restore(batch);
		
		this.renderUninterruptable(batch);
	}
	
	protected void render (SpriteBatch batch) {}
	
	/**
	 * Will always render. Rendered on top of an entity.
	 * @param batch
	 */
	private void renderUninterruptable (SpriteBatch batch) {
		if (this.getEntityType().showHealthBar() && !ArchipeloClient.CINEMATIC_MODE) {//If show healthbar for entity
			float dX = this.getFootX() - this.getLocation().getWorld().getPlayer().getFootX();
			float dY = this.getFootY() - this.getLocation().getWorld().getPlayer().getFootY();
			
			//Only render healthbar if within render distance
			if ((dX * dX) + (dY * dY) <= HEALTHBAR_RENDER_DISTANCE * HEALTHBAR_RENDER_DISTANCE) {
				float fraction = StaticTools.singleDimentionLerpFraction(0, getMaxHealth(), health);
				batch.setColor(1, 0, 0, 1);
				batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("blank"), location.getX(), location.getY() + getViewRect().height + 2, getViewRect().width * fraction, 1);
				batch.setColor(1, 1, 1, 1);
			}
		}
	}
	
	/**
	 * Tick the entity forward in time
	 * @param deltaTime
	 */
	public void update (float deltaTime) {
		animationManager.update(deltaTime);
		for (EntityComponent component : components)
			component.update(deltaTime);
		
		//Update and clamp flashTimer
		if (this.flashTimer > 0)
			this.flashTimer -= deltaTime;
		if (flashTimer < 0)
			flashTimer = 0;
	}
	
	/**
	 * Interpolate the movement or other for this entity.
	 * @param timeStamp
	 * @param snapshotFrom
	 * @param snapshotTo
	 * @param fraction
	 */
	public void interpolate (long timeStamp, EntitySnapshot snapshotFrom, EntitySnapshot snapshotTo, float fraction) {
		audioManager.change(snapshotTo);
		
		for (EntityComponent component : components)
			component.interpolate(timeStamp, snapshotFrom, snapshotTo, fraction);
	}
	
	/**
	 * Definite and optimized way of knowing if an entity is a player.
	 * @return
	 */
	public boolean isPlayer () {
		return false;
	}
	
	/**
	 * Definite and optimized way of knowing if an entity is living/can move aka instance of {@link LivingEntity} class.
	 * @return
	 */
	public boolean isAlive () {
		return false;
	}
	
	/**
	 * Called when the entity is loaded
	 */
	public void load () {
		for (EntityComponent component : components)
			component.load();
	}
	
	/**
	 * Called when the entity is unloaded
	 */
	public void unload () {
		audioManager.dispose();
		for (EntityComponent component : components)
			component.unload();
	}
	
	/**
	 * Called whenever this entity has moved
	 */
	public void moved() {
		audioManager.moved();
	}
	
	public void teleport(float x, float y, Direction direction) {
		this.location.set(x, y, direction);
		this.moved();
	}
	
	public void applyChangesSnapshot (EntitySnapshot snapshot) {
		style = snapshot.getInt("style", style);
		health = snapshot.getFloat("health", health);
		
		//Do flash animation if property is there
		if (snapshot.doesPropertyExist("flash") && this.flashTimer == 0) {
			this.damageFlash = snapshot.getBoolean("flash", damageFlash);
			this.flashTimer = DAMAGE_FLASH_DURATION;
		}
		
		if (!overrideControls) {
			animationManager.change(snapshot);
			audioManager.handleChanges(snapshot);
		}
		
		if (!overrideControls)
			location.direction = Direction.values()[snapshot.getInt("direction", location.direction.ordinal())];
		for (EntityComponent component : components)
			component.applyChangesSnapshot(snapshot);
	}
	
	public String getName () {
		return name;
	}
	
	public int getStyle () {
		return style;
	}
	
	public int getMaxHealth () {
		return entityType.getMaxHealth();
	}
	
	/**
	 * May not be accurate if health of this entity is hidden.
	 * @return
	 */
	public float getHealth() {
		return health;
	}
	
	public Direction getDirection () {
		return location.direction;
	}
	
	public Location getLocation () {
		return location;
	}
	
	public void setLocation (Location location) {
		this.location = location;
	}
	
	public EntityType getEntityType () {
		return entityType;
	}
	
	public EntityAnimationManager getAnimationManager () {
		return animationManager;
	}

	public CollisionRect[] getCollisionRects () {
		return entityType.getCollisionRects(location.getX(), location.getY());
	}
	
	public CollisionRect[] getCollisionRects (Vector2 potentialPosition) {
		return entityType.getCollisionRects(potentialPosition.x, potentialPosition.y);
	}
	
	@Override
	public CollisionRect getViewRect () {
		return entityType.getViewRect(location.getX(), location.getY());
	}
	
	public float getRenderY () {
		return entityType.getDrawOrderY(location.getY());
	}
	
	public EntityAudioManager getAudioManager() {
		return audioManager;
	}
	
	public float getFootX () {
		return location.getX() + entityType.getFootstepOffsetX();
	}
	
	public float getFootY () {
		return location.getY() + entityType.getFootstepOffsetY();
	}
	
	/**
	 * Returns the tile which this entities feet is stepping on
	 * @return
	 */
	public Vector2 getFeetTile () {
		return new Vector2(getFootX() / ArchipeloClient.TILE_SIZE, getFootY() / ArchipeloClient.TILE_SIZE);
	}
	
	/**
	 * Called when the current animation is complete. Only used by entities where animations are overridden, like the CurrentPlayer.
	 * @param animationId
	 * @return
	 */
	public EntityAnimationObject animationCompleted (String animationId) {
		return null;
	}
	
	/**
	 * This is used by certain entities which don't always want a collision rect to be hard.
	 * Ex: Like a locked door that becomes unlocked for some players.
	 * @param player
	 * @param rectName
	 * @return
	 */
	public boolean ignoreHardnessOfCollisionRects (Player player, String rectName) {
		boolean ignoreHardness = false;
		for (EntityComponent component : components) {
			if (component.ignoreHardnessOfCollisionRects(player, rectName))
				ignoreHardness = true;
		}
		return ignoreHardness;
	}
	
	/**
	 * Exact center point of the entities view rect.
	 * @return
	 */
	public Vector2 getCenterPoint () {
		CollisionRect viewRect = entityType.getViewRect(location.getX(), location.getY());
		return new Vector2(location.getX() + viewRect.width / 2, location.getY() + viewRect.height / 2);
	}
	
	public Vector2 getCenterPointTile () {
		Vector2 centerPoint = getCenterPoint();
		centerPoint.x /= ArchipeloClient.TILE_SIZE;
		centerPoint.y /= ArchipeloClient.TILE_SIZE;
		return centerPoint;
	}
	
}
