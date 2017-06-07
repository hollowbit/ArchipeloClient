package net.hollowbit.archipelo.screen.screens.gamescreen.windows;

import java.util.HashMap;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.form.MobileCompatibleWindow;
import net.hollowbit.archipelo.network.packets.FormRequestPacket;
import net.hollowbit.archipelo.network.packets.LogoutPacket;
import net.hollowbit.archipelo.screen.screens.GameScreen;
import net.hollowbit.archipelo.tools.LM;

public class MainMenuWindow extends MobileCompatibleWindow {
	
	GameScreen screen;
	
	//Ui
	TextButton inventoryBtn;
	TextButton statsBtn;
	TextButton chatBtn;
	TextButton logoutBtn;
	TextButton returnBtn;
	
	public MainMenuWindow (final GameScreen screen, Stage stage) {
		super(LM.ui("mainMenu"), ArchipeloClient.getGame().getUiSkin(), 0.5f);
		this.screen = screen;
		this.setStage(stage);
		this.setMovable(false);
		
		inventoryBtn = new TextButton(LM.ui("inventory"), getSkin());
		inventoryBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ArchipeloClient.getGame().getNetworkManager().sendPacket(new FormRequestPacket("inventory", new HashMap<String, String>()));
				remove();
				super.clicked(event, x, y);
			}
			
		});
		add(inventoryBtn).pad(5);
		row();
		
		statsBtn = new TextButton(LM.ui("myStats"), getSkin());
		statsBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ArchipeloClient.getGame().getNetworkManager().sendPacket(new FormRequestPacket("stats", new HashMap<String, String>()));
				remove();
				super.clicked(event, x, y);
			}
			
		});
		add(statsBtn).pad(5);
		row();
		
		chatBtn = new TextButton(LM.ui("chat"), getSkin());
		chatBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				screen.openChatWindow();
				super.clicked(event, x, y);
			}
			
		});
		add(chatBtn).pad(5);
		row();
		
		logoutBtn = new TextButton(LM.ui("logout"), getSkin());
		logoutBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ArchipeloClient.getGame().getNetworkManager().sendPacket(new LogoutPacket());//Will receive a logout packet in response and then go to menu
				remove();
				super.clicked(event, x, y);
			}
			
		});
		add(logoutBtn).pad(5);
		row();
		
		returnBtn = new TextButton(LM.ui("returnToGame"), getSkin());
		returnBtn.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				remove();
				super.clicked(event, x, y);
			}
			
		});
		add(returnBtn).pad(5);
		
		final MainMenuWindow mainMenuWindow = this;
		this.addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				if (keycode == Keys.ESCAPE)
					mainMenuWindow.remove();
				return super.keyDown(event, keycode);
			}
		});
		
		this.pack();
	}

}
