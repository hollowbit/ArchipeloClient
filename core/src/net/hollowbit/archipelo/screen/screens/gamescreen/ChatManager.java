package net.hollowbit.archipelo.screen.screens.gamescreen;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.network.packets.ChatMessagePacket;
import net.hollowbit.archipelo.screen.screens.gamescreen.windows.ChatMessage;

public class ChatManager {
	
	
	ArrayList<ChatMessage> messages;
	ArrayList<ChatListener> chatListeners;
	
	public ChatManager () {
		this.messages = new ArrayList<ChatMessage>();
		this.chatListeners = new ArrayList<ChatListener>();
	}
	
	public synchronized void update (float deltatime) {
		ArrayList<ChatMessage> messagesToRemove = new ArrayList<ChatMessage>();
		for (ChatMessage message : messages) {
			if (message.isShowing())
				message.update(deltatime);
			if (message.removeFromList())
				messagesToRemove.add(message);
		}
		messages.removeAll(messagesToRemove);
	}
	
	public synchronized void render (SpriteBatch batch, boolean showAll) {
		for (ChatMessage message : messages) {
			if (message.isShowing() || showAll)
				message.render(batch, showAll);
		}
	}
	
	public synchronized void addChatMessage (ChatMessagePacket packet) {
		ChatMessage message = new ChatMessage(packet.prefix, packet.message, packet.sender, this);
		messages.add(message);
		
		for (ChatListener listener : chatListeners)
			listener.newChatMessageReceived(message);
	}
	
	public synchronized ArrayList<ChatMessage> getChatMessages () {
		return messages;
	}
	
	public synchronized void addChatListener (ChatListener listener) {
		chatListeners.add(listener);
	}
	
	public synchronized void removeChatListener (ChatListener listener) {
		chatListeners.remove(listener);
	}
	
	public void sendMessage(String message) {
		ArchipeloClient.getGame().getNetworkManager().sendPacket(new ChatMessagePacket(message));
	}
	
}
