//package caveGame;
//
//import java.util.ArrayList;
//
//import net.dermetfan.utils.libgdx.box2d.Box2DUtils;
//import net.dermetfan.utils.libgdx.graphics.AnimatedBox2DSprite;
//import net.dermetfan.utils.libgdx.graphics.AnimatedSprite;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input.Keys;
//import com.badlogic.gdx.InputAdapter;
//import com.badlogic.gdx.graphics.g2d.Animation;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.Body;
//import com.badlogic.gdx.physics.box2d.BodyDef;
//import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
//import com.badlogic.gdx.physics.box2d.Fixture;
//import com.badlogic.gdx.physics.box2d.PolygonShape;
//import com.badlogic.gdx.physics.box2d.World;
//import common.GameResources;
//import common.Globals;
//
//public class CopyOfPlayer extends InputAdapter {
//	Body body;
//	BodyDef bodyDef;
//	Fixture fixture;
//	float angle;
//	float w, h;
//	PolygonShape shape;
//	Animation swimAnimation;
//	AnimatedSprite animatedSprite;
//	AnimatedBox2DSprite animatedBox2dSprite;
//	private boolean isMoving;
//
//	private Vector2 movement;
//	private float speed;
//	private float rotationSpeed;
//	private Vector2 pos, oldPos, deltaPos;
//	private float newAngle = 0;
//
//	private boolean flashlightEnabled;
//	
//	//DEBUG STUFF
//	public static ArrayList<String> debugStrings = new ArrayList<String>(); 
//
//	private static float SCALE = 4; // needed to make the "pixels" to scale
//
//	public CopyOfPlayer(World world, Vector2 startPos) {
//		angle = 0;
//		flashlightEnabled = false;
//		isMoving = false;
//		speed = 2f / Globals.PIXELS_PER_METER;
//		rotationSpeed = 3f;
//		movement = Vector2.Zero;
//		pos = new Vector2(startPos.x, startPos.y);
//		oldPos = pos;
//
//		deltaPos = Vector2.Zero;
//
//		bodyDef = new BodyDef();
//		bodyDef.type = BodyType.DynamicBody;
//		bodyDef.position.set(new Vector2((Gdx.graphics.getWidth() / 2)
//				/ Globals.PIXELS_PER_METER, (Gdx.graphics.getHeight() / 2)
//				/ Globals.PIXELS_PER_METER));
//		body = world.createBody(bodyDef);
//
//		swimAnimation = new Animation(1 / 3f, GameResources.diverSprites);
//		swimAnimation.setPlayMode(Animation.LOOP);
//
//		animatedBox2dSprite = new AnimatedBox2DSprite(new AnimatedSprite(
//				swimAnimation));
//		animatedBox2dSprite.setAdjustSize(false);
//		animatedBox2dSprite.setUseOrigin(true);
//		animatedBox2dSprite.setScale(SCALE / Globals.PIXELS_PER_METER);
//
//		w = animatedBox2dSprite.getWidth();
//		h = animatedBox2dSprite.getHeight();
//
//		shape = new PolygonShape();
//		shape.setAsBox(((w / 2) / Globals.PIXELS_PER_METER) * SCALE,
//				((h / 2) / Globals.PIXELS_PER_METER) * SCALE);
//
//		fixture = body.createFixture(shape, 100f);
//		shape.dispose();
//
//		animatedBox2dSprite.setOrigin(animatedBox2dSprite.getWidth() / 2,
//				animatedBox2dSprite.getHeight() / 2);
//
//		animatedBox2dSprite.setPosition(
//				(-animatedBox2dSprite.getWidth() / 2)
//						+ (Box2DUtils.width(body) / 2),
//				(-animatedBox2dSprite.getHeight() / 2)
//						+ (Box2DUtils.height(body) / 2));
//
//		body.setUserData(animatedBox2dSprite);
//
////		body.setFixedRotation(true);
//		// fixture.setUserData(animatedBox2dSprite);
//
//		animatedBox2dSprite.play();
//	}
//
//	public void update(float detla) {
//		animatedBox2dSprite.update(1 / 60f);
//		oldPos = pos;
//
//		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
//			movement.x = -speed;
//		} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
//			movement.x = speed;
//		} else
//			movement.x = 0;
//
//		if (Gdx.input.isKeyPressed(Keys.UP)) {
//			movement.y = speed;
//		} else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
//			movement.y = -speed;
//		} else
//			movement.y = 0;
//
//		move(movement.x, movement.y);
//		
////		if (isMoving) {
////			if (!Gdx.input.isKeyPressed(Keys.DOWN)
////					&& !Gdx.input.isKeyPressed(Keys.UP)
////					&& !Gdx.input.isKeyPressed(Keys.LEFT)
////					&& !Gdx.input.isKeyPressed(Keys.RIGHT)) {
////				stop();
////			}
////		}
//
//		if (Globals.isAndroid)
//			flashlightEnabled = true;
//	}
//
//	public void move(float x, float y) {
//		isMoving = true;
//
//		pos.x += x;
//		pos.y += y;
//
//		float angleRad = (float) Math.atan2(y, x);
//
//		if (body.getAngle() < angleRad)
//			body.setAngularVelocity(rotationSpeed);
//		else if (body.getAngle() < -angleRad)
//			body.setAngularVelocity(-rotationSpeed);
//		else if(body.getAngle() != angleRad)
//			body.setTransform(body.getPosition(), angleRad);
//
//	}
//
//	public void rotate(float newAngle) {
////		float angleRad = (float) Math.atan2(pos.y - oldPos.y, pos.x - oldPos.x);
////		if (body.getAngle() < angleRad) {
////			body.setAngularVelocity(rotationSpeed);
////		} else {
////			body.setTransform(body.getPosition(), angleRad);
////		}
//
//	}
//
//	public void stop() {
////		body.setLinearVelocity(0, 0);
//		body.setAngularVelocity(0);
//		movement = Vector2.Zero;
//		isMoving = false;
//	}
//
//	public void scale(float amount) {
//		animatedBox2dSprite.scale(amount);
//	}
//
//	public void draw(SpriteBatch batch) {
//		animatedBox2dSprite.draw(batch, body);
//		// animatedBox2dSprite.draw(batch, fixture);
//	}
//
//	public AnimatedBox2DSprite getSprite() {
//		return animatedBox2dSprite;
//	}
//
//	public Body getBody() {
//		return body;
//	}
//
//	public Vector2 getPos() {
//		return pos;
//	}
//
//	public boolean isFlashlightEnabled() {
//		return flashlightEnabled;
//	}
//
//	// @Override
//	// public boolean scrolled(int amount) {
//	// animatedBox2dSprite.setScale(Globals.cameraZoom - 4);
//	// return false;
//	// }
//
//	@Override
//	public boolean keyDown(int keycode) {
//		switch (keycode) {
//		case Keys.LEFT:
//			if (!isMoving && !animatedBox2dSprite.isFlipX())
//				animatedBox2dSprite.flipFrames(true, false);
//			break;
//		case Keys.RIGHT:
//			// if (!animatedBox2dSprite.isFlipX())
//			if (!isMoving && animatedBox2dSprite.isFlipX())
//				animatedBox2dSprite.flipFrames(true, false);
//			break;
//		case Keys.R:
//			body.setAngularVelocity(5f);
//			break;
//		case Keys.F:
//			flashlightEnabled = !flashlightEnabled;
//			break;
//		default:
//			return false;
//		}
//		return true;
//	}
//
//	@Override
//	public boolean keyUp(int keycode) {
//		switch (keycode) {
//		case Keys.LEFT:
//			stop();
//			// animatedBox2dSprite.flipFrames(true, false);
//			break;
//		case Keys.RIGHT:
//			stop();
//			// if (animatedBox2dSprite.isFlipX())
//			// animatedBox2dSprite.flipFrames(true, false);
//			break;
//		case Keys.UP:
//			stop();
//			break;
//		case Keys.DOWN:
//			stop();
//			break;
//		case Keys.R:
//			body.setAngularVelocity(0);
//			break;
//		default:
//			return false;
//		}
//		return true;
//	}
//}
