package net.hollowbit.archipelo.entity.living;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.Entity;
import net.hollowbit.archipelo.entity.EntityAnimationManager.EntityAnimationObject;
import net.hollowbit.archipelo.entity.EntityAudioManager;
import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.entity.LivingEntity;
import net.hollowbit.archipelo.entity.components.FootstepPlayerComponent;
import net.hollowbit.archipelo.entity.living.player.MovementLog;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.ControlsPacket;
import net.hollowbit.archipelo.network.packets.PositionCorrectionPacket;
import net.hollowbit.archipelo.screen.screens.GameScreen;
import net.hollowbit.archipelo.tools.ControlsManager;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.Controls;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.EntitySnapshot;
import net.hollowbit.archipeloshared.HitCalculator;
import net.hollowbit.archipeloshared.RollableEntity;
import net.hollowbit.archipeloshared.TileSoundType;
import net.hollowbit.archipeloshared.UseTypeSettings;

public class CurrentPlayer extends Player implements PacketHandler, RollableEntity {
	
	public static final float EMPTY_HAND_USE_ANIMATION_LENTH = 0.5f;
	public static final float HIT_RANGE = 8;
	
	float rollDoubleClickTimer = 0;
	
	MovementLog movementLog;
	boolean[] controls;
	float playerSpeed;
	Vector2 serverPos;
	Random random;
	
	public void create (EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		super.create(fullSnapshot, map, entityType);
		movementLog = new MovementLog();
		this.controls = new boolean[Controls.TOTAL];
		animationManager.change("default");
		this.playerSpeed = fullSnapshot.getFloat("playerSpeed", entityType.getSpeed());
		random = new Random(fullSnapshot.getInt("seed", 0));
		
		this.serverPos = new Vector2(location.pos);
		this.components.add(new FootstepPlayerComponent(this, true, TileSoundType.GRASS));
		
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
		overrideControls = true;
	}
	
	@Override
	public void unload() {
		ArchipeloClient.getGame().getNetworkManager().removePacketHandler(this);
		super.unload();
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
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
		//if (ArchipeloClient.DEBUGMODE)
			//batch.draw(ArchipeloClient.getGame().getAssetManager().getTexture("invalid"), location.getX(), location.getY(), ArchipeloClient.PLAYER_SIZE, ArchipeloClient.PLAYER_SIZE);
	}
	
	public void addCommand (ControlsPacket packet) {
		movementLog.add(packet);
		
		//duplicate controls since they will be replaced
		boolean[] oldControls = new boolean[controls.length];
		for (int i = 0; i < controls.length; i++)
			oldControls[i] = controls[i];
		boolean[] newControls = packet.parse();
		applyControlExceptions(newControls);
		this.controls = newControls;
		
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
		
		applyCommand(packet);
		packet.x = location.getX();
		packet.y = location.getY();
	}
	
