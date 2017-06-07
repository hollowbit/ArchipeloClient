package net.hollowbit.archipelo.tools;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.packets.ControlsPacket;
import net.hollowbit.archipelo.screen.screens.GameScreen;
import net.hollowbit.archipeloshared.Controls;
import net.hollowbit.archipeloshared.Direction;

public class ControlsManager {

	private static final int IMAGE_OFF = 0;
	private static final int IMAGE_ON = 1;
	
	public static int[] KEY_BINDINGS = new int[] {Keys.UP, Keys.LEFT, Keys.DOWN, Keys.RIGHT, Keys.X, Keys.Z, Keys.CONTROL_LEFT, Keys.TAB};
	private static final int POINTERS_TO_CHECK = 5;
	
	public static final float UPDATE_RATE = 1 / 30f;
	
	GameScreen game;
	boolean[] controls;
	boolean focused = false;
	
	boolean lockOn = false; 
	
	private Texture[] dpadImages;
	private Texture[] zImages;
	private Texture[] xImages;
	private Texture[] lockImages;
	
	private int dpadSize = 250;
	private int lockSize = 70;//Will only be used for displaying, not touch events
	private int buttonSize = 130;
	
	private int dpadX = 10, dpadY = 10;
	private int lockX = dpadX + (dpadSize / 2) - lockSize / 2, lockY = dpadY + (dpadSize / 2) - lockSize / 2;
	private int zX = 270, zY = 30;
	private int xX = 150, xY = 90;
	
	private Rectangle[] dpadRects;
	private Rectangle lockRect;
	private Circle zCircle;
	private Circle xCircle;
	
	private Direction dpadDirectionSelected = null;
	private int buttonSelected = -1;//0 = z, 1 = x
	
	private int dpadPointer = -1;
	private int buttonPointer = -1;
	
	private ArrayList<Integer> keysDown;
	private ArrayList<Integer> pointersDown;
	private Task task;
	
