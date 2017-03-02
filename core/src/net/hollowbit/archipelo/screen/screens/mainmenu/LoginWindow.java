package net.hollowbit.archipelo.screen.screens.mainmenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.form.MobileCompatibleWindow;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerConnectivity;
import net.hollowbit.archipelo.hollowbitserver.HollowBitServerQueryResponseHandler;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipelo.tools.Prefs;
import net.hollowbit.archipelo.tools.QuickUi;
import net.hollowbit.archipelo.tools.QuickUi.TextFieldMessageListener;
import net.hollowbit.archipeloshared.StringValidator;

public class LoginWindow extends MobileCompatibleWindow {
	
	//Ui
	TextButton loginBtn;
	TextButton cancelBtn;
	TextField emailFld;
	TextField passwordFld;
	CheckBox rememberChkBx;
	
	String email = "", password = "";
	Prefs prefs = ArchipeloClient.getGame().getPrefs();
	
	public LoginWindow (final LoginRegisterWindow loginRegisterWindow, Stage stage) {
		super(LM.ui("login"), ArchipeloClient.getGame().getUiSkin());
		this.setStage(stage);
		this.setMovable(false);
		
		//Load ui elements
		Label instructionsLabel = new Label(LM.ui("loginInstructions"), getSkin(), "small");
		instructionsLabel.setWrap(true);
		instructionsLabel.setAlignment(Align.center);
		add(instructionsLabel).width(400).pad(10).colspan(2);
		row();
		
		emailFld = new TextField(prefs.getEmail(), getSkin());
		emailFld.setMessageText(LM.ui("email"));
		emailFld.setMaxLength(StringValidator.MAX_EMAIL_LENGTH);
		QuickUi.makeTextFieldMobileCompatible(LM.ui("email"), emailFld, stage, new TextFieldMessageListener() {
			
			@Override
			public void messageReceived (String message, boolean isEmpty) {
				emailFld.setText(message);
			}
		});
		add(emailFld).width(400).pad(10).colspan(2);
		row();
		
		passwordFld = new TextField(prefs.getPassword(), getSkin());
		passwordFld.setPasswordCharacter('*');
		passwordFld.setMaxLength(StringValidator.MAX_PASSWORD_LENGTH);
		passwordFld.setPasswordMode(true);
		passwordFld.setMessageText(LM.ui("password"));
		QuickUi.makeTextFieldMobileCompatible(LM.ui("password"), passwordFld, stage, new TextFieldMessageListener() {
			
			@Override
			public void messageReceived (String message, boolean isEmpty) {
				passwordFld.setText(message);
			}
		});
		add(passwordFld).width(400).pad(10).colspan(2);
		row();
		
		rememberChkBx = new CheckBox(" " + LM.ui("remember"), getSkin());
		rememberChkBx.setChecked(prefs.isLoggedIn());
		add(rememberChkBx).pad(10);
		row();
		
		loginBtn = new TextButton(LM.ui("login"), getSkin());
		loginBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (emailFld.getText().equals("") || passwordFld.getText().equals("")) {
					QuickUi.showErrorWindow(LM.error("fieldsEmptyTitle"), LM.error("fieldsEmpty"), getStage());
				} else if (!StringValidator.isEmailValid(emailFld.getText()) || emailFld.getText().contains(" ")) {
					QuickUi.showErrorWindow(LM.error("loginHBInvalidEmailTitle"), LM.error("loginHBInvalidEmail"), getStage());
				} else if (!StringValidator.isStringValid(passwordFld.getText(), StringValidator.PASSWORD, StringValidator.MAX_PASSWORD_LENGTH)) {
					QuickUi.showErrorWindow(LM.error("loginInvalidPasswordTitle"), LM.error("loginInvalidPasswordTitle"), getStage());
				} else {
					//Put data in variables for later and send login packet
					email = emailFld.getText();
					password = passwordFld.getText();
                	//ArchipeloClient.getGame().getNetworkManager().sendPacket(new LoginPacket(username, password, false));
					
					ArchipeloClient.getGame().getHollowBitServerConnectivity().sendVerifyQuery(email, password, getHollowBitServerQueryResponseHandler());
				}

				super.clicked(event, x, y);
			}
			
		});
		add(loginBtn).pad(10);
		
		cancelBtn = new TextButton(LM.ui("cancel"), getSkin());
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
				case HollowBitServerConnectivity.TEMP_BAN_RESPONSE_PACKET_ID://Verify too fast
					QuickUi.showErrorWindow(LM.error("youAreBanned"), data[0], getStage());
					break;
				case HollowBitServerConnectivity.USER_DOESNT_EXIST_ERROR_RESPONSE_PACKET_ID://User doesn't exist
					QuickUi.showErrorWindow(LM.error("loginHBDoesntExistTitle"), LM.error("loginHBDoesntExist"), getStage());
					break;
				case HollowBitServerConnectivity.WRONG_PASSWORD_RESPONSE_PACKET_ID://Wrong password
					QuickUi.showErrorWindow(LM.error("loginHBIncorrectPasswordTitle"), LM.error("loginHBIncorrectPassword"), getStage());
					break;
				case HollowBitServerConnectivity.INVALID_EMAIL_RESPONSE_PACKET_ID://Invalid email
					QuickUi.showErrorWindow(LM.error("loginHBInvalidEmailTitle"), LM.error("loginHBInvalidEmail"), getStage());
					break;
				case HollowBitServerConnectivity.INVALID_PASSWORD_RESPONSE_PACKET_ID://Invalid password
					QuickUi.showErrorWindow(LM.error("loginInvalidPasswordTitle"), LM.error("loginInvalidPassword"), getStage());
					break;
				case HollowBitServerConnectivity.CORRECT_LOGIN_RESPONSE_PACKET_ID://Correct login!
					if (rememberChkBx.isChecked())//Save login if remember is on
						prefs.setLogin(email, password);
					else
						prefs.resetLogin();
					remove();
					break;
				}
			}
		};
	}

}
