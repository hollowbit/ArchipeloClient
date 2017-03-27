package net.hollowbit.archipelo.entity.components;

import java.util.HashSet;

import com.badlogic.gdx.math.Vector2;

import net.hollowbit.archipelo.entity.EntityComponent;
import net.hollowbit.archipelo.entity.LivingEntity;
import net.hollowbit.archipeloshared.RollableEntity;
import net.hollowbit.archipeloshared.TileSoundType;

public class FootstepPlayerComponent extends EntityComponent {
	
	protected RollableEntity rollableEntity;
	protected LivingEntity livingEntity;
	protected boolean canRoll = false;
	protected HashSet<String> possibleSoundTypes;
	
	public FootstepPlayerComponent(LivingEntity entity, boolean canRoll, TileSoundType... possibleSoundTypes) {
		super(entity);
		this.livingEntity = entity;
		
		if (entity instanceof RollableEntity && canRoll) {
			rollableEntity = (RollableEntity) entity;
			this.canRoll = true;
		}
		
		this.possibleSoundTypes = new HashSet<String>();
		for (TileSoundType type : possibleSoundTypes)
			this.possibleSoundTypes.add(type.getId());
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		Vector2 tilePos = entity.getFeetTile();
		
		String tileSound = entity.getLocation().getMap().getTileTypeAtLocation((int) tilePos.x, (int) tilePos.y).getFootstepSound();
		if (!possibleSoundTypes.contains(tileSound))
			tileSound = "default";
		
		if (livingEntity.isMoving()) {
			if (canRoll && rollableEntity.isRolling())
				entity.getAudioManager().setFootstepSound(entity.getEntityType().getFootstepSound() + "/" + tileSound + "-roll", 1);
			else
				entity.getAudioManager().setFootstepSound(entity.getEntityType().getFootstepSound() + "/" + tileSound + "-walk", livingEntity.getSpeed() / entity.getEntityType().getSpeed());
		} else
			entity.getAudioManager().stopFootstepSound();
	}

}