	public ControlsManager (GameScreen game) {
		this.game = game;
		this.keysDown = new ArrayList<Integer>();
		this.pointersDown = new ArrayList<Integer>();
		controls = new boolean[Controls.TOTAL];
		
		if (ArchipeloClient.IS_MOBILE) {
			//Dpad images
			dpadImages = new Texture[Direction.TOTAL + 1];
			dpadImages[Direction.UP.ordinal()] = new Texture("controls/dpad_up.png");
			dpadImages[Direction.UP_RIGHT.ordinal()] = new Texture("controls/dpad_up_right.png");
			dpadImages[Direction.RIGHT.ordinal()] = new Texture("controls/dpad_right.png");
			dpadImages[Direction.DOWN_RIGHT.ordinal()] = new Texture("controls/dpad_down_right.png");
			dpadImages[Direction.DOWN.ordinal()] = new Texture("controls/dpad_down.png");
			dpadImages[Direction.DOWN_LEFT.ordinal()] = new Texture("controls/dpad_down_left.png");
			dpadImages[Direction.LEFT.ordinal()] = new Texture("controls/dpad_left.png");
			dpadImages[Direction.UP_LEFT.ordinal()] = new Texture("controls/dpad_up_left.png");
			dpadImages[Direction.TOTAL] = new Texture("controls/dpad.png");
			
			for (Texture texture : dpadImages)
				texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			
			//Z images
			zImages = new Texture[2];
			zImages[IMAGE_OFF] = new Texture("controls/z_off.png");
			zImages[IMAGE_ON] = new Texture("controls/z_on.png");
			
			for (Texture texture : zImages)
				texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			
			//X images
			xImages = new Texture[2];
			xImages[IMAGE_OFF] = new Texture("controls/x_off.png");
			xImages[IMAGE_ON] = new Texture("controls/x_on.png");
			
			for (Texture texture : xImages)
				texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			
			//Lock images
			lockImages = new Texture[2];
			lockImages[IMAGE_OFF] = new Texture("controls/dpad_lock_off.png");
			lockImages[IMAGE_ON] = new Texture("controls/dpad_lock_on.png");
			
			for (Texture texture : lockImages)
				texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			
			//Dpad rectangle
			dpadRects = new Rectangle[Direction.TOTAL + 1];
			dpadRects[Direction.UP.ordinal()] = new Rectangle(dpadX + (dpadSize / 3) * 1, dpadY + (dpadSize / 3) * 2, dpadSize / 3, dpadSize / 3);
			dpadRects[Direction.UP_RIGHT.ordinal()] = new Rectangle(dpadX + (dpadSize / 3) * 2, dpadY + (dpadSize / 3) * 2, dpadSize / 3, dpadSize / 3);
			dpadRects[Direction.RIGHT.ordinal()] = new Rectangle(dpadX + (dpadSize / 3) * 2, dpadY + (dpadSize / 3) * 1, dpadSize / 3, dpadSize / 3);
			dpadRects[Direction.DOWN_RIGHT.ordinal()] = new Rectangle(dpadX + (dpadSize / 3) * 2, dpadY + (dpadSize / 3) * 0, dpadSize / 3, dpadSize / 3);
			dpadRects[Direction.DOWN.ordinal()] = new Rectangle(dpadX + (dpadSize / 3) * 1, dpadY + (dpadSize / 3) * 0, dpadSize / 3, dpadSize / 3);
			dpadRects[Direction.DOWN_LEFT.ordinal()] = new Rectangle(dpadX + (dpadSize / 3) * 0, dpadY + (dpadSize / 3) * 0, dpadSize / 3, dpadSize / 3);
			dpadRects[Direction.LEFT.ordinal()] = new Rectangle(dpadX + (dpadSize / 3) * 0, dpadY + (dpadSize / 3) * 1, dpadSize / 3, dpadSize / 3);
			dpadRects[Direction.UP_LEFT.ordinal()] = new Rectangle(dpadX + (dpadSize / 3) * 0, dpadY + (dpadSize / 3) * 2, dpadSize / 3, dpadSize / 3);
			dpadRects[Direction.TOTAL] = new Rectangle(dpadX, dpadY, dpadSize, dpadSize);
			
			lockRect = new Rectangle(dpadX + (dpadSize / 3) * 1, dpadY + (dpadSize / 3) * 1, dpadSize / 3, dpadSize / 3);
			zCircle = new Circle(Gdx.graphics.getWidth() - zX + buttonSize / 2, zY + buttonSize / 2, buttonSize / 2);
			xCircle = new Circle(Gdx.graphics.getWidth() - xX + buttonSize / 2, xY + buttonSize / 2, buttonSize / 2);
		}
		
		task = new Task() {
			
			@Override
			public void run() {
				forceUpdate();
			}
			
		};
		Timer.schedule(task, 0, UPDATE_RATE);
	}
	
	public void forceUpdate() {
		ControlsPacket packet = new ControlsPacket(getControlsClone());
		
		//Get controls sample and apply it
		if (ArchipeloClient.getGame().getWorld().getPlayer() != null) {
			ArchipeloClient.getGame().getWorld().getPlayer().addCommand(packet);
			ArchipeloClient.getGame().getNetworkManager().sendPacket(packet);
		}
	}
	
	public void dispose() {
		task.cancel();
	}
	
	public synchronized void updateControls(int index, boolean value) {
		controls[index] = value;
	}
	
	public synchronized boolean getControl(int index) {
		return controls[index];
	}
	
	public synchronized boolean[] getBlankControls () {
		boolean[] controlsClone = new boolean[controls.length];
		for (int i = 0; i < controls.length; i++) {
			controlsClone[i] = false;
		}
		return controlsClone;
	}
	
	public synchronized boolean[] getControlsClone () {
		boolean[] controlsClone = new boolean[controls.length];
		for (int i = 0; i < controls.length; i++) {
			controlsClone[i] = controls[i];
		}
		return controlsClone;
	}
	
	public void stopMovement () {
		updateControls(Controls.UP, false);
		updateControls(Controls.LEFT, false);
		updateControls(Controls.DOWN, false);
		updateControls(Controls.RIGHT, false);
	}
	
