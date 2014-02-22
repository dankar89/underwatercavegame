package com.me.cavegenerator;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;

public class OLD_GameResources {
	private static AssetManager assetManager;
	
	public static Texture texture;
	public static Array<AtlasRegion> cornerTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> verticalTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> horizontalTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> lonelyHorizontalTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> lonelyVerticalTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> thinHorizontalTiles = new Array<AtlasRegion>();
	public static Array<AtlasRegion> thinVerticalTiles = new Array<AtlasRegion>();
	public static AtlasRegion wallRegion;
	
	public static TextureAtlas tilesAtlas;
	public static TextureAtlas spriteAtlas;
	
	public static void init()
	{
		assetManager = new AssetManager();
		loadResources();
		getResources();
	}
	
	private static void loadResources() {

		// queue stuff for loading
		assetManager.load("textures/tiles.txt", TextureAtlas.class);
		assetManager.load("textures/spritesheet.txt", TextureAtlas.class);

		assetManager.load("data/whitesquare.png", Texture.class);

		// do the actual loading
		assetManager.finishLoading();
	}
	
	private static void getResources()
	{
		texture = assetManager.get("data/whitesquare.png");
		tilesAtlas = assetManager.get("textures/tiles.txt");
		spriteAtlas = assetManager.get("textures/spritesheet.txt");

		cornerTiles = tilesAtlas.findRegions("corner");
		horizontalTiles = tilesAtlas.findRegions("horizontalTile");
		verticalTiles = tilesAtlas.findRegions("verticalTile");
		lonelyHorizontalTiles = tilesAtlas.findRegions("lonelyTile");
		lonelyVerticalTiles = tilesAtlas.findRegions("lonelyVerticalTile");
		thinVerticalTiles = tilesAtlas.findRegions("thinVerticalTile");
		thinHorizontalTiles = tilesAtlas.findRegions("thinHorizontalTile");
		wallRegion = tilesAtlas.findRegion("empty");
	}
	
	public static void dispose()
	{
		assetManager.dispose();
	}
	
	public static boolean isReady()
	{
		if(assetManager.update())
			return true;
		return false;
	}
}
