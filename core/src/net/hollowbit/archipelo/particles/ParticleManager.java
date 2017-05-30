package net.hollowbit.archipelo.particles;

import java.util.LinkedList;

import net.hollowbit.archipeloshared.MapSnapshot;
import net.hollowbit.archipeloshared.ParticlesData;

public class ParticleManager {
	
	private LinkedList<Particle> particles;
	
	public ParticleManager() {
		particles = new LinkedList<Particle>();
	}
	
	public void update(float deltaTime) {
		LinkedList<Particle> particlesToRemove = new LinkedList<Particle>();
		for (Particle particle : getParticles()) {
			particle.update(deltaTime);
			
			if (particle.isExpired())
				particlesToRemove.add(particle);
		}
		removeAll(particlesToRemove);
	}
	
	private synchronized void removeAll(LinkedList<Particle> particlesToRemove) {
		particles.removeAll(particlesToRemove);
	}
	
	private synchronized void addAll(LinkedList<Particle> particlesToAdd) {
		particles.addAll(particlesToAdd);
	}
	
	public synchronized LinkedList<Particle> getParticles() {
		LinkedList<Particle> particleList = new LinkedList<Particle>();
		particleList.addAll(particles);
		return particleList;
	}

	/**
	 * Add new particles to the manager that have arrived in the MapSnapshot
	 * @param particleDatas
	 */
	public void applyChangesSnapshot(MapSnapshot changes) {
		if (!changes.particles.isEmpty()) {
			LinkedList<Particle> newParticles = new LinkedList<Particle>();
			for (ParticlesData particleData : changes.particles) {
				for (int i = 0; i < particleData.x.length; i++)
					newParticles.add(ParticleType.createUsingData(particleData.type, particleData.x[i], particleData.y[i], particleData.w[i], particleData.meta));
			}
			addAll(newParticles);
		}
	}
	
}
