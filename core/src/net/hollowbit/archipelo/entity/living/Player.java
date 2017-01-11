package net.hollowbit.archipelo.entity.living;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.EntitySnapshot;
import net.hollowbit.archipelo.entity.EntityType;
import net.hollowbit.archipelo.entity.LivingEntity;
import net.hollowbit.archipelo.entity.living.player.MovementLog;
import net.hollowbit.archipelo.entity.living.player.MovementLogEntry;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.items.ItemType;
import net.hollowbit.archipelo.screen.screens.GameScreen;
import net.hollowbit.archipelo.tools.StaticTools;
import net.hollowbit.archipelo.world.Map;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.Controls;
import net.hollowbit.archipeloshared.Direction;

public class Player extends LivingEntity {
	
	//public static final int SPEED = 60;//Pixels per second
	public static final float ROLLING_DURATION = 0.4f;
	public static final float ROLLING_SPEED_SCALE = 3.0f;
	public static final float SPRINTING_SPEED_SCALE = 1.4f;
	public static final float ROLL_DOUBLE_CLICK_DURATION = 0.3f;
	
	//Equipped Inventory Index
	public static final int EQUIP_SIZE = 10;
	public static final int EQUIP_INDEX_BODY = 0;
	public static final int EQUIP_INDEX_BOOTS = 1;
	public static final int EQUIP_INDEX_PANTS = 2;
	public static final int EQUIP_INDEX_SHIRT = 3;
	public static final int EQUIP_INDEX_GLOVES = 4;
	public static final int EQUIP_INDEX_SHOULDERPADS = 5;
	public static final int EQUIP_INDEX_FACE = 6;
	public static final int EQUIP_INDEX_HAIR = 7;
	public static final int EQUIP_INDEX_HAT = 8;
	public static final int EQUIP_INDEX_USABLE = 9;
	
	Direction rollingDirection = Direction.UP;
	float rollingStateTime;
	float rollTimer;
	float rollDoubleClickTimer = 0;
	boolean isSprinting;
	boolean isCurrentPlayer;
	Item[] displayInventory;
	boolean loaded = false;
	float speed = 0;
	
	MovementLog movementLog;
	boolean[] controls;
	
	/**
	 * This method is used when creating a player that is the current one.
	 * No point in initializing variables if they won't be used, right?
	*/
	public void createCurrentPlayer () {
		movementLog = new MovementLog();
		this.controls = new boolean[Controls.TOTAL];
	}
	
	@Override
	public void create (EntitySnapshot fullSnapshot, Map map, EntityType entityType) {
		super.create(fullSnapshot, map, entityType);
		this.displayInventory = StaticTools.getJson().fromJson(Item[].class, fullSnapshot.getString("displayInventory", ""));
		this.speed = fullSnapshot.getFloat("speed", EntityType.PLAYER.getSpeed());
		loaded = true;
	}
	
	@Override
	public void load() {
		super.load();
	}
	
	@Override
	public void unload() {
		super.unload();
	}
	
	@Override
	public void update (float deltatime) {
		super.update(deltatime);
		
		if (isRolling()) {
			rollingStateTime += deltatime;
			rollTimer -= deltatime;
			if (rollTimer < 0) {
				rollTimer = 0;
				rollingStateTime = 0;
			}
		}
	}
	
	public void stopMovement () {
		rollTimer = 0;
		rollingStateTime = 0;
		rollDoubleClickTimer = 0;
		goal.set(location.pos);
	}
	
