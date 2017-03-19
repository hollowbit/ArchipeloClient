package net.hollowbit.archipelo.tools;

import com.badlogic.gdx.utils.Json;

public class StaticTools {
	
	private static final Json json = new Json();
	
	public static Json getJson () {
		return json;
	}
	
	public static float singleDimentionLerpFraction (double valueBefore, double valueAfter, double intermediateValue) {
		return (float) ((intermediateValue - valueBefore) / (valueAfter - valueBefore));
	}
	
	public static float singleDimensionLerp (double value1, double value2, double fraction) {
		return (float) (value1 + ((value2 - value1) * fraction));
	}
	
}
