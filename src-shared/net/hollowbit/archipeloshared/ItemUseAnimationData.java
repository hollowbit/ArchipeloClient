package net.hollowbit.archipeloshared;

public class ItemUseAnimationData {
	
	public boolean canEndEarly = false;//Whether the player can end this animation before it is finished
	public boolean thrust = false;
	public boolean stick = false;//Whether the animation should stick on the last frame if the attack button is still held down.
	public float[] timings = null;
	public float runtime = 0;//Only needs to be specified if timings is not
	
}
