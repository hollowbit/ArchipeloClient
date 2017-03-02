package net.hollowbit.archipelo.screen.screens.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.form.MobileCompatibleWindow;
import net.hollowbit.archipelo.screen.screens.MainMenuScreen;
import net.hollowbit.archipelo.tools.LM;

public class LoginRegisterWindow extends MobileCompatibleWindow {

	private Label instructionsLabel;
	private TextButton loginButton;
	private TextButton registerButton;
	private TextButton exitButton;
	
	MainMenuScreen screen;
	
	public LoginRegisterWindow (final MainMenuScreen screen, Stage stage) {
		super("Login", ArchipeloClient.getGame().getUiSkin());
		this.screen = screen;
		this.setStage(stage);
		
		setMovable(false);
		
		instructionsLabel = new Label(LM.ui("loginRegisterInstructions"), getSkin(), "small");
		instructionsLabel.setWrap(true);
		instructionsLabel.setAlignment(Align.center);
		add(instructionsLabel).width(350).pad(15);
		
		row();
		
		final LoginRegisterWindow loginRegisterWindow = this;
		
		//Initialize buttons
		loginButton = new TextButton(LM.ui("login"), getSkin());
		loginButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!screen.isThereAlreadyALoginWindow()) {
					LoginWindow loginWndw = new LoginWindow(loginRegisterWindow, getStage());
					loginWndw.centerOnScreen();
					getStage().addActor(loginWndw);
					screen.setLoginWindow(loginWndw);
					setVisible(false);
				}
				super.clicked(event, x, y);
			}
		});
		add(loginButton).pad(15);
		
		row();
		
		registerButton = new TextButton(LM.ui("register"), getSkin());
		registerButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!screen.isThereAlreadyARegisterWindow()) {
					RegisterWindow registerWndw = new RegisterWindow(loginRegisterWindow, getStage());
					registerWndw.centerOnScreen();
					getStage().addActor(registerWndw);
					screen.setRegisterWindow(registerWndw);
					setVisible(false);
				}
				super.clicked(event, x, y);
			}
		});
		add(registerButton).pad(15);
		
		row();
		
		if (!ArchipeloClient.IS_GWT) {
			exitButton = new TextButton(LM.ui("exit"), getSkin());
			exitButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Gdx.app.exit();
					super.clicked(event, x, y);
				}
			});
			add(exitButton).pad(15);
		}
		
		pack();
	}
	
}
