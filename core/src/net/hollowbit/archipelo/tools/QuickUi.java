package net.hollowbit.archipelo.tools;

import java.util.Date;

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
		
		dialog.button(LM.ui("close"), true);
		dialog.key(Keys.ENTER, true);
		dialog.key(Keys.ESCAPE, true);
		dialog.show(stage);
	}
	
	public static ImageButton getIconButton (IconType iconType) {
		return new ImageButton(iconType.getUpImage(), iconType.getUpImage());
	}
	
	public static void makeTextFieldMobileCompatible (final String usage, final TextField textField, final Stage stage, final TextFieldMessageListener listener) {
		if (ArchipeloClient.IS_MOBILE)
			textField.setMessageText(usage);
		else
			textField.setMessageText(usage);
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
							}, LM.ui("enterAMessageFor") + ": " + usage, (!isTextFieldEmpty(textField) ? textField.getText() : ""), (isTextFieldEmpty(textField) ? LM.ui("writeHere") : ""));
						}
					}
				}
				super.keyboardFocusChanged(event, actor, focused);
			}
			
		});
	}
	
	@SuppressWarnings("deprecation")
	public static String processMessageString (String message) {
		if (ArchipeloClient.getGame().getWorld() != null && ArchipeloClient.getGame().getWorld().getPlayer() != null)
			message.replaceAll("{n}", ArchipeloClient.getGame().getWorld().getPlayer().getName());
		
		message.replaceAll("{t}", new Date().toLocaleString());
		
		//Colors
		//message.replaceAll("&", "[" + Color. + "]");
		message.replaceAll("&h", "[CLEAR]");
		message.replaceAll("&5", "[BLACK]");
		message.replaceAll("&4", "[DARK_GRAY]");
		message.replaceAll("&3", "[GRAY]");
		message.replaceAll("&2", "[LIGHT_GRAY]");
		message.replaceAll("&1", "[WHITE]");
		
		message.replaceAll("&B", "[NAVY]");
		message.replaceAll("&b", "[.BLUE]");
		message.replaceAll("&C", "[TEAL]");
		message.replaceAll("&c", "[CYAN]");
		message.replaceAll("&R", "[FIREBRICK]");
		message.replaceAll("&r", "[RED]");
		message.replaceAll("&P", "[MAGENTA]");
		message.replaceAll("&p", "[PINK]");
		message.replaceAll("&V", "[VIOLET]");
		message.replaceAll("&v", "[ROYAL]");
		message.replaceAll("&Y", "[GOLDENROD]");
		message.replaceAll("&y", "[YELLOW]");
		message.replaceAll("&G", "[FOREST]");
		message.replaceAll("&g", "[GREEN]");
		message.replaceAll("&O", "[ORANGE]");
		message.replaceAll("&o", "[TAN]");
		
		message.replaceAll("&l", "[LIME]");
		message.replaceAll("&e", "[OLIVE]");
		message.replaceAll("&s", "[SCARLET]");
		message.replaceAll("&m", "[MAROON]");
		message.replaceAll("&c", "[CHARTREUSE]");
		message.replaceAll("&b", "[BROWN]");
		message.replaceAll("&n", "[SALMON]");
		message.replaceAll("&t", "[SLATE]");
		
		return message;
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
