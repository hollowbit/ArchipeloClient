package net.hollowbit.archipelo.entity.living;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.Entity;
import net.hollowbit.archipelo.entity.EntityAnimationManager.EntityAnimationObject;
import net.hollowbit.archipelo.entity.EntitySnapshot;
import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.entity.LivingEntity;
import net.hollowbit.archipelo.entity.living.player.MovementLog;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.network.packets.ControlsPacket;
import net.hollowbit.archipelo.screen.screens.GameScreen;
import net.hollowbit.archipelo.tools.StaticTools;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.Controls;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.HitCalculator;

public class CurrentPlayer extends Player {
	
	public static final float EMPTY_HAND_USE_ANIMATION_LENTH = 0.5f;
	public static final float HIT_RANGE = 8;
	public static final float CORRECTION_RATE = 0.1f;
	
	float rollDoubleClickTimer = 0;
	
	MovementLog movementLog;
	boolean[] controls;
	float speed;
	ControlsPacket lastControlsPacket;
	
	float timeSinceLastInterp = 0;
	Vector2 serverPos;
	Vector2 goal;
	Vector2 startInterpPos;
	
	/**
	 * This method is used when creating a player that is the current one.
	 * No point in initializing variables if they won't be used, right?
	*/
	public void create (EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		super.create(fullSnapshot, map, entityType);
		movementLog = new MovementLog();
		this.controls = new boolean[Controls.TOTAL];
		animationManager.change("default");
		this.speed = fullSnapshot.getFloat("speed", entityType.getSpeed());
		this.serverPos = new Vector2(location.pos);
		this.goal = new Vector2(location.pos);
		this.startInterpPos = new Vector2(location.pos);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		timeSinceLastInterp += deltaTime;
		if (timeSinceLastInterp > CORRECTION_RATE)
			timeSinceLastInterp -= CORRECTION_RATE;
		
		float fraction = timeSinceLastInterp / CORRECTION_RATE;
		
		if (fraction > 1)
			fraction = 1;
		location.pos.x = StaticTools.singleDimensionLerp(startInterpPos.x, goal.x, fraction);
		location.pos.y = StaticTools.singleDimensionLerp(startInterpPos.y, goal.y, fraction);
		//location.pos.lerp(goal, fraction);
		
		animationManager.update(deltaTime);
		
		//Tick timer for roll double-click
		if (rollDoubleClickTimer >= 0) {
			rollDoubleClickTimer -= deltaTime;
			if (rollDoubleClickTimer < 0)
				rollDoubleClickTimer = 0;
		}
	}
	
