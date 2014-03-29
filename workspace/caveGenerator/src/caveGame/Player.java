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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import common.GameConstants;
import common.GameResources;
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
	private boolean isMoving;

	private Vector2 movement;
	private float speed;
	private float rotationSpeed;
	private Vector2 pos, oldPos, deltaPos;
	private float newAngle = 0;

	private boolean flashlightEnabled;
	private boolean isUnderWater;

	private PointLight playerlight;
	private ConeLight flashlight;
	private float flashlightAngle = 0;

	// DEBUG STUFF
	public static ArrayList<String> debugStrings = new ArrayList<String>();

	private static float SCALE = 2; // needed to make the "pixels" to scale

	public Player(World world, RayHandler rayHandler, Vector2 startPos) {
		angle = 0;
		flashlightEnabled = false;
		isUnderWater = false;
		isMoving = false;
		speed = 5.5f;
		rotationSpeed = 3f;
		movement = Vector2.Zero;
		oldPos = pos;

		deltaPos = Vector2.Zero;

		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(startPos);
		body = world.createBody(bodyDef);

		swimAnimation = new Animation(1 / 3f, GameResources.playerSprites);
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
		fixDef.density = 6.5f;
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

		playerlight = new PointLight(rayHandler, 11, new Color(1, 1, 1, 0.2f),
				8.5f, body.getPosition().x, body.getPosition().y);
		playerlight.attachToBody(body, 0, 0);

		Vector2 lightPos = new Vector2(body.getPosition().x,
				body.getPosition().y);
		Color lightColor = new Color(0.2f, 0.5f, 0.5f, 0.55f);
		flashlight = new ConeLight(rayHandler, 5, lightColor, 22.0f,
				lightPos.x, lightPos.y, 0, 20.0f);
	}

	public void update(float detla, OrthographicCamera cam, int waterLevel) {
		animatedBox2dSprite.update(1 / 60f);

		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
			movement.x = -speed;
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT)
				|| Gdx.input.isKeyPressed(Keys.D)) {
			movement.x = speed;
		} else
			movement.x = 0;

		if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) {
			movement.y = -speed;
		} else if (Gdx.input.isKeyPressed(Keys.DOWN)
				|| Gdx.input.isKeyPressed(Keys.S)) {
			movement.y = speed;
		} else
			movement.y = 0;

		if (GameConstants.isAndroid)
			flashlightEnabled = true;

		if (Globals.lightsEnabled)
			flashlight.setActive(isFlashlightEnabled());

		if (Gdx.input.isTouched()) {
//			flashlightAngle = MathUtils.atan2(
//					(Gdx.input.getY() / GameConstants.PIXELS_PER_METER)
//							- getPos().y,
//					(Gdx.input.getX() / GameConstants.PIXELS_PER_METER)
//							- getPos().x);
//			System.out.println(getScreenPos(cam));
//			flashlightAngle = new Vector2(Gdx.input.getX(), Gdx.input.getY()).sub(getPos())
			System.out.println(flashlightAngle);
			System.out.println((flashlightAngle * MathUtils.radDeg) * 10);

		}
		
		flashlight.setDirection(flashlightAngle);
		flashlight.setPosition(getPos());

		if ((int) body.getPosition().y > waterLevel) {
			if (Gdx.input.isKeyPressed(Keys.SPACE)) {
				swim(0, -(speed * 2));
			} else {
				swim(movement.x * 2, movement.y * 2);
			}

			body.setGravityScale(0);
			body.setLinearDamping(10f);
			// body.setFixedRotation(false);
			isUnderWater = true;
		} else {
			move(movement.x);
			body.setGravityScale(1);
			body.setLinearDamping(2f);
			isUnderWater = false;
		}
	}

	public void jump() {
		// body.applyForceToCenter(0, -15f, true);
		body.applyLinearImpulse(0, -2.5f, body.getWorldCenter().x,
				body.getWorldCenter().y, true);
	}

	public void swim(float mx, float my) {
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
		isMoving = true;

		if (mx != 0) {
			body.applyForce(mx, 0, body.getWorldCenter().x,
					body.getWorldCenter().y, true);
			body.getFixtureList().get(0).setFriction(.5f);
		} else {
			// body.getFixtureList().get(0).setFriction(2f);
		}

	}

	public void setGravityScale(float gravity) {
		body.setGravityScale(gravity);
	}

	public void setPosition(float x, float y) {
		isMoving = true;

		// pos.x += x;
		// pos.y += y;

		// float angleRad = (float) Math.atan2(y, x);
		//
		// if (body.getAngle() < angleRad)
		// body.setAngularVelocity(rotationSpeed);
		// else if (body.getAngle() < -angleRad)
		// body.setAngularVelocity(-rotationSpeed);
		// else if(body.getAngle() != angleRad)
		// body.setTransform(body.getPosition(), angleRad);

	}

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
		body.setLinearVelocity(0, 0);
		body.setAngularVelocity(0);
		movement = Vector2.Zero;
		isMoving = false;
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
	
//	public Vector2 getScreenPos(OrthographicCamera cam) {
//		Vector3 topLeft = cam.position.sub(cam.viewportWidth/2, cam.viewportHeight/2, 0);	
//		System.out.println("viewport: " + topLeft.toString());
//		return getPos().scl(GameConstants.PIXELS_PER_METER).sub(topLeft.x, topLeft.y);
//	}

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
			if (!isMoving && !animatedBox2dSprite.isFlipX())
				animatedBox2dSprite.flipFrames(true, false);
			break;
		case Keys.RIGHT:
		case Keys.D:
			// if (!animatedBox2dSprite.isFlipX())
			if (!isMoving && animatedBox2dSprite.isFlipX())
				animatedBox2dSprite.flipFrames(true, false);
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
			// body.getFixtureList().get(0).setFriction(1.5f);
			// stop();
			// animatedBox2dSprite.flipFrames(true, false);
			break;
		case Keys.RIGHT:
			// body.getFixtureList().get(0).setFriction(1.5f);
			// stop();
			// if (animatedBox2dSprite.isFlipX())
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
