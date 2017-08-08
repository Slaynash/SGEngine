package slaynash.sgengine.playercharacters;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;

import slaynash.sgengine.inputs.ControllersControlManager;
import slaynash.sgengine.inputs.KeyboardControlManager;
import slaynash.sgengine.world2d.CollisionManager2d;

public class PlayerCharacter2d extends PlayerCharacter {
	
	private Body body;
	
	private int footsContact = 0;

	private boolean jumped;

	public PlayerCharacter2d(){
		BodyDef bodyDef = new BodyDef();
		bodyDef.setType(BodyType.DYNAMIC);
		bodyDef.setPosition(new Vec2(0, 1));
		body = CollisionManager2d.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.8f, 1f);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.5f; 
		fixtureDef.friction = 0.4f;
		
		body.createFixture(fixtureDef);
		body.setFixedRotation(true);
		body.setSleepingAllowed(false);
		
		PolygonShape sensorShape = new PolygonShape();
		sensorShape.setAsBox(0.79f, 0.05f, new Vec2(0, -1f), 0);
		
		FixtureDef sensor = new FixtureDef();
		sensor.isSensor = true;
		sensor.shape = sensorShape;
		Fixture f = body.createFixture(sensor);
		f.setUserData(3);
		
		CollisionManager2d.setContactListener(new ContactListener() {
			
			@Override
			public void preSolve(Contact arg0, Manifold arg1) {
				
			}
			
			@Override
			public void postSolve(Contact arg0, ContactImpulse arg1) {
				
			}
			
			@Override
			public void endContact(Contact arg0) {
				if((arg0.m_fixtureA.getUserData() != null && arg0.m_fixtureA.getUserData().equals(3)) || (arg0.m_fixtureB.getUserData() != null && arg0.m_fixtureB.getUserData().equals(3))){
					footsContact--;
				}
			}
			
			@Override
			public void beginContact(Contact arg0) {
				if((arg0.m_fixtureA.getUserData() != null && arg0.m_fixtureA.getUserData().equals(3)) || (arg0.m_fixtureB.getUserData() != null && arg0.m_fixtureB.getUserData().equals(3))){
					footsContact++;
				}
			}
		});
	}
	
	@Override
	public void update() {
		position.x = body.getPosition().x;
		position.y = body.getPosition().y;
		viewPosition.x = body.getPosition().x;
		viewPosition.y = body.getPosition().y;
		
		if(KeyboardControlManager.isPressed("left")){
			if(body.getLinearVelocity().x > -3) body.applyLinearImpulse(new Vec2(-0.80f, 0), new Vec2(position.x, position.y), true);
		}
		else if(KeyboardControlManager.isPressed("right")){
			if(body.getLinearVelocity().x < 3) body.applyLinearImpulse(new Vec2(0.80f, 0), new Vec2(position.x, position.y), true);
		}
		else if(ControllersControlManager.getValue(0, "right") != 0){
			if((ControllersControlManager.getValue(0, "right") < 0 && body.getLinearVelocity().x > -3) || (ControllersControlManager.getValue(0, "right") > 0 && body.getLinearVelocity().x < 3))
				body.applyLinearImpulse(new Vec2(ControllersControlManager.getValue(0, "right")*0.80f, 0), new Vec2(position.x, position.y), true);
		}
		else{
			body.setLinearVelocity(new Vec2(0, body.m_linearVelocity.y));
		}
		
		if(KeyboardControlManager.isPressed("jump") || ControllersControlManager.isPressed(0, "jump")){
			if(footsContact != 0 && !jumped) body.applyLinearImpulse(new Vec2(0, 3), new Vec2(position.x, position.y), true);
			jumped = true;
		}
		if(footsContact != 0 && jumped){
			jumped = false;
		}
	}
	
	
	
}
