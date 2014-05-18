package multiplayer;

import kryonet.NetworkPlayer;
import net.dermetfan.utils.libgdx.box2d.Box2DUtils;
import net.dermetfan.utils.libgdx.graphics.Box2DSprite;
import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import common.Assets;
import common.GameConstants;
import common.Globals;

public class RemotePlayer {
	Body body;
	BodyDef bodyDef;
	Fixture fixture;
	float w, h;
	PolygonShape shape;
	Box2DSprite box2dSprite;

	private Vector2 movement;
	private float speed;
	private float rotationSpeed;

	private boolean flashlightEnabled;

	private Vector2 mouseWorldPos;

	private PointLight playerlight;
	private ConeLight flashlight;
	private float lookAngle = 0;

	public RemotePlayer(World world, RayHandler rayHandler, Vector2 startPos) {
		flashlightEnabled = false;
		movement = Vector2.Zero;
		mouseWorldPos = Vector2.Zero;

		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(startPos);
		body = world.createBody(bodyDef);

		box2dSprite = new Box2DSprite(Assets.playerSprites.first());
		box2dSprite.setAdjustSize(false);
		box2dSprite.setUseOrigin(true);
		box2dSprite.setScale(1f / GameConstants.PIXELS_PER_METER);

		w = box2dSprite.getWidth();
		h = box2dSprite.getHeight();

		box2dSprite.setOrigin(w / 2, h / 2);

		FixtureDef fixDef = new FixtureDef();
		fixDef.density = 10f;
		fixDef.friction = 2f;

		shape = new PolygonShape();
		shape.setAsBox(((w / 2) / GameConstants.PIXELS_PER_METER),
				((h / 2f) / GameConstants.PIXELS_PER_METER), new Vector2(0, 0),
				0);
		fixDef.shape = shape;
		body.createFixture(fixDef);

		box2dSprite.setPosition((-w / 2) + (Box2DUtils.width(body) / 2),
				(-h / 2) + (Box2DUtils.height(body) / 2));

		body.setFixedRotation(true);
		body.setAngularDamping(2f);

		body.setUserData(box2dSprite);

		// playerlight = new PointLight(rayHandler, 100, new Color(1, 1, 1,
		// 0.2f),
		// 8.5f, body.getPosition().x, body.getPosition().y);
		// playerlight.attachToBody(body, 0, 0);

		Vector2 lightPos = new Vector2(body.getPosition().x,
				body.getPosition().y);
		Color lightColor = new Color(0.2f, 0.5f, 0.5f, 0.55f);
		flashlight = new ConeLight(rayHandler, 10, lightColor, 15.0f,
				lightPos.x, lightPos.y, 0, 20.0f);
	}

	public void update(int waterLevel) {

		for (NetworkPlayer p : NetworkData.players.values()) {
			if (p.x != 0 && p.y != 0) {
				body.setTransform(p.x, p.y, 0);
			}

			mouseWorldPos.x = p.mouseX;
			mouseWorldPos.y = p.mouseY;
			lookAngle = MathUtils.atan2(mouseWorldPos.y - getPos().y,
					mouseWorldPos.x - getPos().x) * MathUtils.radDeg;
			flashlight.setDirection(lookAngle);
			flashlight.setPosition(getPos());

			if (p.lastKeyDown != 0 && p.lastKeyDown == Keys.F) {
				flashlightEnabled = !flashlightEnabled;
			}

			if (Globals.lightsEnabled)
				flashlight.setActive(isFlashlightEnabled());
		}
	}

	public void jump() {
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
		}
	}

	public void setGravityScale(float gravity) {
		body.setGravityScale(gravity);
	}

	public void stop() {
		body.setAngularVelocity(0);
		movement = Vector2.Zero;
	}

	public void scale(float amount) {
		box2dSprite.scale(amount);
	}

	public void draw(SpriteBatch batch) {
		batch.enableBlending();
		box2dSprite.draw(batch, body);
	}

	public Box2DSprite getSprite() {
		return box2dSprite;
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
}
