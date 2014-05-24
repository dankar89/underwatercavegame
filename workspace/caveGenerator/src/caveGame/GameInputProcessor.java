package caveGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import common.Globals;

public class GameInputProcessor implements InputProcessor {	
	
	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.F1:
			Globals.debug = !Globals.debug;
			break;
		case Keys.F2:
			Globals.hideUI = !Globals.hideUI;
			break;
		case Keys.F3:
			Globals.lightsEnabled = !Globals.lightsEnabled;
			break;
		case Keys.F4:
			Globals.isAndroid = !Globals.isAndroid;
			break;
		case Keys.F5:
			Globals.fullscreen = !Globals.fullscreen;
		case Keys.ESCAPE:
//			Gdx.app.exit();			
			break;
		case Keys.M:
			break;
		default:
			return false;			
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		Globals.cameraZoom += amount;
		Globals.cameraZoom = MathUtils.clamp(Globals.cameraZoom, 1, 10);
		return true;
	}

}
