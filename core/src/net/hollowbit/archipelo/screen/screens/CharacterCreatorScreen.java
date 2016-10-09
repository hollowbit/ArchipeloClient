package net.hollowbit.archipelo.screen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.living.Player;
import net.hollowbit.archipelo.items.Item;
import net.hollowbit.archipelo.items.ItemType;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.PlayerPickPacket;
import net.hollowbit.archipelo.screen.Screen;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.screen.screens.mainmenu.CharacterDisplay;
import net.hollowbit.archipelo.screen.screens.playercreator.ColorPickListener;
import net.hollowbit.archipelo.screen.screens.playercreator.ColorPicker;
import net.hollowbit.archipelo.tools.GameCamera;
import net.hollowbit.archipeloshared.CollisionRect;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.StringValidator;

public class CharacterCreatorScreen extends Screen implements PacketHandler {
	
	private static final float CAM_SPEED_X = 50;
	private static final float CAM_SPEED_Y = 40;
	
	private static final int ARROW_BUTTON_HEIGHT = 96;
	private static final int ARROW_BUTTON_WIDTH = 40;
	private static final int PART_BUTTON_SIZE = 96;
	private static final int PADDING = 4;
	private static final int PADDING_FROM_EACH_OTHER = 15;
	private static final int PADDING_FROM_TOP = 150;
	private static final int DISPLAY_SIZE = 320;
	
	private static final Item[] HAIR_STYLES = {new Item(ItemType.HAIR1)};
	private static final Item[] FACE_STYLES = {new Item(ItemType.FACE1)};
	private static final Item BODY = new Item(ItemType.BODY);
	private static final Item SHIRT = new Item(ItemType.SHIRT_BASIC);
	private static final Item PANTS = new Item(ItemType.PANTS_BASIC);
	
	private static final Color[] HAIR_COLORS = {new Color(1, 1, 1, 1), new Color(0.627f, 0.412f, 0.071f, 1), new Color(0.843f, 0.824f, 0.275f, 1)};
	private static final Color[] EYE_COLORS = {Color.BLUE, Color.BROWN, Color.RED, Color.GREEN};
	private static final Color[] BODY_COLORS = {new Color(1, 1, 1, 1), new Color(0.7f, 0.5f, 0.08f, 1)};
	//private static final Color[] SHIRT_COLORS = {new Color(1, 1, 1, 1)};
	//private static final Color[] PANTS_COLORS = {new Color(1, 1, 1, 1)};
	
	Stage stage;	
	Stage stageWindow;
	TextButton changeHairLeftButton;
	TextButton changeHairRightButton;
	TextButton changeFaceLeftButton;
	TextButton changeFaceRightButton;
	
	CharacterDisplay characterDisplay;
	
	TextField nameTextField;
	
	TextButton finishButton;
	
	Button hairColorButton;
	Button eyeColorButton;
	Button bodyColorButton;
	Button pantsColorButton;
	Button shirtColorButton;
	
	int selectedHair = 0;
	int selectedFace = 0;
	
	int hairColor;
	int eyeColor;
	int bodyColor;
	//int shirtColor;
	//int pantsColor;
	
	float stetime = 0;
	
	float camVelocityX = CAM_SPEED_X, camVelocityY = CAM_SPEED_Y;
	GameCamera cam;
	Texture background;
	
	ColorPicker colorPickerWindow = null;//Not null if a window is open
	
	public CharacterCreatorScreen () {
		super(ScreenType.CHARACTER_CREATOR);
	}