	public void updatePlayer (float deltatime, boolean[] controls) {
		this.controls = controls;
		if (!(ArchipeloClient.getGame().getScreenManager().getScreen() instanceof GameScreen))
			return;
		
		GameScreen gameScreen = (GameScreen) ArchipeloClient.getGame().getScreenManager().getScreen();
		
		if (!gameScreen.canPlayerMove())
			return;
		
		//Tick timer for roll double-click
		if (rollDoubleClickTimer >= 0) {
			rollDoubleClickTimer -= deltatime;
			if (rollDoubleClickTimer < 0)
				rollDoubleClickTimer = 0;
		}
		
		Vector2 pos = new Vector2(goal.x, goal.y);
		
		Direction directionMoved = null;
		double speedMoved = 0;//Use double now for accuracy and cast later
		
		//Direction
		if (controls[Controls.UP]) {
			if (controls[Controls.LEFT]) {//Up left
				if (location.getDirection() != Direction.UP_LEFT && !controls[Controls.LOCK])
					location.setDirection(Direction.UP_LEFT);
				
				if (rollingDirection != Direction.UP_LEFT)
					rollingDirection = Direction.UP_LEFT;
				
				if (isMoving(controls)) {
					directionMoved = Direction.UP_LEFT;
					speedMoved = getSpeed() / LivingEntity.DIAGONAL_FACTOR;
					pos.add((float) (-deltatime * speedMoved), (float) (deltatime * speedMoved));
				}
			} else if (controls[Controls.RIGHT]) {//Up right
				if (location.getDirection() != Direction.UP_RIGHT && !controls[Controls.LOCK])
					location.setDirection(Direction.UP_RIGHT);
				
				if (rollingDirection != Direction.UP_RIGHT)
					rollingDirection = Direction.UP_RIGHT;
				
				if (isMoving(controls)) {
					directionMoved = Direction.UP_RIGHT;
					speedMoved = getSpeed() / LivingEntity.DIAGONAL_FACTOR;
					pos.add((float) (deltatime * speedMoved), (float) (deltatime * speedMoved));
				}
			} else {//Up
				if (location.getDirection() != Direction.UP && !controls[Controls.LOCK])
					location.setDirection(Direction.UP);
				
				if (rollingDirection != Direction.UP)
					rollingDirection = Direction.UP;
				
				if (isMoving(controls)) {
					directionMoved = Direction.UP;
					speedMoved = getSpeed();
					pos.add(0, (float) (deltatime * speedMoved));
				}
			}
		} else if (controls[Controls.DOWN]) {
			if (controls[Controls.LEFT]) {//Down left
				if (location.getDirection() != Direction.DOWN_LEFT && !controls[Controls.LOCK])
					location.setDirection(Direction.DOWN_LEFT);
				
				if (rollingDirection != Direction.DOWN_LEFT)
					rollingDirection = Direction.DOWN_LEFT;
				
				if (isMoving(controls)) {
					directionMoved = Direction.DOWN_LEFT;
					speedMoved = getSpeed() / LivingEntity.DIAGONAL_FACTOR;
					pos.add((float) (-deltatime * speedMoved), (float) (-deltatime * speedMoved));
				}
			} else if (controls[Controls.RIGHT]) {//Down right
				if (location.getDirection() != Direction.DOWN_RIGHT && !controls[Controls.LOCK])
					location.setDirection(Direction.DOWN_RIGHT);
				
				if (rollingDirection != Direction.DOWN_RIGHT)
					rollingDirection = Direction.DOWN_RIGHT;
				
				if (isMoving(controls)) {
					directionMoved = Direction.DOWN_RIGHT;
					speedMoved = getSpeed() / LivingEntity.DIAGONAL_FACTOR;
					pos.add((float) (deltatime * speedMoved), (float) (-deltatime * speedMoved));
				}
			} else {//Down
				if (location.getDirection() != Direction.DOWN && !controls[Controls.LOCK])
					location.setDirection(Direction.DOWN);
				
				if (rollingDirection != Direction.DOWN)
					rollingDirection = Direction.DOWN;
				
				if (isMoving(controls)) {
					directionMoved = Direction.DOWN;
					speedMoved = getSpeed();
					pos.add(0, (float) (-deltatime * speedMoved));
				}
			}
		} else if (controls[Controls.LEFT]) {//Left
			if (location.getDirection() != Direction.LEFT && !controls[Controls.LOCK])
				location.setDirection(Direction.LEFT);
			
			if (rollingDirection != Direction.LEFT)
				rollingDirection = Direction.LEFT;
			
			if (isMoving(controls)) {
				directionMoved = Direction.LEFT;
				speedMoved = getSpeed();
				pos.add((float) (-deltatime * speedMoved), 0);
			}
		} else if (controls[Controls.RIGHT]) {//Right
			if (location.getDirection() != Direction.RIGHT && !controls[Controls.LOCK])
				location.setDirection(Direction.RIGHT);
			
			if (rollingDirection != Direction.RIGHT)
				rollingDirection = Direction.RIGHT;
			
			if (isMoving(controls)) {
				directionMoved = Direction.RIGHT;
				speedMoved = getSpeed();
				pos.add((float) (deltatime * speedMoved), 0);
			}
		}
		
		boolean collidesWithMap = false;
		for (CollisionRect rect : getCollisionRects(pos)) {//Checks to make sure no collision rect is intersecting with map
			if (location.getMap().collidesWithMap(rect, this)) {
				collidesWithMap = true;
				break;
			}
		}
		
		if (!collidesWithMap || doesCurrentPositionCollideWithMap()) {
			goal.set(pos);
			
			if (isMoving(controls)) {
				//Add new log entry to movement log manager
				gameScreen.playerMoved();
				movementLog.add(new MovementLogEntry(directionMoved, (float) speedMoved));
			}
		}
	}
	
