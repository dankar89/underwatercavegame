package caveGame;

import java.util.ArrayList;

import net.dermetfan.utils.libgdx.box2d.Box2DUtils;
import net.dermetfan.utils.libgdx.graphics.AnimatedBox2DSprite;
import net.dermetfan.utils.libgdx.graphics.AnimatedSprite;
import net.dermetfan.utils.libgdx.graphics.Box2DSprite;
import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import common.Assets;
import common.GameConstants;
import common.Globals;

public class Player extends InputAdapter {
	Body body;
	BodyDef bodyDef;
	Fixture fixture;
	float angle;
	float w, h;
	PolygonShape shape;
	Animation swimAnimation;
	AnimatedSprite animatedSprite;
	AnimatedBox2DSprite animatedBox2dSprite;
	Box2DSprite box2dSprite;
	

	
	private enum LookDirection {
		LEFT, RIGHT, UP, DOWN,
	}

	private LookDirection lookDirection;

	private Vector2 movement;
	private float speed;
	private float rotationSpeed;
	private Vector2 pos, oldPos, deltaPos;
	private float newAngle = 0;

	private boolean flashlightEnabled;
	private boolean isUnderWater;

	private PointLight playerlight;
	private ConeLight flashlight;
	private float lookAngle = 0;

	// DEBUG STUFF
	public static ArrayList<String> debugStrings = new ArrayList<String>();

	private static float SCALE = 2; // needed to make the "pixels" to scale

	public Player(World world, RayHandler rayHandler, Vector2 startPos) {
		angle = 0;
		flashlightEnabled = false;
		isUnderWater = false;
		lookDirection = LookDirection.RIGHT;
		speed = 5.5f;
		rotationSpeed = 3f;
		movement = Vector2.Zero;
		oldPos = pos;

		deltaPos = Vector2.Zero;

		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(startPos);
		body = world.createBody(bodyDef);

		swimAnimation = new Animation(1 / 3f, Assets.playerSprites);
		swimAnimation.setPlayMode(Animation.LOOP);

		animatedBox2dSprite = new AnimatedBox2DSprite(new AnimatedSprite(
				swimAnimation));

		animatedBox2dSprite.setAdjustSize(false);
		animatedBox2dSprite.setUseOrigin(true);

		animatedBox2dSprite.setScale(1f / GameConstants.PIXELS_PER_METER);

		w = animatedBox2dSprite.getWidth();
		h = animatedBox2dSprite.getHeight();

		animatedBox2dSprite.setOrigin(w / 2, h / 2);

		FixtureDef fixDef = new FixtureDef();
		fixDef.density = 10f;
		fixDef.friction = 2f;

		shape = new PolygonShape();
		// shape.setAsBox(((w / 2) / GameConstants.PIXELS_PER_METER),
		// ((h / 4) / GameConstants.PIXELS_PER_METER));
		// shape.setAsBox(((w / 2) / GameConstants.PIXELS_PER_METER),
		// ((h / 3f) / GameConstants.PIXELS_PER_METER), new Vector2(0,.58f), 0);
		shape.setAsBox(((w / 2) / GameConstants.PIXELS_PER_METER),
				((h / 2f) / GameConstants.PIXELS_PER_METER), new Vector2(0, 0),
				0);
		fixDef.shape = shape;
		body.createFixture(fixDef);

		CircleShape circleShape = new CircleShape();
		circleShape.setRadius((w / 2) / GameConstants.PIXELS_PER_METER);
		circleShape.setPosition(new Vector2(0,
				(((h / 2f)) / GameConstants.PIXELS_PER_METER)
						+ circleShape.getRadius() * 2));
		fixDef.shape = circleShape;
		// body.createFixture(fixDef);

		circleShape.setRadius((w / 2) / GameConstants.PIXELS_PER_METER);
		circleShape.setPosition(new Vector2(0, (h * 1.5f)
				/ GameConstants.PIXELS_PER_METER));
		fixDef.shape = circleShape;
		// body.createFixture(fixDef);

		shape.dispose();
		circleShape.dispose();

		animatedBox2dSprite.setPosition(
				(-w / 2) + (Box2DUtils.width(body) / 2),
				(-h / 2) + (Box2DUtils.height(body) / 2));

		animatedBox2dSprite.flipFrames(false, true);

		body.setFixedRotation(true);
		body.setAngularDamping(2f);

		animatedBox2dSprite.play();

		System.out.println(body.getMass());
		body.setUserData(animatedBox2dSprite);

		
		playerlight = new PointLight(rayHandler, 100, new Color(1, 1, 1, 0.2f),
				8.5f, body.getPosition().x, body.getPosition().y);
		playerlight.attachToBody(body, 0, 0);

		Vector2 lightPos = new Vector2(body.getPosition().x,
				body.getPosition().y);
		Color lightColor = new Color(0.2f, 0.5f, 0.5f, 0.55f);
		flashlight = new ConeLight(rayHandler, 100, lightColor, 15.0f,
				lightPos.x, lightPos.y, 0, 20.0f);
	}