	/**
	 * Update controls
	 * @param ignoreActionButtons Whether x and z actions should be ignored
	 */
	public void update (boolean ignoreActionButtons, float deltaTime, boolean canPlayerMove) {
		if (ArchipeloClient.IS_MOBILE && ArchipeloClient.getGame().isWindowOpen()) {
			canPlayerMove = false;
			ignoreActionButtons = true;
		}
		
		//Look for keys to add
		for (int key : KEY_BINDINGS) {
			if (ignoreActionButtons && (key == KEY_BINDINGS[Controls.ATTACK] || key == KEY_BINDINGS[Controls.ROLL]))
				continue;
			
			if (!canPlayerMove && (key == KEY_BINDINGS[Controls.UP] || key == KEY_BINDINGS[Controls.LEFT] || key == KEY_BINDINGS[Controls.DOWN] || key == KEY_BINDINGS[Controls.RIGHT]))
				continue;
			
			if (Gdx.input.isKeyJustPressed(key)) {
				keysDown.add(key);
				keyDown(key);
			}
		}
		
		//Look for keys to remove
		ArrayList<Integer> keysToRemove = new ArrayList<Integer>();
		for (int key : keysDown) {
			if (ignoreActionButtons && (key == KEY_BINDINGS[Controls.ATTACK] || key == KEY_BINDINGS[Controls.ROLL]))
				continue;
			
			if (!canPlayerMove && (key == KEY_BINDINGS[Controls.UP] || key == KEY_BINDINGS[Controls.LEFT] || key == KEY_BINDINGS[Controls.DOWN] || key == KEY_BINDINGS[Controls.RIGHT]))
				continue;
			
			if (!Gdx.input.isKeyPressed(key)) {
				keysToRemove.add(key);
				keyUp(key);
			}
		}
		keysDown.removeAll(keysToRemove);
		
		//Look for pointers to add
		for (int pointer = 0; pointer < POINTERS_TO_CHECK; pointer++) {
			if (Gdx.input.isTouched(pointer) && !pointersDown.contains(pointer)) {
				pointersDown.add(pointer);
				touchDown(ignoreActionButtons, canPlayerMove, Gdx.input.getX(pointer), Gdx.input.getY(pointer), pointer);
			}
		}
		
		//Look for pointers to remove
		ArrayList<Integer> pointersToRemove = new ArrayList<Integer>();
		for (int pointer : pointersDown) {
			if (!Gdx.input.isTouched(pointer)) {
				pointersToRemove.add(pointer);
				touchUp(ignoreActionButtons, canPlayerMove, Gdx.input.getX(pointer), Gdx.input.getY(pointer), pointer);
			}
		}
		pointersDown.removeAll(pointersToRemove);
		
		//Update pointer moving
		for (int pointer : pointersDown)
			pointerMoved(ignoreActionButtons, canPlayerMove, Gdx.input.getX(pointer), Gdx.input.getY(pointer), pointer);
	}
	