	@Override
	public void render (SpriteBatch batch) {
		if (!loaded)
			return;
		
		drawPlayer(batch, location.getDirection(), isMoving(controls), isRolling(), location.getX(), location.getY(), movingStateTime, rollingStateTime, displayInventory, isSprinting, ArchipeloClient.PLAYER_SIZE, ArchipeloClient.PLAYER_SIZE);
		
		super.render(batch);
	}
	
	@Override
	public void applyChangesSnapshot (EntitySnapshot snapshot) {
		if (!isCurrentPlayer()) {
			if (snapshot.getBoolean("is-rolling", false) && !isRolling()) {
				rollTimer = ROLLING_DURATION;
			}
			isSprinting = snapshot.getBoolean("is-sprinting", isSprinting);
			rollingDirection = Direction.values()[snapshot.getInt("rolling-direction", rollingDirection.ordinal())];
		}
		
		//Get clothes
		if (snapshot.doesPropertyExist("displayInventory")) {
			Json json = new Json();
			this.displayInventory = json.fromJson(Item[].class, snapshot.getString("displayInventory", ""));
		}
		
		if (snapshot.doesPropertyExist("speed")) {
			System.out.println("Player.java new speed! " + snapshot.getFloat("speed", speed));
			this.speed = snapshot.getFloat("speed", speed);
		}
		
		super.applyChangesSnapshot(snapshot);
	}
	
	@Override
	public void applyInterpSnapshot (long timeStamp, EntitySnapshot snapshot1, EntitySnapshot snapshot2, float fraction) {
		if (isCurrentPlayer()) {
			//Correct player position using interp snapshot and time stamp from server
			movementLog.removeFromBeforeTimeStamp(timeStamp);
			Vector2 packet1Pos = new Vector2(snapshot1.getFloat("x", location.getX()), snapshot1.getFloat("y", location.getY()));
			Vector2 packet2Pos = new Vector2(snapshot2.getFloat("x", location.getX()), snapshot2.getFloat("y", location.getY()));
			this.goal.set(packet1Pos.lerp(packet2Pos, fraction));
			
			System.out.println("Player.java g: " + goal);
			System.out.println("Player.java p: " + this.location.pos);
			
			double lastTime = timeStamp;
			
			//Redo player prediction movements
			for (MovementLogEntry logEntry : movementLog.getCurrentLogs()) {
				float deltatime = (float) ((logEntry.timeStamp - lastTime) / 1000);
				
				//Move depending on direction
				switch(logEntry.direction) {
				case UP:
					goal.add(0, (float) (deltatime * logEntry.speed));
					break;
				case UP_LEFT:
					goal.add((float) (-deltatime * logEntry.speed), (float) (deltatime * logEntry.speed));
					break;
				case UP_RIGHT:
					goal.add((float) (deltatime * logEntry.speed), (float) (deltatime * logEntry.speed));
					break;
				case DOWN:
					goal.add(0, (float) (-deltatime * logEntry.speed));
					break;
				case DOWN_LEFT:
					goal.add((float) (-deltatime * logEntry.speed), (float) (-deltatime * logEntry.speed));
					break;
				case DOWN_RIGHT:
					goal.add((float) (deltatime * logEntry.speed), (float) (-deltatime * logEntry.speed));
					break;
				case LEFT:
					goal.add((float) (-deltatime * logEntry.speed), 0);
					break;
				case RIGHT:
					goal.add((float) (deltatime * logEntry.speed), 0);
					break;
				}
				
				lastTime = logEntry.timeStamp;
			}
			System.out.println("Player.java n: " + this.goal);
			System.out.println();
		} else
			super.applyInterpSnapshot(timeStamp, snapshot1, snapshot2, fraction);
	}
	
	public boolean isMoving (boolean[] controls) {
		if (isCurrentPlayer)
			return controls[Controls.UP] || controls[Controls.LEFT] || controls[Controls.DOWN] || controls[Controls.RIGHT];
		else
			return super.isMoving;
	}
	
