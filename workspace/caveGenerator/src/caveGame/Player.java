package caveGame;

import java.util.ArrayList;

import net.dermetfan.utils.libgdx.box2d.Box2DUtils;
import net.dermetfan.utils.libgdx.graphics.AnimatedBox2DSprite;
import net.dermetfan.utils.libgdx.graphics.AnimatedSprite;
import net.dermetfan.utils.libgdx.graphics.Box2DSprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
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

	// DEBUG STUFF
	public static ArrayList<String> debugStrings = new ArrayList<String>();

	private static float SCALE = 4; // needed to make the "pixels" to scale

	public Player(World world, Vector2 startPos) {
		angle = 0;
		flashlightEnabled = false;
		isMoving = false;
		speed = 1f;
		rotationSpeed = 3f;
		movement = Vector2.Zero;
		oldPos = pos;

		deltaPos = Vector2.Zero;

		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		// bodyDef.position.set(new Vector2((Gdx.graphics.getWidth() / 2)
		// / GameConstants.TILE_SIZE, (Gdx.graphics.getHeight() / 2)
		// / GameConstants.TILE_SIZE));
		bodyDef.position.set(startPos);
		body = world.createBody(bodyDef);

		swimAnimation = new Animation(1 / 3f, GameResources.diverSprites);
		swimAnimation.setPlayMode(Animation.LOOP);

		animatedBox2dSprite = new AnimatedBox2DSprite(new AnimatedSprite(
				swimAnimation));

		animatedBox2dSprite.setAdjustSize(false);
		animatedBox2dSprite.setUseOrigin(true);

		animatedBox2dSprite.setScale(SCALE / GameConstants.TILE_SIZE);

		w = animatedBox2dSprite.getWidth();
		h = animatedBox2dSprite.getHeight();

		animatedBox2dSprite.setOrigin(w / 2, h / 2);

		shape = new PolygonShape();
		shape.setAsBox(((w / 2) / GameConstants.TILE_SIZE) * SCALE,
				((h / 2) / GameConstants.TILE_SIZE) * SCALE);

		fixture = body.createFixture(shape, 50f);
		shape.dispose();

		animatedBox2dSprite.setPosition(
				(-w / 2) + (Box2DUtils.width(body) / 2),
				(-h / 2) + (Box2DUtils.height(body) / 2));

		body.setFixedRotation(true);

		animatedBox2dSprite.play();

		body.setUserData(animatedBox2dSprite);
	}

	public void update(float detla, Vector2 camPos) {
		animatedBox2dSprite.update(1 / 60f);
		
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			movement.x = -speed;
		} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			movement.x = speed;
		} else
			movement.x = 0;

		if (Gdx.input.isKeyPressed(Keys.UP)) {
			movement.y = -speed;
		} else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			movement.y = speed;
		} else
			movement.y = 0;
		
		move(movement.x, movement.y);

		if (GameConstants.isAndroid)
			flashlightEnabled = true;
	}

	public void move(float mx, float my) {
		isMoving = true;

		if (mx != 0 || my != 0)
			body.setLinearVelocity(mx, my);
		else
			body.setLinearVelocity(0, 0);

		float angleRad = (float) Math.atan2(my, mx);

		if (body.getAngle() < angleRad)
			body.setAngularVelocity(rotationSpeed);
		else if (body.getAngle() < -angleRad)
			body.setAngularVelocity(-rotationSpeed);
		else if (body.getAngle() != angleRad)
			body.setTransform(body.getPosition(), angleRad);

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
		animatedBox2dSprite.draw(batch, body);
		// box2dSprite.draw(batch, body);
		// animatedBox2dSprite.draw(batch, fixture);
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
			if (!isMoving && !animatedBox2dSprite.isFlipX())
				animatedBox2dSprite.flipFrames(true, false);
			break;
		case Keys.RIGHT:
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
		default:
			return false;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.LEFT:
			stop();
			// animatedBox2dSprite.flipFrames(true, false);
			break;
		case Keys.RIGHT:
			stop();
			// if (animatedBox2dSprite.isFlipX())
			// animatedBox2dSprite.flipFrames(true, false);
			break;
		case Keys.UP:
			stop();
			break;
		case Keys.DOWN:
			stop();
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
