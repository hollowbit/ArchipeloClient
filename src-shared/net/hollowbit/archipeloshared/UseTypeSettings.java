package net.hollowbit.archipeloshared;

public class UseTypeSettings {
	
	public int animationType;
	public int soundType;
	public boolean thrust;
	
	public UseTypeSettings(int animationId, int soundId, boolean thrust) {
		super();
		this.animationType = animationId;
		this.soundType = soundId;
		this.thrust = thrust;
	}
	
}
