package net.hollowbit.archipelo.tools;

import java.util.Random;

import com.badlogic.gdx.utils.Json;

public class StaticTools {
	
	private static final Json json = new Json();
	private static final Random random = new Random();
	
	public static Json getJson () {
		return json;
	}
	
	public static Random getRandom () {
		return random;
	}
	
	/**
	 * Returns the raw, un-altered fraction between 2 values. It can be 0-1.
	 * It may be more or less depending on the input values.
	 * @param valueBefore
	 * @param valueAfter
	 * @param intermediateValue
	 * @return
	 */
	public static float singleDimentionLerpFraction (double valueBefore, double valueAfter, double intermediateValue) {
		return (float) ((intermediateValue - valueBefore) / (valueAfter - valueBefore));
	}
	
	/**
	 * Returns the lerp coordinate between the 2 values at the specified fraction.
	 * @param value1
	 * @param value2
	 * @param fraction
	 * @return
	 */
	public static float singleDimensionLerp (double value1, double value2, double fraction) {
		return (float) (value1 + ((value2 - value1) * fraction));
	}
	
	/**
	 * Math square root function with less accuracy but much faster compute time for values under 1000.
	 * @param value
	 * @return
	 */
	public static float imperfectSqrt(int value) {
        float lastValue = 1;
        for (float i = 1; i < value / 2f; i += 0.1f) {
            if (i * i > value) {
                return lastValue;
            }
            lastValue = i;
        }
        return 0;
    }
	
}
