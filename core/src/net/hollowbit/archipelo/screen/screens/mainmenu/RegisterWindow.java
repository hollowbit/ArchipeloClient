package net.hollowbit.archipelo.screen.screens.mainmenu;

import com.badlogic.gdx.Input.Keys;
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
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerConnectivity;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerQueryResponseHandler;
import net.hollowbit.archipeloshared.StringValidator;

public class RegisterWindow extends Window {
	
	//Ui
	TextButton loginBtn;
	TextButton cancelBtn;
	TextField emailFld;
	TextField passwordFld;
	TextField remPasswordFld;
	CheckBox rememberChkBx;
	
	String password = "", email = "";
	
	public RegisterWindow(final LoginRegisterWindow loginRegisterWindow, Stage stage) {
		super("Register", ArchipeloClient.getGame().getUiSkin());
		this.setStage(stage);
		this.setMovable(false);
		
		//Load ui elements
		Label instructionsLabel = new Label("Register a new account.", getSkin(), "small");
		instructionsLabel.setWrap(true);
		instructionsLabel.setAlignment(Align.center);
		add(instructionsLabel).width(400).pad(10).colspan(2);
		row();
		
		emailFld = new TextField("", getSkin());
		emailFld.setMessageText("Email");
		add(emailFld).width(400).pad(10).colspan(2);
		row();
		
		passwordFld = new TextField("", getSkin());
		passwordFld.setPasswordCharacter('*');
		passwordFld.setPasswordMode(true);
		passwordFld.setMessageText("Password");
		add(passwordFld).width(400).pad(10).colspan(2);
		row();
		
		remPasswordFld = new TextField("", getSkin());
		remPasswordFld.setPasswordCharacter('*');
		remPasswordFld.setPasswordMode(true);
		remPasswordFld.setMessageText("Confirm Password");
		add(remPasswordFld).width(400).pad(10).colspan(2);
		row();
		
		rememberChkBx = new CheckBox(" Remember?", getSkin());
		rememberChkBx.setChecked(ArchipeloClient.getGame().getPrefs().isLoggedIn());
		add(rememberChkBx).pad(10);
		row();
		
		loginBtn = new TextButton("Login", getSkin());
		loginBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (emailFld.getText().equals("") || passwordFld.getText().equals("") || remPasswordFld.getText().equals("")) {
					showErrorWindow("Please don't leave fields blank!");
				} else if (!StringValidator.isEmailValid(emailFld.getText())) {
					showErrorWindow("Please enter a valid email.");
				} else if (!StringValidator.isStringValid(passwordFld.getText(), StringValidator.PASSWORD)) {
					showErrorWindow("Please only use a-zA-Z0-9 and !@#$%^&*()-_+= for passwords.");
				} else if (!remPasswordFld.getText().equals(passwordFld.getText())) {
					showErrorWindow("Confirm password does not match password.");
				} else {
					//Put data in variables for later and send login packet
					email = emailFld.getText();
					password = passwordFld.getText();
					
					ArchipeloClient.getGame().getHollowBitServerConnectivity().sendCreateQuery(email, password, getHollowBitServerQueryResponseHandler());
				}
				super.clicked(event, x, y);
			}
			
		});
		add(loginBtn).pad(10);
		
		cancelBtn = new TextButton("Cancel", getSkin());
		cancelBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//Remove from stage
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
				System.out.println("RegisterWindow.java " + id);
				switch (id) {
				case HollowBitServerConnectivity.TEMP_BAN_RESPONSE_PACKET_ID://Ban
					showErrorWindow(data[0]);
					break;
				case HollowBitServerConnectivity.USER_ALREADY_EXISTS_ERROR_RESPONSE_PACKET_ID://User already exists
					showErrorWindow("User with this email already exist.");
					break;
				case HollowBitServerConnectivity.INVALID_EMAIL_RESPONSE_PACKET_ID://Invalid email
					showErrorWindow("Please enter a valid email.");
					break;
				case HollowBitServerConnectivity.INVALID_PASSWORD_RESPONSE_PACKET_ID://Invalid password
					showErrorWindow("Please only use a-zA-Z0-9 and !@#$%^&*()-_+= for passwords.");
					break;
				case HollowBitServerConnectivity.CREATE_SUCCESSFUL_RESPONSE_PACKET_ID://Creation successful
					if (rememberChkBx.isChecked())//Save login settings
						ArchipeloClient.getGame().getPrefs().setLogin(email, password);
					else
						ArchipeloClient.getGame().getPrefs().resetLogin();
					
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
			case LoginPacket.RESULT_INVALID_USERNAME:
				showErrorWindow("Please only use a-zA-Z0-9 and _ for usernames.");
				return true;
			case LoginPacket.RESULT_INVALID_PASSWORD:
				showErrorWindow("Please only use a-zA-Z0-9 and !@#$%^&*()-_+= for passwords.");
				return true;
			case LoginPacket.RESULT_USERNAME_TAKEN:
				showErrorWindow("Username is taken!");
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

