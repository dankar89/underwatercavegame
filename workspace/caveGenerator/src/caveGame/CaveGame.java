package caveGame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import common.Assets;

public class CaveGame extends Game {
	boolean firstTimeCreate = true;

	@Override
	public void create() {
//		String str = "danielärbäst";
//		MathUtils.random.setSeed(Globals.randSeed.hashCode());
//		System.out.println();
//		Settings.load();
		Assets.load();
		if (Assets.isReady())
			setScreen(new MainMenuScreen(this));
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}
	
	public void quitGame() {		
		Gdx.app.exit();
	}

	@Override
	public void dispose() {
		super.dispose();
		Assets.dispose();
		getScreen().dispose();		
	}
}
