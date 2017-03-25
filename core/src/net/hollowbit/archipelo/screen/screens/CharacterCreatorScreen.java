package net.hollowbit.archipelo.screen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
import net.hollowbit.archipelo.screen.screens.mainmenu.ScrollingBackground;
import net.hollowbit.archipelo.screen.screens.playercreator.ColorPickListener;
import net.hollowbit.archipelo.screen.screens.playercreator.ColorPicker;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipelo.tools.LanguageSpecificMessageManager.Cat;
import net.hollowbit.archipelo.tools.QuickUi;
import net.hollowbit.archipelo.tools.QuickUi.IconType;
import net.hollowbit.archipelo.tools.QuickUi.TextFieldMessageListener;
import net.hollowbit.archipeloshared.Direction;
import net.hollowbit.archipeloshared.StringValidator;

public class CharacterCreatorScreen extends Screen implements PacketHandler {
	
	private static final int ARROW_BUTTON_HEIGHT = 96;
	private static final int ARROW_BUTTON_WIDTH = 40;
	private static final int PART_BUTTON_SIZE = 96;
	private static final int PADDING = 4;
	private static final int PADDING_FROM_EACH_OTHER = 15;
	private static final int PADDING_FROM_TOP = 150;
	private static final int DISPLAY_SIZE = 290;
	
	private static final Item[] HAIR_STYLES = {new Item(ItemType.HAIR1)};
	private static final Item[] FACE_STYLES = {new Item(ItemType.FACE1)};
	private static final Item BODY = new Item(ItemType.BODY);
	private static final Item SHIRT = new Item(ItemType.SHIRT_BASIC);
	private static final Item PANTS = new Item(ItemType.PANTS_BASIC);
	private static final Item BOOTS = new Item(ItemType.BOOTS_BASIC);
	
	private static final Color[] HAIR_COLORS = {new Color(39 / 255f, 28 / 255f, 3 / 255f, 1), new Color(0.627f, 0.412f, 0.071f, 1), new Color(0.843f, 0.824f, 0.275f, 1)};
	private static final Color[] EYE_COLORS = {new Color(0.0549f,0.608f,0.819f,1), Color.BROWN, Color.RED, new Color(0.09f,0.784f,0.125f,1)};
	private static final Color[] BODY_COLORS = {new Color(251 / 255f, 222 / 255f, 136 / 255f, 1), new Color(0.7f, 0.5f, 0.08f, 1), new Color(249 / 255f, 240 / 255f, 138 / 255f, 1)};
	//private static final Color[] SHIRT_COLORS = {new Color(1, 1, 1, 1)};
	//private static final Color[] PANTS_COLORS = {new Color(1, 1, 1, 1)};
	
	Stage stage;	
	Stage stageWindow;
	TextButton changeHairLeftButton;
	TextButton changeHairRightButton;
	TextButton changeFaceLeftButton;
	TextButton changeFaceRightButton;
	
	Label titleLabel;
	CharacterDisplay characterDisplay;
	
	TextField nameTextField;
	
	TextButton finishButton;
	
	Button hairColorButton;
	Button eyeColorButton;
	Button bodyColorButton;
	Button pantsColorButton;
	Button shirtColorButton;
	
	ImageButton backButton;
	
	int selectedHair = 0;
	int selectedFace = 0;
	
	int hairColor;
	int eyeColor;
	int bodyColor;
	//int shirtColor;
	//int pantsColor;
	
	ScrollingBackground scrollingBackground;
	
	ColorPicker colorPickerWindow = null;//Not null if a window is open
	
	public CharacterCreatorScreen () {
		super(ScreenType.CHARACTER_CREATOR);
	}

