package net.hollowbit.archipelo.screen.screens.gamescreen.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.form.MobileCompatibleWindow;
import net.hollowbit.archipelo.screen.screens.gamescreen.ChatListener;
import net.hollowbit.archipelo.screen.screens.gamescreen.ChatManager;
import net.hollowbit.archipelo.tools.ControlsManager;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipelo.tools.QuickUi;
import net.hollowbit.archipelo.tools.QuickUi.TextFieldMessageListener;

public class ChatWindow extends MobileCompatibleWindow implements ChatListener {
	
	//Ui elements
	TextButton closeButton;
	ScrollPane chatPane;
	Table chatTable;
	TextField chatTextField;
	TextButton sendButton;
	
	ChatManager chatManager;
	
	float heightOfMessages = 0;
	
	public ChatWindow (final ChatManager chatManager, Stage stage, ControlsManager controlsManager) {
		super(LM.ui("chat"), ArchipeloClient.getGame().getUiSkin(), 1);
		this.chatManager = chatManager;
		this.setStage(stage);
		chatTable = new Table();
		
		chatPane = new ScrollPane(chatTable, getSkin());
		chatPane.setFadeScrollBars(false);
		add(chatPane).grow().colspan(2).pad(3).height(450);
		
		row();
		
		chatTextField = new TextField("", getSkin());
		chatTextField.setMaxLength(140);//Like Twitter!
		QuickUi.makeTextFieldMobileCompatible(LM.ui("chat"), chatTextField, getStage(), new TextFieldMessageListener() {
			
			@Override
			public void messageReceived (String message, boolean isEmpty) {
				if (!isEmpty) {
					chatManager.sendMessage(message);
					chatTextField.setText("");
				}
			}
		});
		
		//Change width depending if on mobile or not
		float width = 380;
		if (ArchipeloClient.IS_MOBILE)
			width = 700;
		add(chatTextField).width(width).height(40).pad(3);
		
		if (!ArchipeloClient.IS_MOBILE)//If not on mobile, set focus to chat text field
			stage.setKeyboardFocus(chatTextField);
		
		sendButton = new TextButton(LM.ui("send"), getSkin());
		sendButton.addListener(new ClickListener () {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!chatTextField.getText().equals("") && !chatTextField.getText().equals(".") && !chatTextField.getText().equals("/") && !chatTextField.getText().equals("!")) {
					chatManager.sendMessage(chatTextField.getText());
					chatTextField.setText("");
					super.clicked(event, x, y);
				}
			}
		});
		add(sendButton).width(100).height(40).pad(3);
		
		QuickUi.addCloseButtonToWindow(this);//Adds a close button to the window
		
		chatManager.addChatListener(this);
		
		//Add messages already in chat manager
		for (ChatMessage message : chatManager.getChatMessages())
			newChatMessageReceived(message);
		
		this.pack();
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		//If the user presses the enter key, send the message
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)){
			if (!chatTextField.getText().equals("") && !chatTextField.getText().equals(".") && !chatTextField.getText().equals("/") && !chatTextField.getText().equals("!")) {
				chatManager.sendMessage(chatTextField.getText());
				chatTextField.setText("");
			}
		}
		chatPane.act(delta);
	}
	
	@Override
	public boolean remove() {
		chatManager.removeChatListener(this);
		return super.remove();
	}
	
	//When a new message is received, add it to the table
	@Override
	public void newChatMessageReceived (ChatMessage chatMessage) {
		Label messageLabel = new Label(chatMessage.message, getSkin(), "small");
		messageLabel.setWrap(true);
		messageLabel.setAlignment(Align.left);

		int size = chatTable.getCells().size;
		if (size > 0)
			chatTable.getCells().get(size - 1).expand(true, false);

		boolean moveToBottom = chatPane.getScrollY() >= chatPane.getMaxY() - 10;
		
		//Add message to table
		chatTable.row();
		chatTable.add(messageLabel).expandY().growX().top().left();
		
		//Update chatPane so that getMaxY is updated
		chatPane.layout();
		
		//Adjust scoll if scroll is at bottom, otherwise, don't.
		if (moveToBottom)
			chatPane.setScrollY(chatPane.getMaxY());
		chatPane.updateVisualScroll();
	}

}
