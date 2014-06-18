package twelveengine.actors;

import java.util.ArrayList;

import javax.vecmath.*;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.linearmath.Transform;

import twelveengine.Game;
import twelveengine.data.*;
import twelveengine.graphics.*;
import twelveengine.physics.*;
import twelveutil.*;

public class RigidBody extends Physical {	
	//A rigidboy actor is an actor that has a physics model and uses bulletphysics.
	//Extend this if you want rigid body physics on an actor.
	public RigidBody(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		
		createPhysicsObject(tag.getObject("collision"));
		setLocation(l);
		setRotation(r);
		setVelocity(v);
	}
	
	//Creates and adds this actors physics object to the bullet physics method.
	public void createPhysicsObject(TagSubObject pt) {
		int i = 0;
		ParentedShape[] shps = buildCollisionShape(pt);
		physics = new BulletRigidBody[shps.length];
		while(i < shps.length) {
			Transform startTransform = new Transform();
			startTransform.setIdentity();
			startTransform.origin.set(0, 0, 0f);
			physics[i] = game.bsp.bullet.createDynamicRigidBody(mass, new Transform(), shps[i].shape, shps[i].parent, "object");
			physics[i].setOwner(this);
			i++;
		}
	}
	
	public void step() {
		super.step();
		animation();
		physics();
		hitboxUpdate();
	}
	
	//TODO: this.
	//TODO:Using physics[0] for the location of the model for now.
	public void physics() {
		if(physics == null)
			return;
		//Get current state of bullet physics object
		Vector3f c = physics[0].getCenterOfMassPosition(new Vector3f());
		Vector3f v = physics[0].getLinearVelocity(new Vector3f());
		Quat4f q = physics[0].getOrientation(new Quat4f());
		//Apply it to actor.
		location = new Vertex(c.x, c.y, c.z);
		rotation = new Quat(q.x, q.y, q.z, q.w);
		velocity = new Vertex(v.x, v.y, v.z);
	}
	
	public void draw(ArrayList<TrianglePacket> meshes, float f) {	
		Vertex l = MathUtil.lerp(lastLocation, location, f);
		Quat r = rotation;
		if(animate && frame != null && lastFrame != null) //TODO: really nesscary to check all this?
			model.pushToDrawQueue(meshes, l, new Quat(0,0,0,1), MathUtil.interpolateFrame(lastFrame, frame, f), scale);
		else
			model.pushToDrawQueue(meshes, l, new Quat(0,0,0,1), scale);
		int i = 0;
		while(i < effects.size()) {
			effects.get(i).draw(meshes, f);
			i++;
		}
	}
	
}