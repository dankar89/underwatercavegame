package caveGame;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;

public class Box2dBodyPool extends Pool<Body>{

	@Override
	protected Body newObject() {
		return null;
	}

}
