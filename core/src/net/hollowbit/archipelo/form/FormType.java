package net.hollowbit.archipelo.form;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import net.hollowbit.archipelo.screen.screens.GameScreen;
import net.hollowbit.archipelo.screen.screens.gamescreen.windows.InventoryForm;
import net.hollowbit.archipelo.screen.screens.gamescreen.windows.PlayerStatsForm;
import net.hollowbit.archipeloshared.FormData;

@SuppressWarnings("rawtypes")
public enum FormType {
	
	INVENTORY ("inventory", InventoryForm.class, true),
	PLAYER_STATS ("stats", PlayerStatsForm.class, true);
	
	public String id;
	public Class formClass;
	public boolean requestable;
	
	private FormType (String id, Class formClass, boolean requestable) {
		this.id = id;
		this.formClass = formClass;
		this.requestable = requestable;
	}
	
	@SuppressWarnings("unchecked")
	public Form createFormOfType () {
		Form form = null;
		try {
			form = (Form) ClassReflection.newInstance(formClass);
		} catch (ReflectionException e) {
			Gdx.app.error("Form Loader", "Could not load new instance of form type for: " + id + ". " + e.getMessage());
		}
		return form;
	}
	
	private static HashMap<String, FormType> formTypes;
	
	static {
		formTypes = new HashMap<String, FormType>();
		for (FormType formType : FormType.values())
			formTypes.put(formType.id, formType);
	}
	
	public static FormType getFormTypeById (String id) {
		return formTypes.get(id);
	}
	
	public static Form createFormByFormData (FormData formData, GameScreen gameScreen) {
		FormType formType = formTypes.get(formData.type);
		Form form = formType.createFormOfType();
		form.create(formData, formType, gameScreen);
		return form;
	}
	
}