	@Override
	protected void render(SpriteBatch batch) {
		super.render(batch);
		if (ArchipeloClient.DEBUGMODE)
			batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("invalid"), serverPos.x, serverPos.y, ArchipeloClient.PLAYER_SIZE, ArchipeloClient.PLAYER_SIZE);
	}
	
	public void addCommand (ControlsPacket packet, float deltaTime) {
		packet.timeStamp = System.currentTimeMillis();
		packet.deltaTime = deltaTime;
		movementLog.add(packet);
		applyCommand(packet, deltaTime);
	}
	
	protected void applyCommand (ControlsPacket packet, float deltaTime) {
		if (!(ArchipeloClient.getGame().getScreenManager().getScreen() instanceof GameScreen))
			return;
		GameScreen gameScreen = (GameScreen) ArchipeloClient.getGame().getScreenManager().getScreen();
		
		//duplicate controls since they will be replaced
		boolean[] oldControls = new boolean[controls.length];
		for (int i = 0; i < controls.length; i++)
			oldControls[i] = controls[i];
		this.controls = packet.parse();
		
		//Loops through all controls to handle them one by one.
		for (int i = 0; i < Controls.TOTAL; i++) {
			//Checks for control change and executes controlUp/Down if there is a one.
			if (oldControls[i]) {
				if (!controls[i])
					controlUp(i);
			} else {
				if (controls[i])
					controlDown(i);
			}
		}
		
		Direction direction = getMovementDirection();
		if (!isDirectionLocked() && direction != null)
			location.direction = direction;
		
		if (isMoving()) {
			Vector2 pos = new Vector2(goal);
			double speedMoved = 0;
			switch (direction) {
			case UP:
				speedMoved = getSpeed();
				pos.add(0, (float) (deltaTime * speedMoved));
				break;
			case DOWN:
				speedMoved = getSpeed();
				pos.add(0, (float) (-deltaTime * speedMoved));
				break;
			case LEFT:
				speedMoved = getSpeed();
				pos.add((float) (-deltaTime * speedMoved), 0);
				break;
			case RIGHT:
				speedMoved = getSpeed();
				pos.add((float) (deltaTime * speedMoved), 0);
				break;
			case UP_LEFT:
				speedMoved = getSpeed() / LivingEntity.DIAGONAL_FACTOR;
				pos.add((float) (-deltaTime * speedMoved), (float) (deltaTime * speedMoved));
				break;
			case UP_RIGHT:
				speedMoved = getSpeed() / LivingEntity.DIAGONAL_FACTOR;
				pos.add((float) (deltaTime * speedMoved), (float) (deltaTime * speedMoved));
				break;
			case DOWN_LEFT:
				speedMoved = getSpeed() / LivingEntity.DIAGONAL_FACTOR;
				pos.add((float) (-deltaTime * speedMoved), (float) (-deltaTime * speedMoved));
				break;
			case DOWN_RIGHT:
				speedMoved = getSpeed() / LivingEntity.DIAGONAL_FACTOR;
				pos.add((float) (deltaTime * speedMoved), (float) (-deltaTime * speedMoved));
				break;	
			}
			
			boolean collidesWithMap = false;
			for (CollisionRect rect : getCollisionRects(pos)) {//Checks to make sure no collision rect is intersecting with map
				if (location.getMap().collidesWithMap(rect, this)) {
					collidesWithMap = true;
					break;
				}
			}
			
			//If it doesn't collide with map, move
			if (!collidesWithMap) {
				goal.set(pos);
				gameScreen.playerMoved();
			}
		}
	}
	
	@Override
	public void interpolate(long timeStamp, EntitySnapshot snapshotFrom, EntitySnapshot snapshotTo, float fraction) {
		//Do not interpolate since we are predicting
		//super.interpolate(timeStamp, snapshotFrom, snapshotTo, fraction);
	}
	
	public void applyInterpSnapshot (EntitySnapshot snapshot, long timeStamp) {
		//Correct player position using interp snapshot and time stamp from server
		movementLog.removeCommandsOlderThan(timeStamp);
		serverPos = new Vector2(snapshot.getFloat("x", location.getX()), snapshot.getFloat("y", location.getY()));
		System.out.println("CurrentPlayer Server:  " + serverPos);
		System.out.println("CurrentPlayer Client:  " + location.pos);
		goal.set(serverPos);
		
		double lastTime = timeStamp;
		
		//Redo player prediction movements
		for (ControlsPacket command : movementLog.getCurrentlyStoredCommands()) {
			float deltaTime = (float) ((command.timeStamp - lastTime) / 1000);
			applyCommand(command, deltaTime);
			lastTime = command.timeStamp;
		}
		timeSinceLastInterp = 0;
		startInterpPos.set(location.pos);
	}	
	/**
	 * MAY NOT BE USED SINCE IT CAN CAUSE WEIRD STUFF
	 * May be used to allow players to free themselves when stuck.
	 * @return
	 */
	protected boolean doesCurrentPositionCollideWithMap () {
		for (CollisionRect rect : getCollisionRects(location.pos)) {//Checks to make sure no collision rect is intersecting with map
			if (location.getMap().collidesWithMap(rect, this)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isMoving() {
		return this.isMoving(controls);
	}
	
	private boolean isMoving(boolean[] controls) {
		return controls[Controls.UP] || controls[Controls.LEFT] || controls[Controls.DOWN] || controls[Controls.RIGHT];
	}
	
	public boolean isSprinting () {
		return controls[Controls.ROLL];
	}
	
	public boolean isDirectionLocked () {
		return this.isDirectionLocked(controls);
	}
	
	public boolean isDirectionLocked (boolean[] controls) {
		return controls[Controls.LOCK];
	}
	
	public boolean isRolling() {
		return animationManager.getAnimationId().equals("roll");
	}
	
	/**
	 * Tells whether the player is currently in a use animation
	 * @return
	 */
	public boolean isUsing () {
		return animationManager.getAnimationId().equals("use") || animationManager.getAnimationId().equals("usewalk");
	}
	
	public Direction getMovementDirection () {
		if (controls[Controls.UP]) {
			if (controls[Controls.LEFT])
				return Direction.UP_LEFT;
			else if (controls[Controls.RIGHT])
				return Direction.UP_RIGHT;
			else
				return Direction.UP;
		} else if (controls[Controls.DOWN]) {
			if (controls[Controls.LEFT])
				return Direction.DOWN_LEFT;
			else if (controls[Controls.RIGHT])
				return Direction.DOWN_RIGHT;
			else
				return Direction.DOWN;
		} else if (controls[Controls.LEFT])
			return Direction.LEFT;
		else if (controls[Controls.RIGHT])
			return Direction.RIGHT;
		
		return null;
	}
	
	public void controlUp (int control) {
		switch (control) {
		case Controls.ROLL:
			if (!isRolling()) {
				if (isMoving())
					animationManager.change("walk");
				else
					animationManager.change("default");
			}
			break;
		case Controls.UP:
		case Controls.LEFT:
		case Controls.DOWN:
		case Controls.RIGHT:
			if (!isMoving() && !isRolling()) {
				if (isUsing())
					animationManager.change("use");
				else
					animationManager.change("default");
			}
			break;
		}
	}
	
	public void controlDown (int control) {
		switch (control) {
		case Controls.ROLL:
			if (!isUsing() && isMoving()) {
				animationManager.change("sprint");
				if (rollDoubleClickTimer <= 0) {
					rollDoubleClickTimer = ROLL_DOUBLE_CLICK_DURATION;
				} else {
					rollDoubleClickTimer = 0;
					if (!isRolling())
						animationManager.change("roll", "" + getMovementDirection().ordinal());
				}
			}
			break;
		case Controls.ATTACK:
			if (!isRolling() && !isUsing()) {
				ArrayList<Entity> entitiesOnMap = (ArrayList<Entity>) ArchipeloClient.getGame().getWorld().cloneEntitiesList();
				boolean useHitAnimation = true;
				for (Entity entity : entitiesOnMap) {
					if (entity == this)
						continue;
					
					//Check if player hit any entities that aren't hittable
					for (CollisionRect rect : entity.getCollisionRects()) {
						if (!entity.getEntityType().isHittable() && HitCalculator.didEntityHitEntityCollRect(this.getCenterPoint().x, this.getCenterPoint().y, rect, HIT_RANGE, getDirection())) {
							useHitAnimation = false;
							break;
						}
					}
					
					if (!useHitAnimation)//Check if we already know not to use this animation
						break;
				}
				
				//Use item if no "non-hittable" entity hit
				if(useHitAnimation) {
					Item item = clothesRenderer.getDisplayInventory()[Player.EQUIP_INDEX_USABLE];
					playUseAnimation(item);
				}
			}
				playUseAnimation(clothesRenderer.getDisplayInventory()[Player.EQUIP_INDEX_USABLE]);
			break;
		case Controls.UP:
		case Controls.LEFT:
		case Controls.DOWN:
		case Controls.RIGHT:
			if (!isRolling()) {
				if (isUsing())
					animationManager.change("usewalk");
				else
					animationManager.change("walk");
			}
			break;
		}
	}
	
	/**
	 * Play use animation for current player with the specified item
	 * @param item
	 */
	public void playUseAnimation (Item item) {
		String animationMeta = "";
		float useAnimationLength = EMPTY_HAND_USE_ANIMATION_LENTH;
		
		if (item != null) {
			Color color = new Color(item.color);
			animationMeta = item.getType() + ";" + 0 + ";" + item.style + ";" + color.r + ";" + color.g + ";" + color.b + ";" + color.a;
			useAnimationLength = item.getType().useAnimationLength;
		}
		
		//Use appropriate animations depending
		if (item != null && item.getType().useThrust) {
			if (isMoving())
				stopMovement();
			animationManager.change("thrust", animationMeta, useAnimationLength);
		} else {
			if (isMoving())
				animationManager.change("usewalk", animationMeta, useAnimationLength);
			else
				animationManager.change("use", animationMeta, useAnimationLength);
		}
	}
	
	public void stopMovement () {
		rollDoubleClickTimer = 0;
	}
	
	public float getSpeed () {
		return speed * (isRolling() ? ROLLING_SPEED_SCALE : (isSprinting() ? SPRINTING_SPEED_SCALE : 1));
	}
	
	@Override
	public EntityAnimationObject animationCompleted (String animationId) {
		if (isMoving()) {
			if (isSprinting()) {
				return new EntityAnimationObject("sprint");
			} else {//Walking
				return new EntityAnimationObject("walk");
			}
		} else//Idle
			return new EntityAnimationObject("default");
	}
	
	@Override
	public void applyChangesSnapshot(EntitySnapshot snapshot) {
		super.applyChangesSnapshot(snapshot);
		this.speed = snapshot.getFloat("speed", speed);
	}
	
}