	public void update(float detla, Vector2 mouseWorldPos, int waterLevel) {
		animatedBox2dSprite.update(1 / 60f);
		if (movingLeft() && movingRight()) {
			movement.x = 0;
		} else {
			if (movingLeft()) {
				lookDirection = LookDirection.LEFT;
				movement.x = -speed;
			} else if (movingRight()) {
				lookDirection = LookDirection.RIGHT;
				movement.x = speed;
			} else {
				movement.x = 0;
			}
		}

		if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
			movement.y = -speed;
		} else if (Gdx.input.isKeyPressed(Keys.DOWN)
				|| Gdx.input.isKeyPressed(Keys.S)) {
			movement.y = speed;
		} else
			movement.y = 0;

		if (Globals.isAndroid)
			flashlightEnabled = true;

		if (Globals.lightsEnabled)
			flashlight.setActive(isFlashlightEnabled());


		lookAngle = MathUtils.atan2(mouseWorldPos.y - getPos().y,
				mouseWorldPos.x - getPos().x) * MathUtils.radDeg;
		flashlight.setDirection(lookAngle);
		flashlight.setPosition(getPos());

		// if looking left, flip left
		if (lookAngle < -90 && lookAngle > 90) {

		}

		if ((int) body.getPosition().y > waterLevel) {
			body.setGravityScale(0);
			if (Gdx.input.isKeyPressed(Keys.SPACE)) {
				swim(0, -(speed * 2));
			} else {
				swim(movement.x * 2, movement.y * 2);
			}

			// body.setFixedRotation(false);
			isUnderWater = true;
		} else {
			body.setGravityScale(1);
			// body.setLinearDamping(2f);
			move(movement.x);
			isUnderWater = false;
		}
	}

	private boolean movingLeft() {
		return Gdx.input.isKeyPressed(Keys.LEFT)
				|| Gdx.input.isKeyPressed(Keys.A);
	}

	private boolean movingRight() {
		return Gdx.input.isKeyPressed(Keys.RIGHT)
				|| Gdx.input.isKeyPressed(Keys.D);
	}

	public void jump() {
		// body.applyForceToCenter(0, -15f, true);
		body.applyLinearImpulse(0, -3.5f, body.getWorldCenter().x,
				body.getWorldCenter().y, true);
	}

	public void swim(float mx, float my) {
		body.setLinearDamping(10f);
		if (mx != 0 || my != 0)
			body.applyForce(mx, my, body.getWorldCenter().x,
					body.getWorldCenter().y, true);

		float angleRad = (float) Math.atan2(my, mx);

		if (body.getAngle() < angleRad)
			body.setAngularVelocity(rotationSpeed);
		else if (body.getAngle() < -angleRad)
			body.setAngularVelocity(-rotationSpeed);
		else if (body.getAngle() != angleRad)
			body.setTransform(body.getPosition(), angleRad);
	}

	public void move(float mx) {
		if (mx != 0) {
			body.setLinearDamping(1f);
			body.applyForce(mx * 1.5f, 0, body.getWorldCenter().x,
					body.getWorldCenter().y, true);

			// body.getFixtureList().get(0).setFriction(.5f);
		} else {
			// body.setLinearDamping(0f);
			// body.getFixtureList().get(0).setFriction(2f);
		}

	}

	public void setGravityScale(float gravity) {
		body.setGravityScale(gravity);
	}

	// public void setPosition(float x, float y) {
	// isMoving = true;
	//
	// // pos.x += x;
	// // pos.y += y;
	//
	// // float angleRad = (float) Math.atan2(y, x);
	// //
	// // if (body.getAngle() < angleRad)
	// // body.setAngularVelocity(rotationSpeed);
	// // else if (body.getAngle() < -angleRad)
	// // body.setAngularVelocity(-rotationSpeed);
	// // else if(body.getAngle() != angleRad)
	// // body.setTransform(body.getPosition(), angleRad);
	//
	// }

	public void reset() {

	}

	public void rotate(float newAngle) {
		// float angleRad = (float) Math.atan2(pos.y - oldPos.y, pos.x -
		// oldPos.x);
		// if (body.getAngle() < angleRad) {
		// body.setAngularVelocity(rotationSpeed);
		// } else {
		// body.setTransform(body.getPosition(), angleRad);
		// }

	}

	public void stop() {
		// body.setLinearDamping(5f);
		// System.out.println(body.getLinearDamping());
		// body.setLinearVelocity(0, 0);
		body.setAngularVelocity(0);
		movement = Vector2.Zero;
		// moveDirection = LookDirection.NONE;
	}

	public void scale(float amount) {
		animatedBox2dSprite.scale(amount);
	}

	public void draw(SpriteBatch batch) {
		batch.enableBlending();
		animatedBox2dSprite.draw(batch, body);
	}

	public AnimatedBox2DSprite getSprite() {
		return animatedBox2dSprite;
	}

	public Body getBody() {
		return body;
	}

	public Vector2 getPos() {
		return body.getPosition();
	}

	// public Vector2 getScreenPos(OrthographicCamera cam) {
	// // Vector2 topLeft = new Vector2(
	// // (cam.position.x * GameConstants.TILE_SIZE)
	// // - Gdx.graphics.getWidth(),
	// // (cam.position.y * GameConstants.TILE_SIZE)
	// // - Gdx.graphics.getHeight());
	// //
	//
	// System.out.println("viewport: " + topLeft.toString());
	// return getPos().scl(GameConstants.PIXELS_PER_METER).sub(topLeft.x,
	// topLeft.y);
	// }

	public boolean isFlashlightEnabled() {
		return flashlightEnabled;
	}

	// @Override
	// public boolean scrolled(int amount) {
	// animatedBox2dSprite.setScale(Globals.cameraZoom - 4);
	// return false;
	// }

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.LEFT:
		case Keys.A:
