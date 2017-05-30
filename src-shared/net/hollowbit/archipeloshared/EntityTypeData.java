package net.hollowbit.archipeloshared;

import java.util.ArrayList;

public class EntityTypeData {
	
	public String id;
	public ArrayList<EntityAnimationData> animations = new ArrayList<EntityAnimationData>();
	public int numberOfStyles = 1;
	public boolean hittable = true;
	
	public boolean showHealthBar = false;
	public int maxHealth = 1;
	public float speed = 0;
	
	public int imgWidth = 16;
	public int imgHeight = 16;
	
	public int footstepOffsetX = imgWidth / 2;
	public int footstepOffsetY = 0;
	public int headOffsetFromTop = 0;
	
	public String footstepSound = "";
	
	public float drawOrderOffsetY = 0;//Used to offset render order of an entity
	
	//Collision rect
	public CollisionRectData[] collisionRects = new CollisionRectData[0];
	
	//Sounds
	public EntitySoundData[] sounds = new EntitySoundData[0];
	
	//Properties
	public PropertyDefinition[] defaultProperties = new PropertyDefinition[]{
			new PropertyDefinition("pos", "point", true),
			new PropertyDefinition("style", "style", false),
			new PropertyDefinition("direction", "direction", false),
			new PropertyDefinition("health", "int", false),
	};
	public ArrayList<PropertyDefinition> properties = new ArrayList<PropertyDefinition>();
	
}
