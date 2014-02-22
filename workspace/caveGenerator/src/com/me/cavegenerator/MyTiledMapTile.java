package com.me.cavegenerator;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

public class MyTiledMapTile implements TiledMapTile {

	private int id;

	private BlendMode blendMode = BlendMode.ALPHA;

	private MapProperties properties;

	private TextureRegion textureRegion;

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		this.id = id;
	}

	@Override
	public BlendMode getBlendMode() {
		// TODO Auto-generated method stub
		return blendMode;
	}

	@Override
	public void setBlendMode(BlendMode blendMode) {
		// TODO Auto-generated method stub
		this.blendMode = blendMode;
	}

	@Override
	public TextureRegion getTextureRegion() {
		// TODO Auto-generated method stub
		return textureRegion;
	}

	@Override
	public MapProperties getProperties() {
		if (properties == null) {
			properties = new MapProperties();
		}
		return properties;
	}

	public MyTiledMapTile(TextureRegion textureRegion, boolean flipX,
			boolean flipY) {
		this.textureRegion = textureRegion;
		this.textureRegion.flip(flipX, flipY);
	}

	public MyTiledMapTile(TextureRegion textureRegion) {
		this.textureRegion = textureRegion;
	}

}