//			System.out.println(lookDirection);
//			System.out.println(animatedBox2dSprite.isFlipX());
			if (lookDirection == LookDirection.LEFT && !animatedBox2dSprite.isFlipX()) {
//				System.out.println("moving left");
				animatedBox2dSprite.flipFrames(true, false);
			}
			break;
		case Keys.RIGHT:
		case Keys.D:
//			System.out.println(lookDirection);
//			System.out.println(animatedBox2dSprite.isFlipX());
			if (lookDirection == LookDirection.RIGHT && animatedBox2dSprite.isFlipX()) {
//				System.out.println("moving right");
				animatedBox2dSprite.flipFrames(true, false);
			}
			break;
		case Keys.R:
			body.setAngularVelocity(5f);
			break;
		case Keys.F:
			flashlightEnabled = !flashlightEnabled;
			break;
		case Keys.SPACE:
			if (!isUnderWater)
				jump();
		default:
			return false;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.LEFT:
		case Keys.A:
			stop();

			// if(lookDirection == LookDirection.LEFT)
			// lookDirection =
			// body.getFixtureList().get(0).setFriction(1.5f);
			// stop();
			// if(isMoving)
			// animatedBox2dSprite.flipFrames(true, false);
			break;
		case Keys.RIGHT:
		case Keys.D:
			stop();
			// body.getFixtureList().get(0).setFriction(1.5f);
			// stop();
			// if (animatedBox2dSprite.isFlipX())
			// if(isMoving)
			// animatedBox2dSprite.flipFrames(true, false);
			break;
		case Keys.UP:
			// stop();
			break;
		case Keys.DOWN:
			// stop();
			break;
		case Keys.R:
			body.setAngularVelocity(0);
			break;
		default:
			return false;
		}
		return true;
	}
}
