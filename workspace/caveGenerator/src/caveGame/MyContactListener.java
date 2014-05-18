package caveGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import common.Globals;
import common.Globals.FixtureUserData;

public class MyContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		if ((Globals.FixtureUserData) fixtureA.getUserData() == FixtureUserData.PLAYER_FOOT_SENSOR) {
			Globals.numOfFootContacts++;
		}

		if ((Globals.FixtureUserData) fixtureB.getUserData() == FixtureUserData.PLAYER_FOOT_SENSOR) {
			Globals.numOfFootContacts++;
		}
		// System.out.println("Begin contact between " + fixtureA.toString() +
		// " and " + fixtureB.toString());
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		if ((Globals.FixtureUserData) fixtureA.getUserData() == FixtureUserData.PLAYER_FOOT_SENSOR) {
			Globals.numOfFootContacts--;
		}

		if ((Globals.FixtureUserData) fixtureB.getUserData() == FixtureUserData.PLAYER_FOOT_SENSOR) {
			Globals.numOfFootContacts--;
		}
		// System.out.println("End contact between " + fixtureA.toString() +
		// " and " + fixtureB.toString());
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
