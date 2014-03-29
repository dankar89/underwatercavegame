package caveGame;

import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import common.GameConstants;

public class PhysicsManager {
	private World world;
	private ExtendedBox2DDebugRenderer debugRenderer;
	private Matrix4 debugMatrix;

	// private Iterator<Body> bodies = null;
	private Array<Body> bodies = new Array<Body>();
	private Array<Body> deletionList = new Array<Body>();

	public PhysicsManager() {
		//TODO: Fix box2d scale!!!
		world = new World(new Vector2(0, 9.8f), true);
		world.setContactListener(new MyContactListener());
		debugRenderer = new ExtendedBox2DDebugRenderer();
		
//		BodyDef def = new BodyDef();
//		PolygonShape shape = new PolygonShape();
//		def.type = BodyType.StaticBody;
//		def.position.set(0, 0);
////		def.linearDamping = 2;
////		def.angularDamping = 1;
//		shape.setAsBox(GameConstants.TILE_SIZE*100, GameConstants.TILE_SIZE*100);
//		world.createBody(def).createFixture(shape, 50f);
//		shape.dispose();
	}

	public void update(float timeStep, OrthographicCamera cam) {
		if (world.getBodyCount() > 1) // TODO add check for player body
		{
			world.getBodies(bodies);
			for (Iterator<Body> iter = bodies.iterator(); iter.hasNext();) {
				Body body = iter.next();
				if (body.isActive()) {
					
				}
			}
		}

		world.step(timeStep, 6, 2);
		
//		//destroy bodies marked for deletion
//		for (Body body : deletionList) {
//			world.destroyBody(body);
//		}
//		deletionList.clear();
	}

	public void renderDebug(Matrix4 projMatrix) {
		debugRenderer.render(this.world, projMatrix);
	}
	
	public void renderDebug() {
		debugRenderer.render(this.world, this.debugMatrix);
	}
	
	
//	public void renderDebug(Matrix4 worldProjMatrix, Matrix4 playerProjMatrix) {
//		debugRenderer.renderBodies(this.world, worldProjMatrix, playerProjMatrix);
//	}
	
	public void resize(OrthographicCamera cam){ 
//		debugMatrix = cam.combined.cpy();
////		debugMatrix.translate(-cam.viewportWidth/2, -cam.viewportHeight/2, 0);
//		debugMatrix.scale(GameConstants.METER_PER_TILE, GameConstants.METER_PER_TILE, 0);
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
