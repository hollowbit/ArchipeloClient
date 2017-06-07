package net.hollowbit.archipelo.screen.screens.gamescreen.windows;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.form.MobileCompatibleWindow;
import net.hollowbit.archipelo.network.packets.NpcDialogPacket;
import net.hollowbit.archipelo.network.packets.NpcDialogRequestPacket;
import net.hollowbit.archipelo.screen.screens.GameScreen;
import net.hollowbit.archipelo.tools.QuickUi;
import net.hollowbit.archipelo.tools.npcdialogs.NpcDialog;

public class NpcDialogBox extends MobileCompatibleWindow {
	
	public static final float LETTER_ADD_TIME = 0.03f;
	public static final int WIDTH = 400;
	public static final int HEIGHT = 150;
	public static final int PADDING = 10;
	
	private String message;
	private ArrayList<String> choices;
	private ArrayList<String> choiceLinks;
	private Label messageLabel;
	
	private float timer = 0;
	private int charsAdded = 0;
	private boolean finishedAddingChars = false;
	
	private boolean interruptable = false;
	
	private boolean usesId = false;
	
	private ArrayList<String> messages;//Only used for multi-message, non id dialogs
	private int messageIndex = -1;
	
	public NpcDialogBox (String name, NpcDialogPacket packet) {
		super(name, ArchipeloClient.getGame().getUiSkin(), 0.5f);
		
		this.setMovable(false);
		this.setBounds(this.getX(), this.getY(), WIDTH, HEIGHT);
		
		this.usesId = packet.usesId;
		if (this.usesId) {
			NpcDialog npcDialog = ArchipeloClient.getGame().getLanguageSpecificMessageManager().getNpcDialogById(packet.prefix, packet.name);
			this.message = QuickUi.processMessageString(npcDialog.message);
			this.choices = npcDialog.choices;
			this.choiceLinks = packet.messages;
		} else {
			this.messages = packet.messages;
			this.setName(packet.name);
			
			getNextMessage();
		}
		this.interruptable = packet.interruptable;
		
		messageLabel = new Label("", getSkin(), "small");
		messageLabel.setWrap(true);
		messageLabel.setAlignment(Align.left);
		add(messageLabel).width(WIDTH - PADDING * 2).pad(PADDING);
		updateSize();

		this.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				interactedWith();
				super.clicked(event, x, y);
			}
		});
		
		this.pack();
	}
	
	@Override
	public void pack () {
		super.pack();
		this.setY(GameScreen.NPC_DIALOG_BOX_HEIGHT);
		this.setBounds(this.getX(), this.getY(), WIDTH, HEIGHT);
	}
	
	/**
	 * Called when the window is clicked on or a key is pressed
	 */
	private void interactedWith () {
		if (!finishedAddingChars)
			setFinishedAddingChars();
		else {
			if (!usesId)
				getNextMessage();
			else {
				if (choiceLinks.size() < 1)
					this.remove();
				else if (choiceLinks.size() == 1)
					ArchipeloClient.getGame().getNetworkManager().sendPacket(new NpcDialogRequestPacket(choiceLinks.get(0)));
					
			}
		}
	}
	
	@Override
	public void act (float delta) {
		//Add chars to label
		if (!this.finishedAddingChars) {
			timer += delta;
			if (timer >= LETTER_ADD_TIME) {
				timer -= LETTER_ADD_TIME;
				charsAdded++;
				
				//Adds color markup in one shot
				if (message.charAt(charsAdded - 1) == '[') {
					do {
						charsAdded++;
					} while (charsAdded <= message.length() && message.charAt(charsAdded - 1) != ']');
				}
				
				//Update label
				messageLabel.setText(message.substring(0, charsAdded));
				updateSize();
				
				//See if done adding letters
				if (charsAdded >= message.length())
					setFinishedAddingChars();
			}
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.Z) || Gdx.input.isKeyJustPressed(Keys.X))
			interactedWith();
		
		super.act(delta);
	}
	
	public void setFinishedAddingChars () {
		this.finishedAddingChars = true;
		charsAdded = message.length();
		messageLabel.setText(message.substring(0, charsAdded));
		
		//Add choices
		if (usesId && choices != null && choices.size() > 0) {
			int size = this.getCells().size;
			row();
			for (int i = 0; i < choices.size(); i++) {
				final String id = choiceLinks.get(i);
				
				TextButton choice = new TextButton(choices.get(i), getSkin());
				choice.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						if (id.equals(""))//Some reponses could be blank so just end conversation
							remove();
						else
							ArchipeloClient.getGame().getNetworkManager().sendPacket(new NpcDialogRequestPacket(id));
						super.clicked(event, x, y);
					}
				});
				add(choice).left().pad(2);
				size++;
				this.getCells().get(size - 1).expand(true, false);
				
				row();
			}
		}
		
		updateSize();
	}
	
	public void updateSize () {
		pack();
		if (this.getHeight() <= HEIGHT)
			this.setBounds(this.getX(), this.getY(), WIDTH, HEIGHT);
	}
	
	/**
	 * Resets dialog box to use next message.
	 * Only for multi-message, non id npc dialogs
	 */
	private void getNextMessage () {
		this.charsAdded = 0;
		this.finishedAddingChars = false;
		
		this.messageIndex++;
		
		if (messageIndex >= messages.size())//If reached end of messages, close window
			this.remove();
		else {
			this.message = QuickUi.processMessageString(messages.get(messageIndex));
			
			messageLabel.setText(message.substring(0, charsAdded));
			this.updateSize();
		}
	}
	
	public boolean isInterruptable () {
		return this.interruptable;
	}
	
}
