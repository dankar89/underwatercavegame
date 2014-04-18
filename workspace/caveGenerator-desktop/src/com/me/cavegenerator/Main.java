package com.me.cavegenerator;

import caveGame.CaveGame;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import common.Globals;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "CaveGenerator";
//		cfg.useGL20 = false;
		cfg.useGL20 = true;
//		cfg.fullscreen = true;
//		cfg.width = 1024;
//		cfg.height = 768;
		cfg.width = 1920;
		cfg.height = 1080;
		
		new LwjglApplication(new CaveGame(), cfg);
	}
}
