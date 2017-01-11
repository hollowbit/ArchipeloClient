package net.hollowbit.archipelo.tools;

import com.badlogic.gdx.utils.Json;

public class StaticTools {
	
	private static final Json json = new Json();
	
	public static Json getJson () {
		return json;
	}
	
	public static float singleDimensionLerp (float value1, float value2, float fraction) {
		return value1 + ((value2 - value1) * fraction);
	}
	
}
