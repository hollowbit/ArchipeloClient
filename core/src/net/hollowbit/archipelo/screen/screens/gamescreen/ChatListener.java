package net.hollowbit.archipelo.screen.screens.gamescreen;

import net.hollowbit.archipelo.screen.screens.gamescreen.windows.ChatMessage;

public interface ChatListener {
	
	public void newChatMessageReceived (ChatMessage chatMessage);
	
}
