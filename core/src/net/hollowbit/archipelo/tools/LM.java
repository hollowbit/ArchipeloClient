package net.hollowbit.archipelo.tools;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.tools.LanguageSpecificMessageManager.Cat;

public class LM {
	
	/**
	 * Shorthand for accessing language manager.
	 * Basically this:
	 * LM.getMsg(Cat.UI, "playGame");
	 * v.s.
	 * ArchipeloClient.getGame().getLanguageSpecificMessageManager().getMessage(Cat.UI, "playGame");
	 * @param category
	 * @param id
	 * @return
	 */
	public static String getMsg(Cat category, String id) {
		return ArchipeloClient.getGame().getLanguageSpecificMessageManager().getMessage(category, id);
	}
	
	/**
	 * Gets ui message with id in selected language
	 * @param id
	 * @return
	 */
	public static String ui(String id) {
		return getMsg(Cat.UI, id);
	}
	
	/**
	 * Gets error message with id in selected language
	 * @param id
	 * @return
	 */
	public static String error(String id) {
		return getMsg(Cat.ERROR, id);
	}
	
	public static String itemNames(String id) {
		return getMsg(Cat.ITEM_NAMES, id);
	}
	
	public static String itemDescs(String id) {
		return getMsg(Cat.ITEM_DESCS, id);
	}
	
}