	@Override
	public void create () {
		stage = new Stage(ArchipeloClient.getGame().getCameraUi().getScreenViewport(), ArchipeloClient.getGame().getBatch());
		stageWindow = new Stage(ArchipeloClient.getGame().getCameraUi().getScreenViewport(), ArchipeloClient.getGame().getBatch());
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
		
		Gdx.input.setInputProcessor(new InputMultiplexer(stageWindow, stage));//Put stageWindow first so it has priority
		
		//Hair
		changeHairLeftButton = new TextButton("<", ArchipeloClient.getGame().getUiSkin());
		changeHairLeftButton.setBounds(0, 0, ARROW_BUTTON_WIDTH, ARROW_BUTTON_HEIGHT);
		changeHairLeftButton.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//Change hair selection
				selectedHair--;
				if (selectedHair < 0)
					selectedHair = HAIR_STYLES.length - 1;
				super.clicked(event, x, y);
			}
			
		});
		stage.addActor(changeHairLeftButton);
		
		hairColorButton = new Button(ArchipeloClient.getGame().getUiSkin());
		hairColorButton.setBounds(0, 0, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		hairColorButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (colorPickerWindow != null)//If a color picker window is already open, don't opena  new one
					return;
				
				ColorPicker colorPicker = new ColorPicker("Hair", hairColor, HAIR_COLORS, new ColorPickListener() {
					@Override
					public void colorPicked(int indexOfColor) {
						hairColor = indexOfColor;
						colorPickerWindow = null;
					}
				});
				colorPicker.setPosition(Gdx.graphics.getWidth() / 2 - colorPicker.getWidth() / 2, Gdx.graphics.getHeight() / 2 - colorPicker.getHeight() / 2);
				stageWindow.addActor(colorPicker);
				super.clicked(event, x, y);
			}
		});
		stage.addActor(hairColorButton);
		
		changeHairRightButton = new TextButton(">", ArchipeloClient.getGame().getUiSkin());
		changeHairRightButton.setBounds(0, 0, ARROW_BUTTON_WIDTH, ARROW_BUTTON_HEIGHT);
		changeHairRightButton.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//Change hair selection
				selectedHair++;
				if (selectedHair >= HAIR_STYLES.length)
					selectedHair = 0;
				super.clicked(event, x, y);
			}
			
		});
		stage.addActor(changeHairRightButton);
		
		//Face
		changeFaceLeftButton = new TextButton("<", ArchipeloClient.getGame().getUiSkin());
		changeFaceLeftButton.setBounds(0, 0, ARROW_BUTTON_WIDTH, ARROW_BUTTON_HEIGHT);
		changeFaceLeftButton.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//Change hair selection
				selectedFace--;
				if (selectedFace < 0)
					selectedFace = FACE_STYLES.length - 1;
				super.clicked(event, x, y);
			}
			
		});
		stage.addActor(changeFaceLeftButton);
		
		eyeColorButton = new Button(ArchipeloClient.getGame().getUiSkin());
		eyeColorButton.setBounds(0, 0, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		eyeColorButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (colorPickerWindow != null)//If a color picker window is already open, don't opena  new one
					return;
				
				ColorPicker colorPicker = new ColorPicker("Eye", eyeColor, EYE_COLORS, new ColorPickListener() {
					@Override
					public void colorPicked(int indexOfColor) {
						eyeColor = indexOfColor;
						colorPickerWindow = null;
					}
				});
				colorPicker.setPosition(Gdx.graphics.getWidth() / 2 - colorPicker.getWidth() / 2, Gdx.graphics.getHeight() / 2 - colorPicker.getHeight() / 2);
				stageWindow.addActor(colorPicker);
				super.clicked(event, x, y);
			}
		});
		stage.addActor(eyeColorButton);
		
		changeFaceRightButton = new TextButton(">", ArchipeloClient.getGame().getUiSkin());
		changeFaceRightButton.setBounds(0, 0, ARROW_BUTTON_WIDTH, ARROW_BUTTON_HEIGHT);
		changeFaceRightButton.addListener(new ClickListener() {
					
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//Change hair selection
				selectedFace++;
				if (selectedFace >= FACE_STYLES.length)
					selectedFace = 0;
				super.clicked(event, x, y);
			}
					
		});
		stage.addActor(changeFaceRightButton);
		
		//Body
		bodyColorButton = new Button(ArchipeloClient.getGame().getUiSkin());
		bodyColorButton.setBounds(0, 0, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		bodyColorButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (colorPickerWindow != null)//If a color picker window is already open, don't opena  new one
					return;
				
				ColorPicker colorPicker = new ColorPicker("Skin", bodyColor, BODY_COLORS, new ColorPickListener() {
					@Override
					public void colorPicked(int indexOfColor) {
						bodyColor = indexOfColor;
						colorPickerWindow = null;
					}
				});
				colorPicker.setPosition(Gdx.graphics.getWidth() / 2 - colorPicker.getWidth() / 2, Gdx.graphics.getHeight() / 2 - colorPicker.getHeight() / 2);
				stageWindow.addActor(colorPicker);
				super.clicked(event, x, y);
			}
		});
		stage.addActor(bodyColorButton);
		
		//Shirt
		shirtColorButton = new Button(ArchipeloClient.getGame().getUiSkin());
		shirtColorButton.setBounds(0, 0, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		shirtColorButton.setTouchable(Touchable.disabled);
		/*shirtColorButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (colorPickerWindow != null)//If a color picker window is already open, don't opena  new one
					return;
				
				ColorPicker colorPicker = new ColorPicker("Shirt", shirtColor, SHIRT_COLORS, new ColorPickListener() {
					@Override
					public void colorPicked(int indexOfColor) {
						shirtColor = indexOfColor;
						colorPickerWindow = null;
					}
				});
				colorPicker.setPosition(Gdx.graphics.getWidth() / 2 - colorPicker.getWidth() / 2, Gdx.graphics.getHeight() / 2 - colorPicker.getHeight() / 2);
				stageWindow.addActor(colorPicker);
				super.clicked(event, x, y);
			}
		});*/
		stage.addActor(shirtColorButton);
		
		//Pants
		pantsColorButton = new Button(ArchipeloClient.getGame().getUiSkin());
		pantsColorButton.setBounds(0, 0, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		pantsColorButton.setTouchable(Touchable.disabled);
		/*pantsColorButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (colorPickerWindow != null)//If a color picker window is already open, don't opena  new one
					return;
				
				ColorPicker colorPicker = new ColorPicker("Pants", pantsColor, PANTS_COLORS, new ColorPickListener() {
					@Override
					public void colorPicked(int indexOfColor) {
						pantsColor = indexOfColor;
						colorPickerWindow = null;
					}
				});
				colorPicker.setPosition(Gdx.graphics.getWidth() / 2 - colorPicker.getWidth() / 2, Gdx.graphics.getHeight() / 2 - colorPicker.getHeight() / 2);
				stageWindow.addActor(colorPicker);
				super.clicked(event, x, y);
			}
		});*/
		stage.addActor(pantsColorButton);
		
		//Finish
		finishButton = new TextButton("Play Game!", ArchipeloClient.getGame().getUiSkin(), "large");
		finishButton.setBounds(0, 0, 300, 60);
		finishButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (nameTextField.getText().equals("")) {
					showErrorWindow("Please enter a name.");
					return;
				} else if (!StringValidator.isStringValid(nameTextField.getText(), StringValidator.USERNAME)) {
					showErrorWindow("Please only use a-zA-Z0-9 and _ for names.");
				}
				
				new PlayerPickPacket(nameTextField.getText(), selectedHair, selectedFace, hairColor, eyeColor, bodyColor).send();
				super.clicked(event, x, y);
			}
		});
		stage.addActor(finishButton);
		
		//Character display
		characterDisplay = new CharacterDisplay(getEquppedInventory());
		characterDisplay.setBounds(0, 0, DISPLAY_SIZE, DISPLAY_SIZE);
		stage.addActor(characterDisplay);
		
		nameTextField = new TextField("", ArchipeloClient.getGame().getUiSkin());
		nameTextField.setBounds(0, 0, DISPLAY_SIZE, nameTextField.getHeight());
		nameTextField.setMessageText("Name");
		stage.addActor(nameTextField);
		
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//Moving background
		background = ArchipeloClient.getGame().getAssetManager().getTexture("mainmenu-background");
		cam = ArchipeloClient.getGame().getCamera();
		cam.move(200, 200, 0);
		cam.zoom(3);
		cam.focusOnEntity(null);
		
		hairColor = 0;
		eyeColor = 0;
		bodyColor = 0;
		//shirtColor = 0;
		//pantsColor = 0;
	}

	@Override
	public void update (float deltaTime) {
		stage.act();
		stageWindow.act();
		
		//Update game camera to move around map
		CollisionRect rect = cam.getViewRect();
		float camX = rect.x + camVelocityX * deltaTime;
		float camY = rect.y + camVelocityY * deltaTime;
		
		if (camX < 0) {
			camX = 0;
			camVelocityX = -camVelocityX;
		}
		
		if (camY < 0) {
			camY = 0;
			camVelocityY = -camVelocityY;
		}
		
		if (camX + rect.width > background.getWidth()) {
			camX = background.getWidth() - rect.width;
			camVelocityX = -camVelocityX;
		}
		
		if (camY + rect.height > background.getHeight()) {
			camY = background.getHeight() - rect.height;
			camVelocityY = -camVelocityY;
		}
		
		cam.move(camX, camY, 0);
	}
	
	@Override
	public void render (SpriteBatch batch, float width, float height) {
		batch.draw(background, 0, 0);//Render blurred background image
	}

	@Override
	public void renderUi (SpriteBatch batch, float width, float height) {
		batch.end();
		stage.draw();
		batch.begin();
		
		/////////////Draw display items////////////
		//Draw hair
		batch.setColor(HAIR_COLORS[hairColor]);
		batch.draw(HAIR_STYLES[selectedHair].getType().getWalkFrame(Direction.DOWN, 0), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		
		//Draw face
		batch.setColor(1, 1, 1, 1);
		batch.draw(FACE_STYLES[selectedFace].getType().getWalkFrame(Direction.DOWN, 0, 0), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE), PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		batch.setColor(EYE_COLORS[eyeColor]);
		batch.draw(FACE_STYLES[selectedFace].getType().getWalkFrame(Direction.DOWN, 0, 1), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE), PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		batch.setColor(HAIR_COLORS[hairColor]);
		batch.draw(FACE_STYLES[selectedFace].getType().getWalkFrame(Direction.DOWN, 0, 2), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE), PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		
		//Draw body
		batch.setColor(BODY_COLORS[bodyColor]);
		batch.draw(BODY.getType().getWalkFrame(Direction.DOWN, 0), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE) * 2, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		
		//Draw shirt
		//batch.setColor(SHIRT_COLORS[shirtColor]);
		batch.setColor(1, 1, 1, 1);
		batch.draw(SHIRT.getType().getWalkFrame(Direction.DOWN, 0), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE) * 3, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		
		//Draw pants
		//batch.setColor(PANTS_COLORS[pantsColor]);
		batch.setColor(1, 1, 1, 1);
		batch.draw(PANTS.getType().getWalkFrame(Direction.DOWN, 0), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE) * 4, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		
		batch.end();
		stageWindow.draw();
		batch.begin();
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height);
		//Hair
		changeHairLeftButton.setPosition(width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING - ARROW_BUTTON_WIDTH - PADDING, height - PADDING_FROM_TOP);
		hairColorButton.setPosition(width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP);
		changeHairRightButton.setPosition(width - ARROW_BUTTON_WIDTH - PADDING, height - PADDING_FROM_TOP);
		
		//Face
		changeFaceLeftButton.setPosition(width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING - ARROW_BUTTON_WIDTH - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE));
		eyeColorButton.setPosition(width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE));
		changeFaceRightButton.setPosition(width - ARROW_BUTTON_WIDTH - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE));
		
		//Body
		bodyColorButton.setPosition(width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE) * 2);

		//Shirt
		shirtColorButton.setPosition(width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE) * 3);
		
		//Pants
		pantsColorButton.setPosition(width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE) * 4);
		
		//Finish
		finishButton.setPosition(width - PADDING_FROM_EACH_OTHER - finishButton.getWidth(), PADDING_FROM_EACH_OTHER);
		
		//Change direction
		characterDisplay.setPosition(width / 2 - characterDisplay.getWidth() / 2, height / 2 - characterDisplay.getHeight() / 2);
		
		//Name Textfield
		nameTextField.setPosition(width / 2 - nameTextField.getWidth() / 2, height / 2 - characterDisplay.getHeight() / 2 - nameTextField.getHeight() - 5);
	}
	
	private Item[] getEquppedInventory () {
		HAIR_STYLES[selectedHair].color = Color.rgba8888(HAIR_COLORS[hairColor]);
		FACE_STYLES[selectedFace].color = Color.rgba8888(EYE_COLORS[eyeColor]);
		return Player.createEquipInventory(BODY, null, PANTS, SHIRT, null, null, FACE_STYLES[selectedFace], HAIR_STYLES[selectedHair], null, null);
	}
	
	@Override
	public void dispose () {
		stage.dispose();
		ArchipeloClient.getGame().getNetworkManager().removePacketHandler(this);
	}

	@Override
	public boolean handlePacket (Packet packet) {
		if (packet.packetType == PacketType.PLAYER_PICK) {
			PlayerPickPacket playerPickPacket = (PlayerPickPacket) packet;
			
			//Handle packet result
			switch(playerPickPacket.result) {
			case PlayerPickPacket.RESULT_NAME_ALREADY_TAKEN:
				showErrorWindow("Name already taken. Sorry.");
				break;
			case PlayerPickPacket.RESULT_INVALID_USERNAME:
				showErrorWindow("Please only use a-zA-Z0-9 and _ for names.");
				break;
			case PlayerPickPacket.RESULT_TOO_MANY_CHARACTERS:
				showErrorWindow("You should not be in this menu. You have too many characters.");
				break;
			case PlayerPickPacket.RESULT_SUCCESSFUL:
				ArchipeloClient.getGame().getScreenManager().setScreen(new GameScreen(playerPickPacket.name));
				break;
			}
		}
		return false;
	}
	
	private void showErrorWindow (String error) {
		Dialog dialog = new Dialog("Creation Error", ArchipeloClient.getGame().getUiSkin(), "dialog") {
		    public void result(Object obj) {
		        remove();
		    }
		};
		dialog.text(error);
		dialog.button("Close", true);
		dialog.key(Keys.ENTER, true);
		dialog.key(Keys.ESCAPE, true);
		dialog.show(stage);
	}

}
