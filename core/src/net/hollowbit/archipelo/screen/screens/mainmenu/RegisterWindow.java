package net.hollowbit.archipelo.screen.screens.mainmenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerConnectivity;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerQueryResponseHandler;
import net.hollowbit.archipelo.tools.QuickUi;
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
		emailFld.setMaxLength(StringValidator.MAX_EMAIL_LENGTH);
		add(emailFld).width(400).pad(10).colspan(2);
		row();
		
		passwordFld = new TextField("", getSkin());
		passwordFld.setPasswordCharacter('*');
		passwordFld.setMaxLength(StringValidator.MAX_PASSWORD_LENGTH);
		passwordFld.setPasswordMode(true);
		passwordFld.setMessageText("Password");
		add(passwordFld).width(400).pad(10).colspan(2);
		row();
		
		remPasswordFld = new TextField("", getSkin());
		remPasswordFld.setPasswordCharacter('*');
		remPasswordFld.setMaxLength(StringValidator.MAX_PASSWORD_LENGTH);
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
					QuickUi.showErrorWindow("Fields Empty", "Please don't leave fields blank!", getStage());
				} else if (!StringValidator.isEmailValid(emailFld.getText()) || emailFld.getText().contains(" ")) {
					QuickUi.showErrorWindow("Invalid Email", "Please enter a valid email.", getStage());
				} else if (!StringValidator.isStringValid(passwordFld.getText(), StringValidator.PASSWORD, StringValidator.MAX_PASSWORD_LENGTH)) {
					QuickUi.showErrorWindow("Invalid Password", "Please only use a-zA-Z0-9 and !@#$%^&*()-_+= for passwords.", getStage());
				} else if (!remPasswordFld.getText().equals(passwordFld.getText())) {
					QuickUi.showErrorWindow("Confirm Password", "Confirm password does not match password.", getStage());
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
				switch (id) {
				case HollowBitServerConnectivity.TEMP_BAN_RESPONSE_PACKET_ID://Ban
					QuickUi.showErrorWindow("You Are Banned!", data[0], getStage());
					break;
				case HollowBitServerConnectivity.USER_ALREADY_EXISTS_ERROR_RESPONSE_PACKET_ID://User already exists
					QuickUi.showErrorWindow("User Already Exists", "User with this email already exist.", getStage());
					break;
				case HollowBitServerConnectivity.INVALID_EMAIL_RESPONSE_PACKET_ID://Invalid email
					QuickUi.showErrorWindow("Invalid Email", "Please enter a valid email.", getStage());
					break;
				case HollowBitServerConnectivity.INVALID_PASSWORD_RESPONSE_PACKET_ID://Invalid password
					QuickUi.showErrorWindow("Invalid Password", "Please only use a-zA-Z0-9 and !@#$%^&*()-_+= for passwords.", getStage());
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

}

