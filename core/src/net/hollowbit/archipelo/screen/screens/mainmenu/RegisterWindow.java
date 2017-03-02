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
import net.hollowbit.archipelo.tools.QuickUi;
import net.hollowbit.archipelo.tools.QuickUi.TextFieldMessageListener;
import net.hollowbit.archipeloshared.StringValidator;

public class RegisterWindow extends MobileCompatibleWindow {
	
	//Ui
	TextButton loginBtn;
	TextButton cancelBtn;
	TextField emailFld;
	TextField passwordFld;
	TextField confirmPasswordFld;
	CheckBox rememberChkBx;
	
	String password = "", email = "";
	
	public RegisterWindow(final LoginRegisterWindow loginRegisterWindow, Stage stage) {
		super(LM.ui("register"), ArchipeloClient.getGame().getUiSkin());
		this.setStage(stage);
		this.setMovable(false);
		
		//Load ui elements
		Label instructionsLabel = new Label(LM.ui("registerInstructions"), getSkin(), "small");
		instructionsLabel.setWrap(true);
		instructionsLabel.setAlignment(Align.center);
		add(instructionsLabel).width(400).pad(10).colspan(2);
		row();
		
		emailFld = new TextField("", getSkin());
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
		
		passwordFld = new TextField("", getSkin());
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
		
		confirmPasswordFld = new TextField("", getSkin());
		confirmPasswordFld.setPasswordCharacter('*');
		confirmPasswordFld.setMaxLength(StringValidator.MAX_PASSWORD_LENGTH);
		confirmPasswordFld.setPasswordMode(true);
		confirmPasswordFld.setMessageText(LM.ui("confirmPassword"));
		QuickUi.makeTextFieldMobileCompatible(LM.ui("confirmPassword"), confirmPasswordFld, stage, new TextFieldMessageListener() {
			
			@Override
			public void messageReceived (String message, boolean isEmpty) {
				confirmPasswordFld.setText(message);
			}
		});
		add(confirmPasswordFld).width(400).pad(10).colspan(2);
		row();
		
		rememberChkBx = new CheckBox(" " + LM.ui("remember"), getSkin());
		rememberChkBx.setChecked(ArchipeloClient.getGame().getPrefs().isLoggedIn());
		add(rememberChkBx).pad(10);
		row();
		
		loginBtn = new TextButton(LM.ui("login"), getSkin());
		loginBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (emailFld.getText().equals("") || passwordFld.getText().equals("") || confirmPasswordFld.getText().equals("")) {
					QuickUi.showErrorWindow(LM.error("fieldsEmptyTitle"), LM.error("fieldsEmpty"), getStage());
				} else if (!StringValidator.isEmailValid(emailFld.getText()) || emailFld.getText().contains(" ")) {
					QuickUi.showErrorWindow(LM.error("loginHBInvalidEmailTitle"), LM.error("loginHBInvalidEmail"), getStage());
				} else if (!StringValidator.isStringValid(passwordFld.getText(), StringValidator.PASSWORD, StringValidator.MAX_PASSWORD_LENGTH)) {
					QuickUi.showErrorWindow(LM.error("loginInvalidPasswordTitle"), LM.error("loginInvalidPasswordTitle"), getStage());
				} else if (!confirmPasswordFld.getText().equals(passwordFld.getText())) {
					QuickUi.showErrorWindow(LM.error("registerHBConfirmPasswordWrongTitle"), LM.error("registerHBConfirmPasswordWrong"), getStage());
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
		
		cancelBtn = new TextButton(LM.ui("cancel"), getSkin());
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
					QuickUi.showErrorWindow(LM.error("youAreBanned"), data[0], getStage());
					break;
				case HollowBitServerConnectivity.USER_ALREADY_EXISTS_ERROR_RESPONSE_PACKET_ID://User already exists
					QuickUi.showErrorWindow(LM.error("loginHBAlreadyExistsTitle"), LM.error("loginHBAlreadyExists"), getStage());
					break;
				case HollowBitServerConnectivity.INVALID_EMAIL_RESPONSE_PACKET_ID://Invalid email
					QuickUi.showErrorWindow(LM.error("loginHBInvalidEmailTitle"), LM.error("loginHBInvalidEmail"), getStage());
					break;
				case HollowBitServerConnectivity.INVALID_PASSWORD_RESPONSE_PACKET_ID://Invalid password
					QuickUi.showErrorWindow(LM.error("loginInvalidPasswordTitle"), LM.error("loginInvalidPassword"), getStage());
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

