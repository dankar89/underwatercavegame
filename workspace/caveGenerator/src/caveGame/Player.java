package caveGame;

import java.util.ArrayList;

import kryonet.NetworkClient;
import kryonet.PlayerKeyDownUpdateRequest;
import kryonet.PlayerKeyUpUpdateRequest;
import kryonet.PlayerMouseUpdateRequest;
import kryonet.PlayerPositionUpdateRequest;
import net.dermetfan.utils.libgdx.box2d.Box2DUtils;
import net.dermetfan.utils.libgdx.graphics.AnimatedBox2DSprite;
import net.dermetfan.utils.libgdx.graphics.AnimatedSprite;
import net.dermetfan.utils.libgdx.graphics.Box2DSprite;
import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import common.Assets;
import common.GameConstants;
import common.Globals;
import common.Globals.FixtureUserData;

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
	private boolean canUseJetpack;
	private boolean isUsingJetpack;

	private PointLight playerlight;
	// private ConeLight flashlight;
	private FlashLight flashLight;
	private float lookAngle = 0;

	private ParticleEffect jetpackEffect;

	// DEBUG STUFF
	public static ArrayList<String> debugStrings = new ArrayList<String>();

	private static float SCALE = 2; // needed to make the "pixels" to scale

	public Player(World world, RayHandler rayHandler, Vector2 startPos) {
		angle = 0;
		flashlightEnabled = false;
		isUnderWater = false;
		canUseJetpack = false;
		isUsingJetpack = false;
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

		shape.setAsBox(((w / 2) / GameConstants.PIXELS_PER_METER), (h / 6)
				/ GameConstants.PIXELS_PER_METER, new Vector2(0, .2f), 0);
		fixDef.density = 0;
		// fixDef.filter.categoryBits = Globals.PLAYER_SENSOR_CATEGORY_BITS;
		// fixDef.filter.groupIndex = Globals.PLAYER_SENSOR_GROUP_INDEX;
		// fixDef.filter.maskBits = Globals.PLAYER_SENSOR_MASK_BITS;
		fixDef.friction = 0;
		fixDef.shape = shape;
		fixDef.isSensor = true;
		body.createFixture(fixDef).setUserData(
				FixtureUserData.PLAYER_FOOT_SENSOR);

		// CircleShape circleShape = new CircleShape();
		// circleShape.setRadius((w / 2) / GameConstants.PIXELS_PER_METER);
		// circleShape.setPosition(new Vector2(0,
		// (((h / 2f)) / GameConstants.PIXELS_PER_METER)
		// + circleShape.getRadius() * 2));
		// fixDef.shape = circleShape;
		// body.createFixture(fixDef);

		// circleShape.setRadius((w / 2) / GameConstants.PIXELS_PER_METER);
		// circleShape.setPosition(new Vector2(0, (h * 1.5f)
		// / GameConstants.PIXELS_PER_METER));
		// fixDef.shape = circleShape;
		// body.createFixture(fixDef);

		shape.dispose();
		// circleShape.dispose();

		animatedBox2dSprite.setPosition(
				(-w / 2) + (Box2DUtils.width(body) / 2),
				(-h / 2) + (Box2DUtils.height(body) / 2));

		animatedBox2dSprite.flipFrames(false, true);

		body.setFixedRotation(true);
		body.setAngularDamping(2f);

		animatedBox2dSprite.play();

		body.setUserData(animatedBox2dSprite);

		float lightDistance = (Gdx.graphics.getWidth() / Gdx.graphics
				.getHeight()) * 6.5f;
		playerlight = new PointLight(rayHandler, 100, new Color(1, 1, 1, 0.2f),
				lightDistance, body.getPosition().x, body.getPosition().y);
		playerlight.attachToBody(body, Box2DUtils.width(body) / 2f,
				Box2DUtils.height(body) / 2f);
		// Vector2 lightPos = new Vector2(body.getPosition().x,
		// body.getPosition().y);
		Color lightColor = new Color(0.2f, 0.5f, 0.5f, 0.55f);
		// flashlight = new ConeLight(rayHandler, 100, lightColor, 15.0f,
		// lightPos.x, lightPos.y, 0, 20.0f);

		flashLight = new FlashLight(rayHandler, lightColor, getPos());

		Light.setContactFilter(Globals.PLAYER_SENSOR_CATEGORY_BITS,
				Globals.PLAYER_SENSOR_MASK_BITS,
				Globals.PLAYER_SENSOR_MASK_BITS);

		jetpackEffect = Assets.jetpackEffect;
		jetpackEffect.setPosition(body.getPosition().x, body.getPosition().y);
		jetpackEffect.start();
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

		if (Globals.lightsEnabled) {
			flashLight.setActive(isFlashlightEnabled());
		}

		flashLight.update(detla, getPos(), mouseWorldPos);
		if (!flashLight.hasBattery())
			flashlightEnabled = false;


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
			if (Gdx.input.isKeyPressed(Keys.SPACE)) {
				if (canUseJetpack) {
					jetpackMove(-11f);
				}
			} else {
				isUsingJetpack = false;
			}
			body.setGravityScale(1);
			// body.setLinearDamping(2f);
			move(movement.x);
			isUnderWater = false;
		}

		if (Globals.isCurrentGameMultiplayer) {
			final PlayerPositionUpdateRequest req = new PlayerPositionUpdateRequest();
			req.x = body.getPosition().x;
			req.y = body.getPosition().y;
			NetworkClient.client.sendUDP(req);

			if (Gdx.input.isCursorCatched()) {
				final PlayerMouseUpdateRequest req2 = new PlayerMouseUpdateRequest();
				req2.mouseX = mouseWorldPos.x;
				req2.mouseY = mouseWorldPos.y;
				NetworkClient.client.sendUDP(req2);
			}
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
		canUseJetpack = false;
		body.applyLinearImpulse(0, -3.5f, body.getWorldCenter().x,
				body.getWorldCenter().y, true);
	}

	public void jetpackMove(float y) {
		body.applyForceToCenter(0, y, true);
		jetpackEffect.setPosition(body.getPosition().x, body.getPosition().y);
		jetpackEffect.update(Gdx.graphics.getDeltaTime());
		isUsingJetpack = true;
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

		if (isUsingJetpack) {
			jetpackEffect.draw(batch);
		}
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
	
	public FlashLight getFlashLight(){
		return flashLight;
	}

	public boolean isFlashlightEnabled() {
		return flashlightEnabled;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.LEFT:
		case Keys.A:
			if (lookDirection == LookDirection.LEFT
					&& !animatedBox2dSprite.isFlipX()) {
				animatedBox2dSprite.flipFrames(true, false);
			}
			break;
		case Keys.RIGHT:
		case Keys.D:
			if (lookDirection == LookDirection.RIGHT
					&& animatedBox2dSprite.isFlipX()) {
				animatedBox2dSprite.flipFrames(true, false);
			}
			break;
		case Keys.R:
			body.setAngularVelocity(5f);
			break;
		case Keys.F:
			if (flashLight.hasBattery()) {
				flashlightEnabled = !flashlightEnabled;
			} else {
				flashlightEnabled = false;
			}
			break;
		case Keys.SPACE:
			if (!isUnderWater) {
				if (Globals.numOfFootContacts > 0) {
					jump();
				} else {
					jetpackEffect.reset();
					canUseJetpack = true;
				}
			}
		default:
			return false;
		}

		if (Globals.isCurrentGameMultiplayer) {
			final PlayerKeyDownUpdateRequest req = new PlayerKeyDownUpdateRequest();
			req.lastKeyDown = keycode;
			NetworkClient.client.sendUDP(req);
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
		case Keys.SPACE:
			if (Globals.numOfFootContacts < 1)
				jetpackEffect.reset();
			// canUseJetpack = true;
			break;
		case Keys.R:
			body.setAngularVelocity(0);
			break;
		default:
			return false;
		}

		if (Globals.isCurrentGameMultiplayer) {
			final PlayerKeyUpUpdateRequest req = new PlayerKeyUpUpdateRequest();
			req.lastKeyUp = keycode;
			NetworkClient.client.sendUDP(req);
		}
		return true;
	}

	public void dispose() {
		jetpackEffect.dispose();
	}
}
