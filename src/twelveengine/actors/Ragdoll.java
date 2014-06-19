package twelveengine.actors;

import java.util.ArrayList;

import javax.vecmath.*;

import com.bulletphysics.linearmath.Transform;

import twelveengine.Game;
import twelveengine.Log;
import twelveengine.data.*;
import twelveengine.graphics.*;
import twelveengine.physics.*;
import twelveutil.*;

public class Ragdoll extends RigidBody {	
	//A ragdoll is a ragdoll. 
	//The ragdoll tag must define a rigged model, collision shapes to attach to the bones of that model and IK constraints for those bones.
	//Ragdoll handles location/velocity/rotation values in a special way.
	public Ragdoll(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		animate = true;
	}
	
	public void step() {
		super.step();
		animation();
		physics();
		hitboxUpdate();
	}
	
	//TODO: setup a system to define which marker is root.
	//Overridden because in a ragdoll we take the physics objects and skin the model to them basically.
	public void animation() {
		int i = 0;
		AnimationFrame nufrm = new AnimationFrame(model.models[0].fr.length);
		while(i < nufrm.nodes.length) {
			nufrm.nodes[i] = new AnimationNode(i);
			i++;
		}
		i = 0;
		while(i < physics.length) {
			//Get current state of bullet physics object
			Vector3f c = physics[i].getCenterOfMassPosition(new Vector3f());
			Quat4f q = physics[i].getOrientation(new Quat4f());
			//Apply it to animationnode.
			Vertex l = new Vertex(c.x, c.y, c.z);
			Quat r = new Quat(q.x, q.y, q.z, q.w);
			int ind = getParentFrame(physics[i].parent).index;
			//Log.log(physics[i].parent + " @ " + MathUtil.toString(l) + " : " + MathUtil.toString(r) + " - Ind: " + ind, "Animation");
			nufrm.nodes[ind] = new AnimationNode(ind+1, MathUtil.subtract(l, location), r);
			i++;
		}
		lastFrame = frame;
		frame = nufrm;
	}
	
	//TODO: overiring these functions in physical, dunno if ill change this later
	public void move(Vertex a) { 
		int i = 0;
		while(i < physics.length) {
			physics[i].translate(new Vector3f(a.x, a.y, a.z));
			physics[i].activate();
			Vector3f c = physics[i].getCenterOfMassPosition(new Vector3f());
			location = new Vertex(c.x, c.y, c.z);
			i++;
		}
	}
	
	public void rotate(Quat a) {
		Quat4f q = physics[0].getOrientation(new Quat4f());
		//TODO: IDR how to rotate a rotation...
	}
	
	public void push(Vertex a) {
		int i = 0;
		while(i < physics.length) {
			Vector3f v = new Vector3f();
			v = physics[i].getLinearVelocity(v);
			Vector3f x = new Vector3f(a.x + v.x, a.y + v.y, a.z + v.z);
			physics[i].setLinearVelocity(x);
			physics[i].activate();
			Vector3f e = physics[i].getLinearVelocity(new Vector3f());
			velocity = new Vertex(e.x, e.y, e.z);
			i++;
		}
	}
	
	public void setLocation(Vertex a) {
		int i = 0;
		while(i < physics.length) {
			Vector3f p = new Vector3f();
			p = physics[i].getCenterOfMassPosition(p);
			physics[i].translate(new Vector3f(a.x - p.x + physics[i].offset.x, a.y - p.y + physics[i].offset.y, a.z - p.z + physics[i].offset.z));
			physics[i].activate();
			Vector3f c = physics[i].getCenterOfMassPosition(new Vector3f());
			location = new Vertex(c.x, c.y, c.z);
			Log.log(physics[i].parent + " @ " + MathUtil.toString(location) + " : " + "null" + " - Ind: " + i, "Animation");
			i++;
		}
	}
	
	public void setRotation(Quat a) {
		int i = 0;
		while(i < physics.length) {
			Transform tr = new Transform();
			tr = physics[i].getCenterOfMassTransform(tr);
			tr.setRotation(new Quat4f(a.x, a.y, a.z, a.w));
			physics[i].setCenterOfMassTransform(tr);
			Quat4f q = physics[i].getOrientation(new Quat4f());
			rotation = new Quat(q.x, q.y, q.z, q.w);
			i++;
		}
	}
	
	public void setVelocity(Vertex a) {
		int i = 0;
			while(i < physics.length) {
			Vector3f x = new Vector3f(a.x, a.y, a.z);
			physics[i].setLinearVelocity(x);
			physics[i].activate();
			Vector3f e = physics[i].getLinearVelocity(new Vector3f());
			velocity = new Vertex(e.x, e.y, e.z);
			i++;
		}
	}
	
}