	public void render (SpriteBatch batch) {
		if (ArchipeloClient.IS_MOBILE && !ArchipeloClient.getGame().isWindowOpen()) {
			//Draw dpad
			if (getControl(Controls.UP)) {
				if (getControl(Controls.LEFT))
					batch.draw(dpadImages[Direction.UP_LEFT.ordinal()], dpadX, dpadY, dpadSize, dpadSize);
				else if (getControl(Controls.RIGHT))
					batch.draw(dpadImages[Direction.UP_RIGHT.ordinal()], dpadX, dpadY, dpadSize, dpadSize);
				else
					batch.draw(dpadImages[Direction.UP.ordinal()], dpadX, dpadY, dpadSize, dpadSize);
			} else if (getControl(Controls.DOWN)) {
				if (getControl(Controls.LEFT))
					batch.draw(dpadImages[Direction.DOWN_LEFT.ordinal()], dpadX, dpadY, dpadSize, dpadSize);
				else if (getControl(Controls.RIGHT))
					batch.draw(dpadImages[Direction.DOWN_RIGHT.ordinal()], dpadX, dpadY, dpadSize, dpadSize);
				else
					batch.draw(dpadImages[Direction.DOWN.ordinal()], dpadX, dpadY, dpadSize, dpadSize);
			} else if (getControl(Controls.LEFT))
				batch.draw(dpadImages[Direction.LEFT.ordinal()], dpadX, dpadY, dpadSize, dpadSize);
			else if (getControl(Controls.RIGHT))
				batch.draw(dpadImages[Direction.RIGHT.ordinal()], dpadX, dpadY, dpadSize, dpadSize);
			else
				batch.draw(dpadImages[Direction.TOTAL], dpadX, dpadY, dpadSize, dpadSize);
			
			
			//Draw lock
			batch.draw(lockImages[(getControl(Controls.DIRECTION_LOCK) ? IMAGE_ON : IMAGE_OFF)], lockX, lockY, lockSize, lockSize);
			
			//Draw buttons
			batch.draw(zImages[(getControl(Controls.ROLL) ? IMAGE_ON : IMAGE_OFF)], Gdx.graphics.getWidth() - zX, zY, buttonSize, buttonSize);
			batch.draw(xImages[(getControl(Controls.ATTACK) ? IMAGE_ON : IMAGE_OFF)], Gdx.graphics.getWidth() - xX, xY, buttonSize, buttonSize);
		}
	}
	
	public void resize (int width, int height) {//Since they are drawn on the right side, they need to update when the screen resizes
		if (ArchipeloClient.IS_MOBILE) {
			zCircle.setX(width - zX + buttonSize / 2);
			xCircle.setX(width - xX + buttonSize / 2);
		}
	}
	
	//Controls handlers
	public void keyDown (int keycode) {
		if (!focused)
			return;
		
		if (keycode == KEY_BINDINGS[Controls.UP]) {
			if (ArchipeloClient.INVERT)
				updateControls(Controls.DOWN, true);
			else
				updateControls(Controls.UP, true);	
		} else if (keycode == KEY_BINDINGS[Controls.LEFT]) {
			updateControls(Controls.LEFT, true);
		} else if (keycode == KEY_BINDINGS[Controls.DOWN]) {
			if (ArchipeloClient.INVERT)
				updateControls(Controls.UP, true);
			else
				updateControls(Controls.DOWN, true);
		} else if (keycode == KEY_BINDINGS[Controls.RIGHT]) {
			updateControls(Controls.RIGHT, true);
		} else if (keycode == KEY_BINDINGS[Controls.ATTACK]) {
			updateControls(Controls.ATTACK, true);
		} else if (keycode == KEY_BINDINGS[Controls.ROLL]) {
			updateControls(Controls.ROLL, true);
		} else if (keycode == KEY_BINDINGS[Controls.DIRECTION_LOCK]) {
			updateControls(Controls.DIRECTION_LOCK, true);
		} else if (keycode == KEY_BINDINGS[Controls.MOVEMENT_LOCK]) {
			updateControls(Controls.MOVEMENT_LOCK, true);
		}
	}
	
	public void keyUp (int keycode) {
		if (!focused)
			return;
		
		if (keycode == KEY_BINDINGS[Controls.UP]) {
			if (ArchipeloClient.INVERT)
				updateControls(Controls.DOWN, false);
			else
				updateControls(Controls.UP, false);	
		} else if (keycode == KEY_BINDINGS[Controls.LEFT]) {
			updateControls(Controls.LEFT, false);
		} else if (keycode == KEY_BINDINGS[Controls.DOWN]) {
			if (ArchipeloClient.INVERT)
				updateControls(Controls.UP, false);
			else
				updateControls(Controls.DOWN, false);
		} else if (keycode == KEY_BINDINGS[Controls.RIGHT]) {
			updateControls(Controls.RIGHT, false);
		} else if (keycode == KEY_BINDINGS[Controls.ATTACK]) {
			updateControls(Controls.ATTACK, false);
		} else if (keycode == KEY_BINDINGS[Controls.ROLL]) {
			updateControls(Controls.ROLL, false);
		} else if (keycode == KEY_BINDINGS[Controls.DIRECTION_LOCK]) {
			updateControls(Controls.DIRECTION_LOCK, false);
		} else if (keycode == KEY_BINDINGS[Controls.MOVEMENT_LOCK]) {
			updateControls(Controls.MOVEMENT_LOCK, false);
		}
	}
	
