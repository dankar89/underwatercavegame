package caveGame;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import common.Assets;

public class JetPack {
	private ParticleEffect jetpackEffect;
	private Body body;

	public JetPack() {
		jetpackEffect = Assets.jetpackEffect;
		jetpackEffect.start();
	}
	
	public JetPack(World world, Body body) {
		jetpackEffect = Assets.jetpackEffect;
		attachToBody(body);
		jetpackEffect.start();
	}

	public void update(float deltaTime) {
		if (body != null) {
			final Vector2 pos = body.getPosition();
			jetpackEffect.setPosition(pos.x, pos.y);
		}

		jetpackEffect.update(deltaTime);
	}

	public void update(float deltaTime, Vector2 pos) {
		jetpackEffect.setPosition(body.getWorldCenter().x, body.getWorldCenter().y);
		jetpackEffect.update(deltaTime);
	}

	public void attachToBody(Body body) {
		this.body = body;
	}

	public void draw(SpriteBatch batch) {
		jetpackEffect.draw(batch);
	}

	public void reset() {
		jetpackEffect.reset();
	}

	public void dispose() {
		jetpackEffect.dispose();
	}
}
