package net.hollowbit.archipelo.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;

import net.hollowbit.archipelo.ArchipeloClient;

public class QuickUi {
	
	public static void addCloseButtonToWindow (final Window window) {
		TextButton closeButton = new TextButton("X", ArchipeloClient.getGame().getUiSkin());
		closeButton.addListener(new ClickListener () {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				window.remove();
				super.clicked(event, x, y);
			}
		});
		window.getTitleTable().add(closeButton);
	}
	
	public static void makeTextFieldMobileCompatible (final String usage, final TextField textField, final Stage stage, final TextFieldMessageListener listener) {
		textField.addListener(new FocusListener() {
			
			@Override
			public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				if (actor == textField) {
					if (focused) {
						textField.setText("");
						if (ArchipeloClient.IS_MOBILE) {
							Gdx.input.getTextInput(new TextInputListener() {
								
								@Override
								public void input (String text) {//When the mobile user finishes entering a text, send it
									listener.messageReceived(text);
									stage.setKeyboardFocus(null);
								}
								
								@Override
								public void canceled () {
									stage.setKeyboardFocus(null);
								}//No event for canceled
							}, "Enter a message for: " + usage, "", "Write here!");
						}
					} else {
						if (ArchipeloClient.IS_MOBILE) {
							textField.setText("Tap to " + usage + "!");
						} else {
							//When unfocused, put back hint message if textfield is empty
							if (isTextFieldEmpty(textField))
								textField.setText("Click to " + usage + "!");
						}
					}
				}
				super.keyboardFocusChanged(event, actor, focused);
			}
			
		});
	}
	
	public static boolean isTextFieldEmpty (TextField textField) {
		return isMessageEmpty(textField.getText());
	}
	
	public static boolean isMessageEmpty (String message) {
		message = message.trim();
		return message.equals("") || message.equals(".") || message.equals("/") || message.equals("!");
	}
	
	public interface TextFieldMessageListener {
		public abstract void messageReceived (String message);
	}
	
}
