package net.hollowbit.archipelo.screen.screens.gamescreen;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;

public class NpcDialogBox extends Window {
	
	public static final float LETTER_ADD_TIME = 0.05f;
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
	
	public NpcDialogBox (String npcName, String message, ArrayList<String> choices, ArrayList<String> choiceLinks) {
		super(npcName, ArchipeloClient.getGame().getUiSkin());
		
		this.setMovable(false);
		this.setBounds(this.getX(), this.getY(), 400, HEIGHT);
		
		this.message = message;
		this.choices = choices;
		this.choiceLinks = choiceLinks;
		
		messageLabel = new Label("", getSkin(), "small");
		messageLabel.setWrap(true);
		messageLabel.setAlignment(Align.left);
		add(messageLabel).width(WIDTH - PADDING * 2).pad(PADDING);
		updateSize();
		
		this.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!finishedAddingChars)
					setFinishedAddingChars();
				super.clicked(event, x, y);
			}
		});
	}
	
	@Override
	public void act (float delta) {
		//Add chars to label
		if (!this.finishedAddingChars) {
			timer += delta;
			if (timer >= LETTER_ADD_TIME) {
				timer -= LETTER_ADD_TIME;
				charsAdded++;
				
				//Update label
				messageLabel.setText(message.substring(0, charsAdded));
				updateSize();
				
				//See if done adding letters
				if (charsAdded >= message.length())
					setFinishedAddingChars();
			}
		}
		
		super.act(delta);
	}
	
	public void setFinishedAddingChars () {
		this.finishedAddingChars = true;
		charsAdded = message.length();
		messageLabel.setText(message.substring(0, charsAdded));
		
		//Add choices
		int size = this.getCells().size;
		row();
		for (int i = 0; i < choices.size(); i++) {
			final String id = choiceLinks.get(i);
			
			TextButton choice = new TextButton(choices.get(i), getSkin());
			choice.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println(id);
					super.clicked(event, x, y);
				}
			});
			add(choice).left();
			size++;
			this.getCells().get(size - 1).expand(true, false);
			
			row();
		}
		
		updateSize();
	}
	
	public void updateSize () {
		pack();
		if (this.getHeight() <= HEIGHT)
			this.setBounds(this.getX(), this.getY(), WIDTH, HEIGHT);
	}
	
}