	/**
	 * Cleans up controls when there are certain conditions.
	 * @param controls
	 */
	private void applyControlExceptions (boolean[] controls) {
		if (isThrusting()) {
			controls[Controls.UP] = false;
			controls[Controls.LEFT] = false;
			controls[Controls.DOWN] = false;
			controls[Controls.RIGHT] = false;
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	protected void applyCommand (ControlsPacket packet) {
		float deltaTime = ControlsManager.UPDATE_RATE;
		if (!(ArchipeloClient.getGame().getScreenManager().getScreen() instanceof GameScreen))
			return;
		GameScreen gameScreen = (GameScreen) ArchipeloClient.getGame().getScreenManager().getScreen();
		
		this.controls = packet.parse();
		
		Direction direction = getMovementDirection();
		if (!isDirectionLocked() && direction != null)
			location.direction = direction;
		
		if (isMoving()) {
			Vector2 pos = new Vector2(location.pos);
			double speedMoved = 0;
			switch (direction) {
			case UP:
				speedMoved = getSpeed();
				pos.add(0, (float) (deltaTime * speedMoved));
				break;
			case UP_LEFT:
			case UP_RIGHT:
				speedMoved = getSpeed() / LivingEntity.DIAGONAL_FACTOR;
				pos.add(0, (float) (deltaTime * speedMoved));
				break;
				
			case DOWN:
				speedMoved = getSpeed();
				pos.add(0, (float) (-deltaTime * speedMoved));
				break;
			case DOWN_LEFT:
			case DOWN_RIGHT:
				speedMoved = getSpeed() / LivingEntity.DIAGONAL_FACTOR;
				pos.add(0, (float) (-deltaTime * speedMoved));
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
				location.pos.set(pos);
				this.moved();
				gameScreen.playerMoved();
			}
			pos = new Vector2(location.pos);
			speedMoved = 0;
			switch (direction) {
			case LEFT:
				speedMoved = getSpeed();
				pos.add((float) (-deltaTime * speedMoved), 0);
				break;
			case UP_LEFT:
			case DOWN_LEFT:
				speedMoved = getSpeed() / LivingEntity.DIAGONAL_FACTOR;
				pos.add((float) (-deltaTime * speedMoved), 0);
				break;
				
			case RIGHT:
				speedMoved = getSpeed();
				pos.add((float) (deltaTime * speedMoved), 0);
				break;
			case UP_RIGHT:
			case DOWN_RIGHT:
				speedMoved = getSpeed() / LivingEntity.DIAGONAL_FACTOR;
				pos.add((float) (deltaTime * speedMoved), 0);
				break;	
			}
			
			collidesWithMap = false;
			for (CollisionRect rect : getCollisionRects(pos)) {//Checks to make sure no collision rect is intersecting with map
				if (location.getMap().collidesWithMap(rect, this)) {
					collidesWithMap = true;
					break;
				}
			}
			
			//If it doesn't collide with map, move
			if (!collidesWithMap) {
				location.pos.set(pos);
				this.moved();
				gameScreen.playerMoved();
			}
		}
	}
	
	@Override
	public void interpolate(long timeStamp, EntitySnapshot snapshotFrom, EntitySnapshot snapshotTo, float fraction) {
		//Do not interpolate since we are predicting
		//super.interpolate(timeStamp, snapshotFrom, snapshotTo, fraction);
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
	public void moved() {
		EntityAudioManager.moveAll();//If the player moved, then update all sound locations
	}
	
	@Override
	public boolean isMoving() {
		return this.isMoving(controls);
	}
	
	private boolean isMoving(boolean[] controls) {
		return (controls[Controls.UP] || controls[Controls.LEFT] || controls[Controls.DOWN] || controls[Controls.RIGHT]) && !animationManager.getAnimationId().equals("thrust");
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
	
	@Override
	public boolean isRolling() {
		return animationManager.getAnimationId().equals("roll");
	}

	/**
	 * Tells whether the player is currently in the thrust animation
	 * @return
	 */
	public boolean isThrusting() {
		return animationManager.getAnimationId().equals("thrust");
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
			if (!isMoving() && !isRolling() && !isThrusting()) {
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
					
					if (item != null) {
						UseTypeSettings settings = item.useTap(this);
						if (settings != null)
							playUseAnimation(item, settings.animationType, settings.thrust, settings.soundType);
					} else
						playUseAnimation(null, 0, false, 0);
				}
			}
			break;
		case Controls.UP:
		case Controls.LEFT:
		case Controls.DOWN:
		case Controls.RIGHT:
			System.out.println("Current Player  " + control);
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
	public void playUseAnimation (Item item, int animationType, boolean useThrust, int soundType) {
		String animationMeta = "";
		float useAnimationLength = EMPTY_HAND_USE_ANIMATION_LENTH;
		
		if (item != null) {
			Color color = new Color(item.color);
			animationMeta = item.getType() + ";" + animationType + ";" + item.style + ";" + color.r + ";" + color.g + ";" + color.b + ";" + color.a;
			useAnimationLength = item.getType().useAnimationLengths[animationType];
			
			audioManager.playUnsafeSound(item.getType().getSoundById(item.style, soundType));
		}
		
		//Use appropriate animations depending
		if (useThrust) {
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
	
	public Random getRandom() {
		return random;
	}
	
	public float getSpeed () {
		return isRolling() ? (entityType.getSpeed() * ROLLING_SPEED_SCALE) : (playerSpeed * (isSprinting() ? SPRINTING_SPEED_SCALE : 1));
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
		this.playerSpeed = snapshot.getFloat("playerSpeed", this.playerSpeed);
	}

	@Override
	public boolean handlePacket(Packet packet) {
		if (packet.packetType == PacketType.POSITION_CORRECTION) {
			PositionCorrectionPacket posPacket = (PositionCorrectionPacket) packet;
			
			ControlsPacket posMatchingCommand = movementLog.getCommandById(posPacket.id);
			serverPos = new Vector2(posPacket.x, posPacket.y);
			movementLog.removeCommandsOlderThan(posPacket.id);
			
			//If the player is close enough to the server pos, don't correct
			if (serverPos == null || posMatchingCommand == null || serverPos.epsilonEquals(posMatchingCommand.x, posMatchingCommand.y, 0.5f))
				return true;
			
			//Correct player position using interp snapshot and time stamp from server
			location.pos.set(serverPos);
			
			//Redo player prediction movements
			for (ControlsPacket command : movementLog.getCurrentlyStoredCommands())
				applyCommand(command);
			return true;
		}
		return false;
	}
	
}