	@Override
	public void create () {
		stage = new Stage(ArchipeloClient.getGame().getCameraUi().getScreenViewport(), ArchipeloClient.getGame().getBatch());
		stageWindow = new Stage(ArchipeloClient.getGame().getCameraUi().getScreenViewport(), ArchipeloClient.getGame().getBatch());
		scrollingBackground = new ScrollingBackground();
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
		
		Gdx.input.setInputProcessor(new InputMultiplexer(stageWindow, stage));//Put stageWindow first so it has priority
		
		titleLabel = new Label("Create Character", ArchipeloClient.getGame().getUiSkin(), "menu-title");
		stage.addActor(titleLabel);
		
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
				
				ColorPicker colorPicker = new ColorPicker(LM.getMsg(Cat.UI, "hair"), hairColor, HAIR_COLORS, new ColorPickListener() {
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
				
				ColorPicker colorPicker = new ColorPicker(LM.getMsg(Cat.UI, "eye"), eyeColor, EYE_COLORS, new ColorPickListener() {
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
				
				ColorPicker colorPicker = new ColorPicker(LM.getMsg(Cat.UI, "skin"), bodyColor, BODY_COLORS, new ColorPickListener() {
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
		/*shirtColorButton = new Button(ArchipeloClient.getGame().getUiSkin());
		shirtColorButton.setBounds(0, 0, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		shirtColorButton.setTouchable(Touchable.disabled);
		shirtColorButton.addListener(new ClickListener() {
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
		});
		stage.addActor(shirtColorButton);*/
		
		//Pants
		/*pantsColorButton = new Button(ArchipeloClient.getGame().getUiSkin());
		pantsColorButton.setBounds(0, 0, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		pantsColorButton.setTouchable(Touchable.disabled);
		pantsColorButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (colorPickerWindow != null)//If a color picker window is already open, don't open a new one
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
		});
		stage.addActor(pantsColorButton);*/
		
		//Finish
		finishButton = new TextButton(LM.getMsg(Cat.UI, "playGame"), ArchipeloClient.getGame().getUiSkin(), "large");
		finishButton.setBounds(0, 0, 300, 60);
		finishButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (nameTextField.getText().equals("")) {
					QuickUi.showErrorWindow(LM.getMsg(Cat.ERROR, "nameEmptyTitle"), LM.getMsg(Cat.ERROR, "nameEmpty"), stage);
					return;
				} else if (!StringValidator.isStringValid(nameTextField.getText(), StringValidator.USERNAME, StringValidator.MAX_USERNAME_LENGTH)) {
					QuickUi.showErrorWindow(LM.getMsg(Cat.ERROR, "nameInvalidTitle"), LM.getMsg(Cat.ERROR, "nameInvalid"), stage);
					return;
				}
				
				new PlayerPickPacket(nameTextField.getText(), selectedHair, selectedFace, hairColor, eyeColor, bodyColor).send();
				super.clicked(event, x, y);
			}
		});
		stage.addActor(finishButton);
		
		//Character display
		characterDisplay = new CharacterDisplay(getEquppedInventory(), true);
		characterDisplay.setBounds(0, 0, DISPLAY_SIZE, DISPLAY_SIZE);
		stage.addActor(characterDisplay);
		
		nameTextField = new TextField("", ArchipeloClient.getGame().getUiSkin());
		nameTextField.setMaxLength(StringValidator.MAX_USERNAME_LENGTH);
		nameTextField.setBounds(0, 0, DISPLAY_SIZE, nameTextField.getHeight());
		nameTextField.setMessageText(LM.ui("name"));
		QuickUi.makeTextFieldMobileCompatible(LM.ui("name"), nameTextField, stage, new TextFieldMessageListener() {
			
			@Override
			public void messageReceived (String message, boolean isEmpty) {
				nameTextField.setText(message);
			}
		});
		stage.addActor(nameTextField);
		
		backButton = QuickUi.getIconButton(IconType.BACK);
		backButton.addListener(new ClickListener () {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				ArchipeloClient.getGame().getScreenManager().setScreen(new CharacterPickerScreen(ArchipeloClient.getGame().getPrefs().getEmail()));
			}
		});
		stage.addActor(backButton);
		
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
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
		scrollingBackground.update(deltaTime);
		
		characterDisplay.setEquippedInventory(getEquppedInventory());
	}
	
	@Override
	public void render (SpriteBatch batch, float width, float height) {
		scrollingBackground.render(batch);
	}

	@Override
	public void renderUi (SpriteBatch batch, float width, float height) {
		batch.end();
		stage.draw();
		batch.begin();
		
		/////////////Draw display items////////////
		//Draw hair
		batch.setColor(HAIR_COLORS[hairColor]);
		batch.draw(HAIR_STYLES[selectedHair].getType().getWalkFrame(Direction.DOWN, 0, 0, 0), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		
		//Draw face
		batch.setColor(1, 1, 1, 1);
		batch.draw(FACE_STYLES[selectedFace].getType().getWalkFrame(Direction.DOWN, 0, 0, 0), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE), PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		batch.setColor(EYE_COLORS[eyeColor]);
		batch.draw(FACE_STYLES[selectedFace].getType().getWalkFrame(Direction.DOWN, 0, 1, 0), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE), PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		batch.setColor(HAIR_COLORS[hairColor]);
		batch.draw(FACE_STYLES[selectedFace].getType().getWalkFrame(Direction.DOWN, 0, 2, 0), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE), PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		
		//Draw body
		batch.setColor(BODY_COLORS[bodyColor]);
		batch.draw(BODY.getType().getWalkFrame(Direction.DOWN, 0, 0, 0), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE) * 2, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		
		//Draw shirt
		//batch.setColor(SHIRT_COLORS[shirtColor]);
		/*batch.setColor(1, 1, 1, 1);
		batch.draw(SHIRT.getType().getWalkFrame(Direction.DOWN, 0), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE) * 3, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		
		//Draw pants
		//batch.setColor(PANTS_COLORS[pantsColor]);
		batch.setColor(1, 1, 1, 1);
		batch.draw(PANTS.getType().getWalkFrame(Direction.DOWN, 0), width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE) * 4, PART_BUTTON_SIZE, PART_BUTTON_SIZE);
		*/

		batch.setColor(1, 1, 1, 1);
		batch.end();
		stageWindow.draw();
		batch.begin();
	}

	@Override
	public void resize (int width, int height) {
		titleLabel.setPosition(width / 2 - titleLabel.getWidth() / 2, height - titleLabel.getHeight() - 40);
		
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
		//shirtColorButton.setPosition(width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE) * 3);
		
		//Pants
		//pantsColorButton.setPosition(width - ARROW_BUTTON_WIDTH - PADDING - PART_BUTTON_SIZE - PADDING, height - PADDING_FROM_TOP - (PADDING_FROM_EACH_OTHER + PART_BUTTON_SIZE) * 4);
		
		//Finish
		finishButton.setPosition(width - PADDING_FROM_EACH_OTHER - finishButton.getWidth(), PADDING_FROM_EACH_OTHER);
		
		//Change direction
		characterDisplay.setPosition(width / 2 - characterDisplay.getWidth() / 2, height / 2 - characterDisplay.getHeight() / 2);
		
		//Name Textfield
		nameTextField.setPosition(width / 2 - nameTextField.getWidth() / 2, height / 2 - characterDisplay.getHeight() / 2 - nameTextField.getHeight() - 5);
		
		//Back button
		backButton.setPosition(5, Gdx.graphics.getHeight() - QuickUi.ICON_SIZE - 5);
		
		scrollingBackground.resize();
	}
	
	private Item[] getEquppedInventory () {
		BODY.color = Color.rgba8888(BODY_COLORS[bodyColor]);
		HAIR_STYLES[selectedHair].color = Color.rgba8888(HAIR_COLORS[hairColor]);
		FACE_STYLES[selectedFace].color = Color.rgba8888(EYE_COLORS[eyeColor]);
		return Player.createEquipInventory(BODY, BOOTS, PANTS, SHIRT, null, null, FACE_STYLES[selectedFace], HAIR_STYLES[selectedHair], null, null);
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
				QuickUi.showErrorWindow(LM.getMsg(Cat.ERROR, "nameTakenTitle"), LM.getMsg(Cat.ERROR, "nameTaken"), stage);
				break;
			case PlayerPickPacket.RESULT_INVALID_USERNAME:
				QuickUi.showErrorWindow(LM.getMsg(Cat.ERROR, "nameInvalidTitle"), LM.getMsg(Cat.ERROR, "nameInvalid"), stage);
				break;
			case PlayerPickPacket.RESULT_TOO_MANY_CHARACTERS:
				QuickUi.showErrorWindow(LM.getMsg(Cat.ERROR, "playerTooManyTitle"), LM.getMsg(Cat.ERROR, "playerTooMany"), stage);
				break;
			case PlayerPickPacket.RESULT_SUCCESSFUL:
				ArchipeloClient.getGame().getScreenManager().setScreen(new GameScreen(playerPickPacket.name));
				break;
			}
			
			return true;
		}
		return false;
	}

}
