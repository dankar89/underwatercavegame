package caveGame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class PhysicsManager {
	private World world;
	private Box2DDebugRenderer debugRenderer;

	// private Iterator<Body> bodies = null;
	private Array<Body> bodies = new Array<Body>();
	private Array<Body> deletionList = new Array<Body>();

	public PhysicsManager() {
		world = new World(new Vector2(0, 0), true);
		debugRenderer = new Box2DDebugRenderer();
		
		BodyDef def = new BodyDef();
		PolygonShape shape = new PolygonShape();
		def.type = BodyType.DynamicBody;
		def.position.set(100, 2);
		shape.setAsBox(0.25f, 0.25f);
		world.createBody(def).createFixture(shape, 50f);
		shape.dispose();
	}

	public void update(float timeStep, OrthographicCamera cam) {
		if (world.getBodyCount() > 1) // TODO add check for player body
		{
			// bodies = world.get;
			world.getBodies(bodies);
			for (Body b : bodies) {
//				if (b.isActive() && b.isAwake()) {
//					if (b.getUserData().getClass()
//							.equals(AnimatedBox2DSprite.class)) {
//
//					} else {
//						// TODO: if maptile obj
//						if (b.isActive()) {
//							if (b.getPosition().x < cam.position.x
//									- (cam.viewportWidth / 2)
//									|| b.getPosition().x > cam.viewportWidth
//									|| b.getPosition().y < cam.position.y
//											- (cam.viewportHeight / 2)
//									|| b.getPosition().y > cam.viewportHeight) {
////								b.setTransform(x, y, b.getAngle());
//								deletionList.add(b);
//							}
//						}
//					}
//				}
				
				
				// TODO: if maptile obj
//				if (b.isActive()) {
//					if (b.getPosition().x < cam.position.x
//							- ((cam.viewportWidth / Globals.TILE_SIZE) / 2)
//							|| b.getPosition().x > (cam.viewportWidth / Globals.TILE_SIZE)
//							|| b.getPosition().y < cam.position.y
//									- ((cam.viewportHeight / Globals.TILE_SIZE) / 2)
//							|| b.getPosition().y > (cam.viewportHeight / Globals.TILE_SIZE)) {
////						b.setTransform(x, y, b.getAngle());
//						deletionList.add(b);
//					}
//				}
			}
			
			//
			// nextBody = bodies.next();

			// if(nextBody.getPosition().x < cam.position.x - (cam.viewportWidth
			// / 2) ||
			// nextBody.getPosition().y < cam.position.y - (cam.viewportHeight /
			// 2) ||
			// nextBody.getPosition().x > cam.position.x + (cam.viewportWidth /
			// 2) ||
			// nextBody.getPosition().y > cam.position.y + (cam.viewportHeight /
			// 2))
			// {
			// deletionList.add(nextBody);
			// }

		}

		world.step(timeStep, 6, 2);
		
		//destroy bodies marked for deletion
		for (Body body : deletionList) {
			world.destroyBody(body);
		}
		deletionList.clear();
	}

	public void renderDebug(Matrix4 projMatrix) {
		debugRenderer.render(this.world, projMatrix);
	}

	public World getWorld() {
		return world;
	}

	public void reset() {
		world.dispose();
		world = new World(new Vector2(0, 0), true);
	}

	public void dispose() {
		world.dispose();
		debugRenderer.dispose();
	}
}
