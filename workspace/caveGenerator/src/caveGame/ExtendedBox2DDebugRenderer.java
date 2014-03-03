package caveGame;

import java.util.Iterator;

import net.dermetfan.utils.libgdx.graphics.AnimatedBox2DSprite;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class ExtendedBox2DDebugRenderer extends Box2DDebugRenderer {
	private final static Array<Body> bodies = new Array<Body>();

	public ExtendedBox2DDebugRenderer() {
		super();
	}

	public ShapeRenderer getRenderer() {
		return this.renderer;
	}

	public void setProjectionMatrix(Matrix4 projMatrix) {
		this.renderer.setProjectionMatrix(projMatrix);
	}

	public void renderBody(Body body, Matrix4 projMatrix) {
		setProjectionMatrix(projMatrix);
		super.renderBody(body);
	}

	public void renderBodies(World world, Matrix4 worldProjMatrix,
			Matrix4 playerProjMatrix) {
		
		Body playerBody = null;
		setProjectionMatrix(worldProjMatrix);
		renderer.begin(ShapeType.Line);
		world.getBodies(bodies);
		for (Iterator<Body> iter = bodies.iterator(); iter.hasNext();) {
			Body body = iter.next();
			if (body.isActive()) {
				if (body.getUserData() != null) {
					if (body.getUserData().getClass()
							.equals(AnimatedBox2DSprite.class)) {
						playerBody = body;
						continue;
					}
				}
				super.renderBody(body);
			}
		}
		
		setProjectionMatrix(playerProjMatrix);
		super.renderBody(playerBody);
		renderer.end();
	}
}