	public void setFocused (boolean focused) {
		this.focused = focused;
		if (!focused) {
			controls = getBlankControls();
		}
	}
	
	public boolean touchDown(boolean ignoreAction, boolean canPlayerMove, int screenX, int screenY, int pointer) {
		if (ArchipeloClient.IS_MOBILE) {
			Vector2 input = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
			if (dpadRects[Direction.TOTAL].contains(input.x, input.y) && !lockRect.contains(input.x, input.y)) {//If is touching dpad
				if (canPlayerMove) {
					dpadPointer = pointer;//Set dpad pointer
					
					//Update controls based on where dpad is pressed
					if (dpadRects[Direction.UP.ordinal()].contains(input.x, input.y)) {
						updateControls(Controls.UP, true);
						dpadDirectionSelected = Direction.UP;
					} else if (dpadRects[Direction.UP_RIGHT.ordinal()].contains(input.x, input.y)) {
						updateControls(Controls.UP, true);
						updateControls(Controls.RIGHT, true);
						dpadDirectionSelected = Direction.UP_RIGHT;
					} else if (dpadRects[Direction.RIGHT.ordinal()].contains(input.x, input.y)) {
						updateControls(Controls.RIGHT, true);
						dpadDirectionSelected = Direction.RIGHT;
					} else if (dpadRects[Direction.DOWN_RIGHT.ordinal()].contains(input.x, input.y)) {
						updateControls(Controls.DOWN, true);
						updateControls(Controls.RIGHT, true);
						dpadDirectionSelected = Direction.DOWN_RIGHT;
					} else if (dpadRects[Direction.DOWN.ordinal()].contains(input.x, input.y)) {
						updateControls(Controls.DOWN, true);
						dpadDirectionSelected = Direction.DOWN;
					} else if (dpadRects[Direction.DOWN_LEFT.ordinal()].contains(input.x, input.y)) {
						updateControls(Controls.DOWN, true);
						updateControls(Controls.LEFT, true);
						dpadDirectionSelected = Direction.DOWN_LEFT;
					} else if (dpadRects[Direction.LEFT.ordinal()].contains(input.x, input.y)) {
						updateControls(Controls.LEFT, true);
						dpadDirectionSelected = Direction.LEFT;
					} else if (dpadRects[Direction.UP_LEFT.ordinal()].contains(input.x, input.y)) {
						updateControls(Controls.UP, true);
						updateControls(Controls.LEFT, true);
						dpadDirectionSelected = Direction.UP_LEFT;
					}
					return true;
				}
			} else if (zCircle.contains(input.x, input.y)) {
				if (!ignoreAction) {
					buttonPointer = pointer;
					updateControls(Controls.ROLL, true);
					buttonSelected = 0;
					return true;
				}
			} else if (xCircle.contains(input.x, input.y)) {
				if (!ignoreAction) {
					buttonPointer = pointer;
					updateControls(Controls.ATTACK, true);
					buttonSelected = 1;
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean pointerMoved (boolean ignoreAction, boolean canPlayerMove, int screenX, int screenY, int pointer) {
		if (ArchipeloClient.IS_MOBILE) {
			Vector2 input = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
			if (pointer == dpadPointer && canPlayerMove) {//If player is using the same point as when they were touching the dpad before
				if (dpadRects[Direction.TOTAL].contains(input.x, input.y)) {//If is touching dpad
					//Update controls based on where dpad is pressed
					if (dpadRects[Direction.UP.ordinal()].contains(input.x, input.y) && dpadDirectionSelected != Direction.UP) {
							clearDPadForUpdate();
							updateControls(Controls.UP, true);
							dpadDirectionSelected = Direction.UP;
							ArchipeloClient.getGame().getWorld().getPlayer().controlDown(Controls.UP);
					} else if (dpadRects[Direction.UP_RIGHT.ordinal()].contains(input.x, input.y) && dpadDirectionSelected != Direction.UP_RIGHT) {
						clearDPadForUpdate();
						updateControls(Controls.UP, true);
						updateControls(Controls.RIGHT, true);
						dpadDirectionSelected = Direction.UP_RIGHT;
					} else if (dpadRects[Direction.RIGHT.ordinal()].contains(input.x, input.y) && dpadDirectionSelected != Direction.RIGHT) {
						clearDPadForUpdate();
						updateControls(Controls.RIGHT, true);
						dpadDirectionSelected = Direction.RIGHT;
					} else if (dpadRects[Direction.DOWN_RIGHT.ordinal()].contains(input.x, input.y) && dpadDirectionSelected != Direction.DOWN_RIGHT) {
						clearDPadForUpdate();
						updateControls(Controls.DOWN, true);
						updateControls(Controls.RIGHT, true);
						dpadDirectionSelected = Direction.DOWN_RIGHT;
					} else if (dpadRects[Direction.DOWN.ordinal()].contains(input.x, input.y) && dpadDirectionSelected != Direction.DOWN) {
						clearDPadForUpdate();
						updateControls(Controls.DOWN, true);
						dpadDirectionSelected = Direction.DOWN;
					} else if (dpadRects[Direction.DOWN_LEFT.ordinal()].contains(input.x, input.y) && dpadDirectionSelected != Direction.DOWN_LEFT) {
						clearDPadForUpdate();
						updateControls(Controls.DOWN, true);
						updateControls(Controls.LEFT, true);
						dpadDirectionSelected = Direction.DOWN_LEFT;
					} else if (dpadRects[Direction.LEFT.ordinal()].contains(input.x, input.y) && dpadDirectionSelected != Direction.LEFT) {
						clearDPadForUpdate();
						updateControls(Controls.LEFT, true);
						dpadDirectionSelected = Direction.LEFT;
					} else if (dpadRects[Direction.UP_LEFT.ordinal()].contains(input.x, input.y) && dpadDirectionSelected != Direction.UP_LEFT) {
						clearDPadForUpdate();
						updateControls(Controls.UP, true);
						updateControls(Controls.LEFT, true);
						dpadDirectionSelected = Direction.UP_LEFT;
					}
					return true;
				}
			} else if (pointer == buttonPointer && !ignoreAction) {
				if (zCircle.contains(input.x, input.y) && buttonSelected != 0) {
					updateControls(Controls.ROLL, true);
					updateControls(Controls.ATTACK, false);
					buttonSelected = 0;
					return true;
				} else if (xCircle.contains(input.x, input.y) && buttonSelected != 1) {
					updateControls(Controls.ATTACK, true);
					updateControls(Controls.ROLL, false);
					buttonSelected = 1;
					return true;
				}
			}
		}
		return false;
	}

	public boolean touchUp(boolean ignoreAction, boolean canPlayerMove, int screenX, int screenY, int pointer) {
		if (ArchipeloClient.IS_MOBILE) {
			Vector2 input = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
			if (pointer == dpadPointer && canPlayerMove) {//If player is using the same point as when they were touching the dpad before
				clearDPadForUpdate();
				dpadPointer = -1;
				dpadDirectionSelected = null;
				return true;
			} else if (pointer == buttonPointer && !ignoreAction) {
				updateControls(Controls.ATTACK, false);
				updateControls(Controls.ROLL, false);
				buttonPointer = -1;
				buttonSelected = -1;
				return true;
			} else if (lockRect.contains(input.x, input.y)) {
				updateControls(Controls.DIRECTION_LOCK, !getControl(Controls.DIRECTION_LOCK));
				return true;
			}
		}
		return false;
	}
	
	private void clearDPadForUpdate () {
		updateControls(Controls.UP, false);
		updateControls(Controls.LEFT, false);
		updateControls(Controls.DOWN, false);
		updateControls(Controls.RIGHT, false);
	}
	
}
