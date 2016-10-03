package net.hollowbit.archipelo.screen.screens.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerQueryResponseHandler;
import net.hollowbit.archipeloshared.StringValidator;

public class LoginWindow extends Window {
	
	//Ui
	Label usernameLbl;
	Label passwordLbl;
	TextButton loginBtn;
	TextButton cancelBtn;
	TextField usernameFld;
	TextField passwordFld;
	CheckBox rememberChkBx;
	
	String username = "", password = "";
	
	Preferences prefs = Gdx.app.getPreferences("Archipelo");
	
	public LoginWindow (final LoginRegisterWindow loginRegisterWindow, Stage stage) {
		super("Login", ArchipeloClient.getGame().getUiSkin());
		this.setStage(stage);
		this.setMovable(false);
		
		//Load ui elements
		Label instructionsLabel = new Label("Login to an existing account.", getSkin(), "small");
		instructionsLabel.setWrap(true);
		instructionsLabel.setAlignment(Align.center);
		add(instructionsLabel).width(400).pad(10).colspan(2);
		row();
		
		usernameLbl = new Label("Username: ", getSkin());
		add(usernameLbl).width(usernameLbl.getWidth());
		usernameFld = new TextField(prefs.getString("username", ""), getSkin());
		stage.setKeyboardFocus(usernameFld);
		add(usernameFld).pad(10);
		row();
		
		passwordLbl = new Label("Password: ", getSkin());
		add(passwordLbl).width(passwordLbl.getWidth());
		passwordFld = new TextField(prefs.getString("password", ""), getSkin());
		passwordFld.setPasswordCharacter('*');
		passwordFld.setPasswordMode(true);
		add(passwordFld).pad(10);
		row();
		
		rememberChkBx = new CheckBox(" Remember?", getSkin());
		rememberChkBx.setChecked(prefs.getBoolean("remember", false));
		add(rememberChkBx).pad(10);
		row();
		
		loginBtn = new TextButton("Login", getSkin());
		loginBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (usernameFld.getText().equals("") || passwordFld.getText().equals("")) {
					showErrorWindow("Please don't leave fields blank!");
				} else if (!StringValidator.isStringValid(usernameFld.getText(), StringValidator.USERNAME)) {
					showErrorWindow("Please only use a-zA-Z0-9 and _ for usernames.");
				} else if (!StringValidator.isStringValid(passwordFld.getText(), StringValidator.PASSWORD)) {
					showErrorWindow("Please only use a-zA-Z0-9 and !@#$%^&*()-_+= for passwords.");
				} else {
					//Put data in variables for later and send login packet
					username = usernameFld.getText();
					password = passwordFld.getText();
                	//ArchipeloClient.getGame().getNetworkManager().sendPacket(new LoginPacket(username, password, false));
					
					ArchipeloClient.getGame().getHollowBitServerConnectivity().sendVerifyQuery(username, password, getHollowBitServerQueryResponseHandler());
				}

				super.clicked(event, x, y);
			}
			
		});
		add(loginBtn).pad(10);
		
		cancelBtn = new TextButton("Cancel", getSkin());
		cancelBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//Remove from stage and from main menu
				loginRegisterWindow.setVisible(true);
				remove();
				super.clicked(event, x, y);
			}
			
		});
		add(cancelBtn).pad(10);
		
		pack();
	}
	
	private HollowBitServerQueryResponseHandler getHollowBitServerQueryResponseHandler () {
		return new HollowBitServerQueryResponseHandler() {
			
			@Override
			public void responseReceived(int id, String[] data) {
				switch (id) {
				case 5://Verify too fast
					showErrorWindow("You are verifying too fast. Wait " + data[0] + " before logins.");
					break;
				case 1://User doesn't exist
					showErrorWindow("User with this name doesn't exist.");
					break;
				case 2://Wrong password
					showErrorWindow("User exists but the password is incorrect.");
					break;
				case 13://Invalid username
					showErrorWindow("Please only use a-zA-Z0-9 and _ for usernames.");
					break;
				case 12://Invalid password
					showErrorWindow("Please only use a-zA-Z0-9 and !@#$%^&*()-_+= for passwords.");
					break;
				case 11://Invalid email
					showErrorWindow("Email address entered is invalid.");
					break;
				case 3://Correct login!
					if (rememberChkBx.isChecked()) {
						//Save login settings
						prefs.putString("username", username);
						prefs.putString("password", password);
						prefs.putBoolean("logged-in", true);
					} else {
						prefs.putString("username", "");
						prefs.putString("password", "");
						prefs.putBoolean("logged-in", false);
					}
					prefs.flush();
					
					ArchipeloClient.LOGGED_IN = true;
					ArchipeloClient.USERNAME = username;
					ArchipeloClient.PASSWORD = password;
					
					remove();
					break;
				}
			}
		};
	}
	
	/*@Override
	public boolean handlePacket(Packet packet) {
		if (packet.packetType == PacketType.LOGIN) {
			LoginPacket loginPacket = (LoginPacket) packet;
			switch (loginPacket.result) {
			case LoginPacket.RESULT_ALREADY_LOGGED_IN:
				showErrorWindow("You are already logged in!");
				return true;
			case LoginPacket.RESULT_NO_USER_WITH_NAME:
				showErrorWindow("There is no user with that name!");
				return true;
			case LoginPacket.RESULT_PASSWORD_WRONG:
				showErrorWindow("The entered password is incorrect for this user!");
				return true;
			case LoginPacket.RESULT_BAD_VERSION:
				showErrorWindow("You Archipelo version does not match the server's. Server version: " + loginPacket.version + "!");
				return true;
			case LoginPacket.RESULT_SUCCESS:
				if (rememberChkBx.isChecked()) {
					//Save login settings
					prefs.putString("username", username);
					prefs.putString("password", password);
					prefs.putBoolean("remember", true);
				} else {
					prefs.putString("username", "");
					prefs.putString("password", "");
					prefs.putBoolean("remember", false);
				}
				prefs.flush();
				
				ArchipeloClient.getGame().setUsername(username);
				
				if (loginPacket.hasCreatedPlayer)
					ArchipeloClient.getGame().getScreenManager().setScreen(new GameScreen());//Load gamescreen
				else
					ArchipeloClient.getGame().getScreenManager().setScreen(new PlayerCreatorScreen());
				return true;
			}
		}
		return false;
	}*/
	
	private void showErrorWindow (String error) {
		Dialog dialog = new Dialog("Login Error", getSkin(), "dialog") {
		    public void result(Object obj) {
		        remove();
		    }
		};
		dialog.text(error);
		dialog.button("Close", true);
		dialog.key(Keys.ENTER, true);
		dialog.key(Keys.ESCAPE, true);
		dialog.show(getStage());
	}

}
