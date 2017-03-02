package net.hollowbit.archipelo.entity.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.hollowbit.archipelo.ArchipeloClient;
import net.hollowbit.archipelo.entity.Entity;
import net.hollowbit.archipelo.entity.EntityComponent;
import net.hollowbit.archipelo.entity.EntitySnapshot;

public class ConditionalRenderEntityComponent extends EntityComponent {

	private String animation;
	private String propertyKey;
	private String currentValue;
	private String compareValue;
	private boolean requiredCondition;
	
	public ConditionalRenderEntityComponent (Entity entity, String animation, String propertyKey, String compareValue, boolean requiredCondition, boolean defaultValue) {
		super(entity);
		this.animation = animation;
		this.propertyKey = propertyKey;
		if (defaultValue)
			this.currentValue = compareValue;
		this.compareValue = compareValue;
		this.requiredCondition = requiredCondition;
	}
	
	@Override
	public boolean render(SpriteBatch batch, boolean previouslyCancelled) {
		if (previouslyCancelled)
			return false;
		
		boolean conditionMet = currentValue.equals(compareValue) == requiredCondition;
		
		if (conditionMet)
			render(batch, animation, ArchipeloClient.STATE_TIME);
		
		return conditionMet;
	}
	
	@Override
	public void applyChangesSnapshot (EntitySnapshot snapshot) {
		super.applyChangesSnapshot(snapshot);
		this.currentValue = snapshot.getString(propertyKey, currentValue);
	}

}
