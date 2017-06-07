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
import net.hollowbit.archipelo.tools.LanguageSpecificMessageManager.Cat;

public class QuickUi {
	
	public static final int ICON_SIZE = 100;
	public static final int ERROR_DIALOG_WRAP_WIDTH = 500;
	
	public static void addCloseButtonToWindow (final Window window) {
		TextButton closeButton = new TextButton("x", ArchipeloClient.getGame().getUiSkin(), "small");
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
		return new ImageButton(iconType.getUpImage(), iconType.getDownImage());
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
		if (message == null || message.equals(""))
			return "";
		
		String newMessage = "";
		for (int i = 0; i < message.length(); i++) {
			char ch = message.charAt(i);
			
			if (ch == '{') {
				String id = "";
				ch = message.charAt(++i);
				while (ch != '}' && i < message.length()) {
					id += ch;
					ch = message.charAt(++i);
				}
				
				newMessage += LM.getMsg(Cat.CHAT, id);
			} else
				newMessage += ch;
		}
		message = newMessage;
		
		if (ArchipeloClient.getGame().getWorld() != null && ArchipeloClient.getGame().getWorld().getPlayer() != null)
			message = message.replace("[n]", ArchipeloClient.getGame().getWorld().getPlayer().getName());
		
		message = message.replace("[t]", new Date().toLocaleString());
		
		//Colors
		//message.replaceAll("&", "[" + Color. + "]");
		message = message.replace("&h", "[CLEAR]");
		message = message.replace("&5", "[BLACK]");
		message = message.replace("&4", "[DARK_GRAY]");
		message = message.replace("&3", "[GRAY]");
		message = message.replace("&2", "[LIGHT_GRAY]");
		message = message.replace("&1", "[WHITE]");
		
		message = message.replace("&B", "[NAVY]");
		message = message.replace("&b", "[BLUE]");
		message = message.replace("&C", "[TEAL]");
		message = message.replace("&c", "[CYAN]");
		message = message.replace("&R", "[FIREBRICK]");
		message = message.replace("&r", "[RED]");
		message = message.replace("&P", "[MAGENTA]");
		message = message.replace("&p", "[PINK]");
		message = message.replace("&V", "[VIOLET]");
		message = message.replace("&v", "[ROYAL]");
		message = message.replace("&Y", "[GOLDENROD]");
		message = message.replace("&y", "[YELLOW]");
		message = message.replace("&G", "[FOREST]");
		message = message.replace("&g", "[GREEN]");
		message = message.replace("&O", "[ORANGE]");
		message = message.replace("&o", "[TAN]");
		
		message = message.replace("&l", "[LIME]");
		message = message.replace("&e", "[OLIVE]");
		message = message.replace("&s", "[SCARLET]");
		message = message.replace("&m", "[MAROON]");
		message = message.replace("&c", "[CHARTREUSE]");
		message = message.replace("&b", "[BROWN]");
		message = message.replace("&n", "[SALMON]");
		message = message.replace("&t", "[SLATE]");
		message = message.replace("&d", "[GOLD]");
		
		return message;
	}
	
	public static String stripAllNewlinesAndTabs (String message) {
		if (message == null || message.equals(""))
			return "";
		
		message.replace("\n", "");
		message.replace("\t", "");
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
		BACK(2),
		INVENTORY(3);
		
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
