package slaynash.sgengine.world2d;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;

import slaynash.sgengine.models.Renderable2dModel;
import slaynash.sgengine.shaders.ShaderManager;
import slaynash.sgengine.utils.MatrixUtils;
import slaynash.sgengine.world2d.CollisionManager2d;

public class Model2dWorld {

	private Renderable2dModel model;
	private Vec2 position;

	public Model2dWorld(Vec2[] collisionBounds, Vec2 pos, Renderable2dModel model) {
		this.model = model;
		this.position = pos;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.setPosition(pos);
		
		Body body = CollisionManager2d.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		shape.set(collisionBounds, collisionBounds.length);
		
		body.createFixture(shape, 0);
	}

	public void render() {
		ShaderManager.shader2d_loadTransformationMatrix(MatrixUtils.createTransformationMatrix(position, 1));
		model.render();
	}

}
