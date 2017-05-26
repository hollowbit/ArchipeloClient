package net.hollowbit.archipeloshared;

import java.util.ArrayList;

public class HitCalculator {
	
	public static ArrayList<String> getCollRectsHit (float hitterCenterX, float hitterCenterY, CollisionRect[] hitted, float range, Direction directionOfHitter) {
		ArrayList<String> collRectsHit = new ArrayList<String>();
		for (CollisionRect rect : hitted) {
			if (didEntityHitEntityCollRect(hitterCenterX, hitterCenterY, rect, range, directionOfHitter))
				collRectsHit.add(rect.name);
		}
		return collRectsHit;
	}
	
	public static boolean didEntityHitEntityRects(float hitterCenterX, float hitterCenterY, CollisionRect[] hitted, float range, Direction directionOfHitter) {
		for (CollisionRect rect : hitted) {
			if (didEntityHitEntityCollRect(hitterCenterX, hitterCenterY, rect, range, directionOfHitter))
				return true;
		}
		return false;
	}
	
	public static boolean didEntityHitEntityCollRect (float hitterCenterX, float hitterCenterY, CollisionRect hittedColRect, float range, Direction directionOfHitter) {
		//Calculates if hitted entity is even within the scope of the hitter entity
		switch (directionOfHitter) {
		case UP:
			if (hittedColRect.yWithOffset() + hittedColRect.height < hitterCenterY)
				return false;
			break;
		case DOWN:
			if (hittedColRect.yWithOffset() > hitterCenterY)
				return false;
			break;
		case LEFT:
			if (hittedColRect.xWithOffset() > hitterCenterX)
				return false;
			break;
		case RIGHT:
			if (hittedColRect.xWithOffset() + hittedColRect.width < hitterCenterX)
				return false;
			break;
		case UP_RIGHT:
			if (hittedColRect.xWithOffset() + hittedColRect.width < hitterCenterX || hittedColRect.yWithOffset() + hittedColRect.height < hitterCenterY)
				return false;
			break;
		case UP_LEFT:
			if (hittedColRect.xWithOffset() > hitterCenterX || hittedColRect.yWithOffset() + hittedColRect.height < hitterCenterY)
				return false;
			break;
		case DOWN_RIGHT:
			if (hittedColRect.xWithOffset() + hittedColRect.width < hitterCenterX || hittedColRect.yWithOffset() > hitterCenterY)
				return false;
			break;
		case DOWN_LEFT:
			if (hittedColRect.xWithOffset() > hitterCenterX || hittedColRect.yWithOffset() > hitterCenterY)
				return false;
			break;
		}
		
		float circleDistanceX = Math.abs(hitterCenterX - hittedColRect.xWithOffset());
	    float circleDistanceY = Math.abs(hitterCenterY - hittedColRect.yWithOffset());

	    if (circleDistanceX > (hittedColRect.width / 2 + range)) { return false; }
	    if (circleDistanceY > (hittedColRect.height / 2 + range)) { return false; }

	    if (circleDistanceX <= (hittedColRect.width / 2)) { return true; } 
	    if (circleDistanceY <= (hittedColRect.height / 2)) { return true; }

	    double cornerDistanceSq = Math.pow((circleDistanceX - hittedColRect.width / 2), 2) + Math.pow((circleDistanceY - hittedColRect.height / 2), 2);

	    return (cornerDistanceSq <= Math.pow(range, 2));
	}
	
}
