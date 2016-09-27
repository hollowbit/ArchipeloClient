package net.hollowbit.archipelo.world;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import net.hollowbit.archipelo.world.elements.AnimatedElement;
import net.hollowbit.archipelo.world.elements.PlainElement;
import net.hollowbit.archipelo.world.tiles.AnimatedTile;
import net.hollowbit.archipelo.world.tiles.PlainTile;
import net.hollowbit.archipeloshared.ElementData;
import net.hollowbit.archipeloshared.ElementList;
import net.hollowbit.archipeloshared.TileData;
import net.hollowbit.archipeloshared.TileList;

public class MapElementLoader {
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Tile> loadTiles () {
		HashMap<String, Tile> tiles = new HashMap<String, Tile>();
		Json json = new Json();
		TileData[] tileList = null;
		try {
			tileList = ((TileList) json.fromJson(ClassReflection.forName("net.hollowbit.archipeloshared.TileList"), Gdx.files.internal("shared/map-elements/tiles.json").readString())).tileList;
		} catch (ReflectionException e) {
			System.out.println("Was unable to load tile data.");
			e.printStackTrace();
		}
		
		for (TileData data : tileList) {
			if (data.animated)
				tiles.put(data.id, new AnimatedTile(data));
			else
				tiles.put(data.id, new PlainTile(data));
		}
		
		return tiles;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, MapElement> loadElements () {
		HashMap<String, MapElement> elements = new HashMap<String, MapElement>();
		Json json = new Json();
		ElementData[] elementList = null;
		try {
			elementList = ((ElementList) json.fromJson(ClassReflection.forName("net.hollowbit.archipeloshared.ElementList"), Gdx.files.internal("shared/map-elements/elements.json"))).elementList;
		} catch (ReflectionException e) {
			System.out.println("Was unable to load element data.");
			e.printStackTrace();
		}
		
		for (ElementData data : elementList) {
			if(data.animated)
				elements.put(data.id, new AnimatedElement(data));
			else
				elements.put(data.id, new PlainElement(data));
		}
		
		return elements;
	}
	
}
