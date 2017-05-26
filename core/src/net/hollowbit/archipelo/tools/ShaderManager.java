package net.hollowbit.archipelo.tools;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Simple shader manager with save and restore functionality.
 * @author vedi0boy
 *
 */
public class ShaderManager {
	
	public enum ShaderType {
		WHITE("shaders/passthrough.vsh", "shaders/white.fsh"),
		RED("shaders/passthrough.vsh", "shaders/red.fsh"),
		GREEN("shaders/passthrough.vsh", "shaders/green.fsh"),
		BLUE("shaders/passthrough.vsh", "shaders/blue.fsh"),
		EXPERIMENTAL("shaders/test.vsh", "shaders/test.fsh");
		
		private String vshPath, fshPath;
		
		private ShaderType(String vshPath, String fshPath) {
			this.vshPath = vshPath;
			this.fshPath = fshPath;
		}

		public String getVshPath() {
			return vshPath;
		}

		public String getFshPath() {
			return fshPath;
		}
		
	}
	
	private HashMap<ShaderType, ShaderProgram> shaders;
	
	private ShaderType saved;
	private ShaderType current;
	
	public ShaderManager() {
		this.shaders = new HashMap<ShaderType, ShaderProgram>();
		
		for (ShaderType type : ShaderType.values()) {
			FileHandle vshFile = Gdx.files.internal(type.getVshPath());
			FileHandle fshFile = Gdx.files.internal(type.getFshPath());
			ShaderProgram.pedantic = false;
			ShaderProgram shader = new ShaderProgram(vshFile, fshFile);
			System.out.println(shader.isCompiled() ? "Shader " + type.name() + " compiled!" : shader.getLog());
			shaders.put(type, shader);
		}
	}
	
	/**
	 * Applies a new shader to the batch.
	 * @param batch
	 * @param type
	 */
	public void applyShader(Batch batch, ShaderType type) {
		if (type == null) {//Simply reset shader if type is null
			resetShader(batch);
			return;
		}
		
		ShaderProgram shader = shaders.get(type);
		if (shader.isCompiled()) {
			batch.setShader(shader);
			current = type;
		} else
			System.out.println("ShaderManager Could not apply shader: " + type.name());
	}
	
	/**
	 * Removes the current shader.
	 * @param batch
	 */
	public void resetShader(Batch batch) {
		batch.setShader(null);
		current = null;
	}
	
	/**
	 * Saves the current shader to be restored later.
	 */
	public void save() {
		saved = current;
	}
	
	/**
	 * Restores the previously saved shader.
	 * @param batch
	 */
	public void restore(Batch batch) {
		this.applyShader(batch, saved);
	}
	
}
