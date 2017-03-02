package net.hollowbit.archipelo.form;

import java.util.HashMap;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.packets.FormInteractPacket;
import net.hollowbit.archipelo.screen.screens.GameScreen;
import net.hollowbit.archipeloshared.FormData;

public abstract class Form extends MobileCompatibleWindow {
	
	protected String id;
	protected FormType type;
	protected GameScreen gameScreen;
	
	public Form (String title) {
		super(title, ArchipeloClient.getGame().getUiSkin());
	}
	
	public Form (String title, float inset) {
		super(title, ArchipeloClient.getGame().getUiSkin(), inset);
	}
	
	public void create (FormData formData, FormType formType, GameScreen gameScreen) {
		this.id = formData.id;
		this.type = formType;
		this.gameScreen = gameScreen;
		this.update(formData);
	}
	
	public abstract void update (FormData formData);
	
	@Override
	public boolean remove () {
		ArchipeloClient.getGame().getNetworkManager().sendPacket(new FormInteractPacket(this.id, "close", new HashMap<String, String>()));
		gameScreen.removeForm(this);
		return super.remove();
	}
	
	public String getId () {
		return id;
	}
	
	public FormType getType () {
		return type;
	}
	
}
