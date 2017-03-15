package net.hollowbit.archipelo.screen.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.Packet;
import net.hollowbit.archipelo.network.PacketHandler;
import net.hollowbit.archipelo.network.PacketType;
import net.hollowbit.archipelo.network.packets.LoginPacket;
import net.hollowbit.archipelo.screen.Screen;
import net.hollowbit.archipelo.screen.ScreenType;
import net.hollowbit.archipelo.tools.LM;
import net.hollowbit.archipelo.tools.LanguageSpecificMessageManager.Cat;
import net.hollowbit.archipelo.tools.QuickUi;

public class DebugStartScreen extends Screen {
	
	private static final String DEBUG_EMAIL = "joetheman@gmail.com";
	private static final String DEBUG_PASS = "joetheman";
	
	Stage stage;
	boolean packetSent = false;
	long startTime;
	
	public DebugStartScreen() {
		super(ScreenType.DEBUG_START_SCREEN);
		startTime = System.currentTimeMillis();
	}

	@Override
	public void create() {
		stage = new Stage(ArchipeloClient.getGame().getCameraUi().getScreenViewport(), ArchipeloClient.getGame().getBatch());
		ArchipeloClient.getGame().getNetworkManager().connect("localhost", 22122);
	}

	@Override
	public void update(float deltaTime) {
		if (ArchipeloClient.getGame().getNetworkManager().isConnected() && !packetSent) {
			packetSent = true;
			ArchipeloClient.getGame().getNetworkManager().addPacketHandler(new PacketHandler() {
				
				@Override
				public boolean handlePacket (Packet packet) {
					if (packet.packetType == PacketType.LOGIN) {
						LoginPacket loginPacket = (LoginPacket) packet;
						//Handle packet results
						switch (loginPacket.result) {
						case LoginPacket.RESULT_BAD_VERSION:
							QuickUi.showErrorWindow(LM.getMsg(Cat.ERROR, "outOfDateTitle"), LM.getMsg(Cat.ERROR, "outOfDate") + "(" + loginPacket.version + ")", stage);
							break;
						case LoginPacket.RESULT_LOGIN_ERROR:
							QuickUi.showErrorWindow(LM.getMsg(Cat.ERROR, "loginHBInvalidTitle"), LM.getMsg(Cat.ERROR, "loginHBInvalid"), stage);
							break;
						case LoginPacket.RESULT_LOGIN_SUCCESSFUL:
							ArchipeloClient.getGame().getScreenManager().setScreen(new CharacterPickerScreen());
							break;
						}
						ArchipeloClient.getGame().getNetworkManager().removePacketHandler(this);
						return true;
					}
					return false;
				}
			});
			ArchipeloClient.getGame().getNetworkManager().sendPacket(new LoginPacket(DEBUG_EMAIL, DEBUG_PASS));
			
		} else if (System.currentTimeMillis() - startTime >= 5000) {
			ArchipeloClient.getGame().getScreenManager().setScreen(new ErrorScreen("Could not connect to debug server!"));
			return;
		}
	}

	@Override
	public void render(SpriteBatch batch, float width, float height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderUi(SpriteBatch batch, float width, float height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