	public boolean isRolling () {
		return rollTimer > 0;
	}
	
	public void controlUp (int control) {
		switch (control) {
		case Controls.ROLL:
			isSprinting = false;
			break;
		case Controls.UP:
		case Controls.LEFT:
		case Controls.DOWN:
		case Controls.RIGHT:
			movingStateTime = MOVEMENT_STATETIME_START;
			break;
		}
	}
	
	public void controlDown (int control) {
		switch (control) {
		case Controls.ROLL:
			if (rollDoubleClickTimer <= 0) {
				rollDoubleClickTimer = ROLL_DOUBLE_CLICK_DURATION;
			} else {
				rollDoubleClickTimer = 0;
				if (!isRolling())
					rollTimer = ROLLING_DURATION;
			}
			isSprinting = true;
			break;
		}
	}
	
	public void setIsCurrentPlayer (boolean isCurrentPlayer) {
		this.isCurrentPlayer = isCurrentPlayer;
	}
	
	@Override
	public boolean isPlayer () {
		return true;
	}
	
	public boolean isCurrentPlayer () {
		return isCurrentPlayer;
	}
	
	public float getSpeed () {
		return speed * (isRolling() ? ROLLING_SPEED_SCALE : (isSprinting ? SPRINTING_SPEED_SCALE : 1));
	}
	
	public Item[] getDisplayInventory () {
		return displayInventory;
	}
	
