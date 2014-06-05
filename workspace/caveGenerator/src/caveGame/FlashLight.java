package caveGame;

import box2dLight.ConeLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import common.Globals;

public class FlashLight {
	private ConeLight light;
	private float lookAngle = 0;
	public static final float BATTERY_LIFE = 40; // seconds
	public static final float BATTERY_REST_TIME = 3; // seconds
	public static final float NO_BATTERY_FLICKER_TIME = 1; // seconds
	private float rechargeMultiplier = 1.3f;
	private float activeTime = 0;
	private float restTimer = BATTERY_REST_TIME;
	private boolean hasBattery = true;

	public boolean hasBattery() {
		return hasBattery;
	}

	public FlashLight(RayHandler rayHandler, Color lightColor, Vector2 lightPos) {
		light = new ConeLight(rayHandler, 100, lightColor, 15.0f, lightPos.x,
				lightPos.y, 0, 20.0f);
		light.setActive(false);
		
	}
	
	public FlashLight(RayHandler rayHandler, Color lightColor, Body body) {
		light = new ConeLight(rayHandler, 100, lightColor, 15.0f, 0,
				0, 0, 20.0f);
		light.attachToBody(body, 0, 0);
		light.setActive(false);
	}

	public void update(float deltaTime, Vector2 lightPos, Vector2 mouseWorldPos) {

		if (light.isActive()) {
			lookAngle = MathUtils.atan2(mouseWorldPos.y - light.getY(),
					mouseWorldPos.x - light.getX()) * MathUtils.radDeg;
			light.setDirection(lookAngle);
//			light.setPosition(lightPos);

			if (activeTime + deltaTime < BATTERY_LIFE) {
				activeTime += deltaTime;

				if (activeTime >= BATTERY_LIFE - NO_BATTERY_FLICKER_TIME) {
					// light should flicker before going out
					setActive(Globals.random.nextBoolean());
				}
			} else {
				// battery is dead. deactivate light
				activeTime = BATTERY_LIFE;
				restTimer = 0;
				setActive(false);
				hasBattery = false;
			}
		} else {
			if (restTimer < BATTERY_REST_TIME) {
				restTimer += deltaTime;
			} else {
				if (activeTime > 0) {
					activeTime -= (deltaTime * rechargeMultiplier);
				} else {
					activeTime = 0;
				}
			}
		}

		if (activeTime < BATTERY_LIFE) {
			hasBattery = true;
		}
	}

	public void setActive(boolean active) {
		light.setActive(active);
	}

	public float getActiveTime() {
		return activeTime;
	}
}
