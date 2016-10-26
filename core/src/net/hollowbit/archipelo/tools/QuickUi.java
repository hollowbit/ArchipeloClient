package net.hollowbit.archipelo.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;

public class QuickUi {
	
	public static final int ICON_SIZE = 100;
	public static final int ERROR_DIALOG_WRAP_WIDTH = 500;
	
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
	
	public static void showErrorWindow (String title, String error, Stage stage) {
		Dialog dialog = new Dialog(title, ArchipeloClient.getGame().getUiSkin(), "dialog") {
		    public void result(Object obj) {
		        remove();
		    }
		};
		
		Label label = new Label(error, ArchipeloClient.getGame().getUiSkin());
		label.setWrap(true);
		label.setAlignment(Align.center);
		dialog.getContentTable().add(label).width(ERROR_DIALOG_WRAP_WIDTH);
		
		dialog.button("Close", true);
		dialog.key(Keys.ENTER, true);
		dialog.key(Keys.ESCAPE, true);
		dialog.show(stage);
	}
	
	public static ImageButton getIconButton (IconType iconType) {
		return new ImageButton(iconType.getUpImage(), iconType.getUpImage());
	}
	
	public static void makeTextFieldMobileCompatible (final String usage, final TextField textField, final Stage stage, final TextFieldMessageListener listener) {
		if (ArchipeloClient.IS_MOBILE)
			textField.setMessageText("Tap to " + usage + " !");
		else
			textField.setMessageText("Click to " + usage + " !");
		textField.addListener(new FocusListener() {
			
			@Override
			public void keyboardFocusChanged (FocusEvent event, Actor actor, boolean focused) {
				if (actor == textField) {
					if (focused) {
						if (ArchipeloClient.IS_MOBILE) {
							Gdx.input.getTextInput(new TextInputListener() {
								
								@Override
								public void input (String text) {//When the mobile user finishes entering a text, send it
									listener.messageReceived(text, isMessageEmpty(text));
									stage.setKeyboardFocus(null);
								}
								
								@Override
								public void canceled () {
									stage.setKeyboardFocus(null);
								}//No event for canceled
							}, "Enter a message for: " + usage, "", "Write here!");
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
		message = message.replaceAll(" ", "");
		return message.equals("") || message.equals(".") || message.equals("/") || message.equals("!");
	}
	
	public interface TextFieldMessageListener {
		public abstract void messageReceived (String message, boolean isEmpty);
	}
	
	public enum IconType {
		HOME(0),
		CHAT(1),
		BACK(2);
		
		int row = 0;
		
		private IconType (int row) {
			this.row = row;
		}
		
		public TextureRegionDrawable getUpImage () {
			return new TextureRegionDrawable(ArchipeloClient.getGame().getAssetManager().getTextureMap("icons")[row][0]);
		}
		
		public TextureRegionDrawable getDownImage () {
			return new TextureRegionDrawable(ArchipeloClient.getGame().getAssetManager().getTextureMap("icons")[row][1]);
		}
		
	}
	
}