	private boolean doesCurrentPositionCollideWithMap () {
		for (CollisionRect rect : getCollisionRects(location.pos)) {//Checks to make sure no collision rect is intersecting with map
			if (location.getMap().collidesWithMap(rect, this)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Static method to draw player from any screen, not just gamescreen
	 * @param batch
	 * @param direction
	 * @param isMoving
	 * @param isRolling
	 * @param x
	 * @param y
	 * @param movingStateTime
	 * @param rollingStateTime
	 * @param equippedInventory
	 * @param isSprinting
	 * @param width
	 * @param height
	 */
	public static void drawPlayer (Batch batch, Direction direction, boolean isMoving, boolean isRolling, float xFloat, float yFloat, float movingStateTime, float rollingStateTime, Item[] equippedInventory, boolean isSprinting, float width, float height) {
		boolean drawUseableOnBottom = direction == Direction.DOWN_LEFT || direction == Direction.LEFT || direction == Direction.UP_LEFT || direction == Direction.UP; 
		
		int x = (int) xFloat;
		int y = (int) yFloat;
		
		if (isMoving) {
			if (isRolling) {
				batch.draw(EntityType.PLAYER.getAnimationFrame("rolling", direction, rollingStateTime), x, y, width, height);
			} else {
				if (isSprinting) {
					batch.draw(EntityType.PLAYER.getAnimationFrame("sprinting", direction, movingStateTime), x, y, width, height);
				} else {
					batch.draw(EntityType.PLAYER.getAnimationFrame("default", direction, movingStateTime), x, y, width, height);
				}
			}
		} else {
			batch.draw(EntityType.PLAYER.getAnimationFrame("default", direction, 0), x, y);
		}
		
		if (!ArchipeloClient.PLACEHOLDER_ART_MODE)
			return;
		
		//Placeholder art:
		if (isMoving) {
			if (isRolling) {
				if (drawUseableOnBottom && equippedInventory[EQUIP_INDEX_USABLE] != null) {
					batch.setColor(new Color(equippedInventory[EQUIP_INDEX_USABLE].color));
					batch.draw(ItemType.getItemTypeByItem(equippedInventory[EQUIP_INDEX_USABLE]).getRollFrame(direction, rollingStateTime, equippedInventory[EQUIP_INDEX_USABLE].style), x, y, width, height);
					batch.setColor(1, 1, 1, 1);
				}
				for (int i = 0; i < EQUIP_SIZE - 1; i++) {//Loop through each part of equipable
					if (equippedInventory[i] == null)
						continue;
					batch.setColor(new Color(equippedInventory[i].color));
					if (i == EQUIP_INDEX_HAIR) {
						//If a hat is equipped, use the hair texture for when wearing hats
						if (equippedInventory[EQUIP_INDEX_HAT] == null)
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getRollFrame(direction, rollingStateTime, 0), x, y, width, height);
					} else if (i == EQUIP_INDEX_FACE) {
						batch.setColor(1, 1, 1, 1);
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getRollFrame(direction, rollingStateTime, 0), x, y, width, height);//Draw mouth
						batch.setColor(new Color(equippedInventory[i].color));
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getRollFrame(direction, rollingStateTime, 1), x, y, width, height);//Draw eye's iris with color
						batch.setColor(new Color(equippedInventory[EQUIP_INDEX_HAIR].color));
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getRollFrame(direction, rollingStateTime, 2), x, y, width, height);//Draw eyebrows with hair color
						batch.setColor(1, 1, 1, 1);
					} else
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getRollFrame(direction, rollingStateTime, equippedInventory[i].style), x, y, width, height);
					batch.setColor(1, 1, 1, 1);
				}
				if (!drawUseableOnBottom && equippedInventory[EQUIP_INDEX_USABLE] != null) {
					batch.setColor(new Color(equippedInventory[EQUIP_INDEX_USABLE].color));
					batch.draw(ItemType.getItemTypeByItem(equippedInventory[EQUIP_INDEX_USABLE]).getRollFrame(direction, rollingStateTime, equippedInventory[EQUIP_INDEX_USABLE].style), x, y, width, height);
					batch.setColor(1, 1, 1, 1);
				}
			} else {
				if (isSprinting) {
					if (drawUseableOnBottom && equippedInventory[EQUIP_INDEX_USABLE] != null) {
						batch.setColor(new Color(equippedInventory[EQUIP_INDEX_USABLE].color));
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[EQUIP_INDEX_USABLE]).getRollFrame(direction, rollingStateTime, equippedInventory[EQUIP_INDEX_USABLE].style), x, y, width, height);
						batch.setColor(1, 1, 1, 1);
					}
					for (int i = 0; i < EQUIP_SIZE - 1; i++) {//Loop through each part of equipable
						if (equippedInventory[i] == null)
							continue;
						batch.setColor(new Color(equippedInventory[i].color));
						if (i == EQUIP_INDEX_HAIR) {
							//If a hat is equipped, use the hair texture for when wearing hats
							if (equippedInventory[EQUIP_INDEX_HAT] == null)
								batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getSprintFrame(direction, movingStateTime, 0), x, y, width, height);
						} else if (i == EQUIP_INDEX_FACE) {
							batch.setColor(1, 1, 1, 1);
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getSprintFrame(direction, movingStateTime, 0), x, y, width, height);//Draw mouth
							batch.setColor(new Color(equippedInventory[i].color));
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getSprintFrame(direction, movingStateTime, 1), x, y, width, height);//Draw eye's iris with color
							batch.setColor(new Color(equippedInventory[EQUIP_INDEX_HAIR].color));
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getSprintFrame(direction, movingStateTime, 2), x, y, width, height);//Draw eyebrows with hair color
							batch.setColor(1, 1, 1, 1);
						} else
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getSprintFrame(direction, movingStateTime, equippedInventory[i].style), x, y, width, height);
						batch.setColor(1, 1, 1, 1);
					}
					if (!drawUseableOnBottom && equippedInventory[EQUIP_INDEX_USABLE] != null) {
						batch.setColor(new Color(equippedInventory[EQUIP_INDEX_USABLE].color));
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[EQUIP_INDEX_USABLE]).getRollFrame(direction, rollingStateTime, equippedInventory[EQUIP_INDEX_USABLE].style), x, y, width, height);
						batch.setColor(1, 1, 1, 1);
					}
				 } else {
					if (drawUseableOnBottom && equippedInventory[EQUIP_INDEX_USABLE] != null) {
						batch.setColor(new Color(equippedInventory[EQUIP_INDEX_USABLE].color));
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[EQUIP_INDEX_USABLE]).getRollFrame(direction, rollingStateTime, equippedInventory[EQUIP_INDEX_USABLE].style), x, y, width, height);
						batch.setColor(1, 1, 1, 1);
					}
					for (int i = 0; i < EQUIP_SIZE - 1; i++) {//Loop through each part of equipable
						if (equippedInventory[i] == null)
							continue;
						batch.setColor(new Color(equippedInventory[i].color));
						if (i == EQUIP_INDEX_HAIR) {
							//Only draw hair if no hat/helmet is equipped
							if (equippedInventory[EQUIP_INDEX_HAT] == null)
								batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, movingStateTime, 0), x, y, width, height);
						} else if (i == EQUIP_INDEX_FACE) {
							batch.setColor(1, 1, 1, 1);
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, movingStateTime, 0), x, y, width, height);//Draw mouth
							batch.setColor(new Color(equippedInventory[i].color));
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, movingStateTime, 1), x, y, width, height);//Draw eye's iris with color
							batch.setColor(new Color(equippedInventory[EQUIP_INDEX_HAIR].color));
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, movingStateTime, 2), x, y, width, height);//Draw eyebrows with hair color
							batch.setColor(1, 1, 1, 1);
						} else
							batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, movingStateTime, equippedInventory[i].style), x, y, width, height);
						batch.setColor(1, 1, 1, 1);
					}
					if (!drawUseableOnBottom && equippedInventory[EQUIP_INDEX_USABLE] != null) {
						batch.setColor(new Color(equippedInventory[EQUIP_INDEX_USABLE].color));
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[EQUIP_INDEX_USABLE]).getRollFrame(direction, rollingStateTime, equippedInventory[EQUIP_INDEX_USABLE].style), x, y, width, height);
						batch.setColor(1, 1, 1, 1);
					}
				 }
			}
		} else {
			if (drawUseableOnBottom && equippedInventory[EQUIP_INDEX_USABLE] != null) {
				batch.setColor(new Color(equippedInventory[EQUIP_INDEX_USABLE].color));
				batch.draw(ItemType.getItemTypeByItem(equippedInventory[EQUIP_INDEX_USABLE]).getRollFrame(direction, rollingStateTime, equippedInventory[EQUIP_INDEX_USABLE].style), x, y, width, height);
				batch.setColor(1, 1, 1, 1);
			}
			for (int i = 0; i < EQUIP_SIZE - 1; i++) {//Loop through each part of equipable
				if (equippedInventory[i] == null)
					continue;
				batch.setColor(new Color(equippedInventory[i].color));
				if (i == EQUIP_INDEX_HAIR) {
					//If a hat is equipped, use the hair texture for when wearing hats
					if (equippedInventory[EQUIP_INDEX_HAT] == null)
						batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, 0, 0), x, y, width, height);
				} else if (i == EQUIP_INDEX_FACE) {
					batch.setColor(1, 1, 1, 1);
					batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, 0, 0), x, y, width, height);//Draw mouth
					batch.setColor(new Color(equippedInventory[i].color));
					batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, 0, 1), x, y, width, height);//Draw eye's iris with color
					batch.setColor(new Color(equippedInventory[EQUIP_INDEX_HAIR].color));
					batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, 0, 2), x, y, width, height);//Draw eyebrows with hair color
					batch.setColor(1, 1, 1, 1);
				} else
					batch.draw(ItemType.getItemTypeByItem(equippedInventory[i]).getWalkFrame(direction, 0, equippedInventory[i].style), x, y, width, height);
				batch.setColor(1, 1, 1, 1);
			}
			if (!drawUseableOnBottom && equippedInventory[EQUIP_INDEX_USABLE] != null) {
				batch.setColor(new Color(equippedInventory[EQUIP_INDEX_USABLE].color));
				batch.draw(ItemType.getItemTypeByItem(equippedInventory[EQUIP_INDEX_USABLE]).getRollFrame(direction, rollingStateTime, equippedInventory[EQUIP_INDEX_USABLE].style), x, y, width, height);
				batch.setColor(1, 1, 1, 1);
			}
		}
		batch.setColor(1, 1, 1, 1);
	}
	
	/**
	 * Creates equipment inventory with given items
	 * @return
	 */
	public static Item[] createEquipInventory (Item body, Item boots, Item pants, Item shirt, Item gloves, Item shoulderpads, Item face, Item hair, Item hat, Item usable) {
		Item[] equipInventory = new Item[EQUIP_SIZE];
		equipInventory[EQUIP_INDEX_BODY] = body;
		equipInventory[EQUIP_INDEX_BOOTS] = boots;
		equipInventory[EQUIP_INDEX_PANTS] = pants;
		equipInventory[EQUIP_INDEX_SHIRT] = shirt;
		equipInventory[EQUIP_INDEX_GLOVES] = gloves;
		equipInventory[EQUIP_INDEX_SHOULDERPADS] = shoulderpads;
		equipInventory[EQUIP_INDEX_FACE] = face;
		equipInventory[EQUIP_INDEX_HAIR] = hair;
		equipInventory[EQUIP_INDEX_HAT] = hat;
		equipInventory[EQUIP_INDEX_USABLE] = usable;
		
		return equipInventory;
	}

}
