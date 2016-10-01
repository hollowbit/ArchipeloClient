package net.hollowbit.archipelo.screen.screens.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.screen.screens.MainMenuScreen;

public class LoginRegisterWindow extends Window {

	private TextButton loginButton;
	private TextButton registerButton;
	private TextButton exitButton;
	
	MainMenuScreen screen;
	
	public LoginRegisterWindow (final MainMenuScreen screen, Stage stage) {
		super("Login", ArchipeloClient.getGame().getUiSkin());
		this.screen = screen;
		this.setStage(stage);
		
		this.setMovable(false);
		
		//Initialize buttons
		loginButton = new TextButton("Login", getSkin());
		loginButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!screen.isThereAlreadyALoginWindow()) {
					LoginWindow loginWndw = new LoginWindow(getStage());
					loginWndw.setPosition(Gdx.graphics.getWidth() / 2 - loginWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - loginWndw.getHeight() / 2);
					getStage().addActor(loginWndw);
					screen.setLoginWindow(loginWndw);
				}
				super.clicked(event, x, y);
			}
		});
		add(loginButton);
		
		row();
		
		registerButton = new TextButton("Register", getSkin());
		registerButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (screen.isThereAlreadyARegisterWindow()) {
					RegisterWindow registerWndw = new RegisterWindow(getStage());
					registerWndw.setPosition(Gdx.graphics.getWidth() / 2 - registerWndw.getWidth() / 2, Gdx.graphics.getHeight() / 2 - registerWndw.getHeight() / 2);
					getStage().addActor(registerWndw);
				}
				super.clicked(event, x, y);
			}
		});
		add(registerButton);
		
		row();
		
		exitButton = new TextButton("Exit", getSkin());
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
				super.clicked(event, x, y);
			}
		});
		add(exitButton);
	}
	
}
