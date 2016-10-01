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

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.LoginPacket;
import net.hollowbit.archipelo.screen.screens.GameScreen;
import net.hollowbit.archipelo.screen.screens.PlayerCreatorScreen;
import net.hollowbit.archipeloshared.StringValidator;

public class RegisterWindow extends Window implements PacketHandler {
	
	//Ui
	Label usernameLbl;
	Label passwordLbl;
	Label remPasswordLbl;
	TextButton loginBtn;
	TextButton cancelBtn;
	TextField usernameFld;
	TextField passwordFld;
	TextField remPasswordFld;
	CheckBox rememberChkBx;
	
	String username = "", password = "";
	
	Preferences prefs = Gdx.app.getPreferences("Archipelo");
	
	public RegisterWindow(Stage stage) {
		super("Register", ArchipeloClient.getGame().getUiSkin());
		this.setStage(stage);
		this.setMovable(false);
		
		ArchipeloClient.getGame().getNetworkManager().addPacketHandler(this);
		
		//Load ui elements
		usernameLbl = new Label("Username: ", getSkin());
		add(usernameLbl);
		usernameFld = new TextField("", getSkin());
		getStage().setKeyboardFocus(usernameFld);
		add(usernameFld);
		row();
		
		passwordLbl = new Label("Password: ", getSkin());
		add(passwordLbl);
		passwordFld = new TextField("", getSkin());
		passwordFld.setPasswordCharacter('*');
		passwordFld.setPasswordMode(true);
		add(passwordFld);
		row();
		
		remPasswordLbl = new Label("Confirm: ", getSkin());
		add(remPasswordLbl);
		remPasswordFld = new TextField("", getSkin());
		remPasswordFld.setPasswordCharacter('*');
		remPasswordFld.setPasswordMode(true);
		add(remPasswordFld);
		row();
		
		rememberChkBx = new CheckBox(" Remember?", getSkin());
		rememberChkBx.setChecked(prefs.getBoolean("remember", false));
		add(rememberChkBx);
		row();
		
		loginBtn = new TextButton("Login", getSkin());
		loginBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (usernameFld.getText().equals("") || passwordFld.getText().equals("") || remPasswordFld.getText().equals("")) {
					showErrorWindow("Please don't leave fields blank!");
				} else if (!StringValidator.isStringValid(usernameFld.getText(), StringValidator.USERNAME)) {
					showErrorWindow("Please only use a-zA-Z0-9 and _ for usernames.");
				} else if (!StringValidator.isStringValid(passwordFld.getText(), StringValidator.PASSWORD)) {
					showErrorWindow("Please only use a-zA-Z0-9 and !@#$%^&*()-_+= for passwords.");
				} else if (!remPasswordFld.getText().equals(passwordFld.getText())) {
					showErrorWindow("Confirm password does not match password.");
				} else {
					//Put data in variables for later and send login packet
					username = usernameFld.getText();
					password = passwordFld.getText();
	                ArchipeloClient.getGame().getNetworkManager().sendPacket(new LoginPacket(username, password, true));
				}
				super.clicked(event, x, y);
			}
			
		});
		add(loginBtn);
		
		cancelBtn = new TextButton("Cancel", getSkin());
		cancelBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//Remove from stage
				remove();
				super.clicked(event, x, y);
			}
			
		});
		add(cancelBtn);
		
		pack();
	}

	@Override
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
				
				ArchipeloClient.getGame().setUsername(username);
				
				if (loginPacket.hasCreatedPlayer)
					ArchipeloClient.getGame().getScreenManager().setScreen(new GameScreen());//Load gamescreen
				else
					ArchipeloClient.getGame().getScreenManager().setScreen(new PlayerCreatorScreen());
					
				return true;
			}
		}
		return false;
	}
	
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

