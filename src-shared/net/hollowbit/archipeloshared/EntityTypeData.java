package net.hollowbit.archipeloshared;

import java.util.ArrayList;

public class EntityTypeData {
	
	public String id;
	public ArrayList<EntityAnimationData> animations = new ArrayList<EntityAnimationData>();
	public int numberOfStyles = 1;
	public boolean hittable = true;
	public float speed = 0;
	
	public int imgWidth = 16;
	public int imgHeight = 16;
	
	public int footstepOffsetX = imgWidth / 2;
	public int footstepOffsetY = 0;
	
	public String footstepSound = "";
	
	public float drawOrderOffsetY = 0;//Used to offset render order of an entity
	
	//View rect
	public float viewRectOffsetX = 0;
	public float viewRectOffsetY = 0;
	public float viewRectWidth = 16;
	public float viewRectHeight = 16;
	
	//Collision rect
	public CollisionRectData[] collisionRects;
	
	//Sounds
	public EntitySoundData[] sounds;
	
	//Properties
	public PropertyDefinition[] defaultProperties = new PropertyDefinition[]{
			new PropertyDefinition("pos", "point", true),
			new PropertyDefinition("style", "style", false),
			new PropertyDefinition("direction", "direction", false),
	};
	public ArrayList<PropertyDefinition> properties = new ArrayList<PropertyDefinition>();
	
}
