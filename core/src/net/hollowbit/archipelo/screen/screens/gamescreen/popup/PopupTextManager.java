package net.hollowbit.archipelo.screen.screens.gamescreen.popup;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.network.packets.PopupTextPacket;

public class PopupTextManager {
	
	private ArrayList<PopupText> popupTexts;
	
	public PopupTextManager () {
		popupTexts = new ArrayList<PopupText>();
	}
	
	public synchronized void update (float deltatime) {
		ArrayList<PopupText> popupTextsToRemove = new ArrayList<PopupText>();
		for (PopupText popupText : popupTexts) {
			popupText.update(deltatime);
			if (popupText.remove)
				popupTextsToRemove.add(popupText);
		}
		popupTexts.removeAll(popupTextsToRemove);
	}
	
	public synchronized void render (SpriteBatch batch) {
		for (PopupText popupText : popupTexts) {
			popupText.render(batch);
		}
	}
	
	public synchronized void addPopupTextWithPacket (PopupTextPacket popupTextPacket) {
		PopupText popupText;
		switch (popupTextPacket.type) {
		case PopupText.TYPE_NORMAL:
		default:
			popupText = new NormalPopupText(popupTextPacket.text, this);
			break;
		}
		
		addPopupText(popupText);
	}
	
	public synchronized void addPopupText (PopupText popupText) {
		//If the popup text is a maptag one, remove other map tag popups. You can't have more than one
		if (popupText instanceof MapTagPopupText) {
			ArrayList<PopupText> textsToRemove = new ArrayList<PopupText>();
			for (PopupText text : popupTexts) {
				if (text instanceof MapTagPopupText)
					textsToRemove.add(text);
			}
			popupTexts.removeAll(textsToRemove);
		}
			
		popupTexts.add(popupText);
	}
	
	public synchronized ArrayList<PopupText> getPopupTexts () {
		return popupTexts;
	}
	
}
