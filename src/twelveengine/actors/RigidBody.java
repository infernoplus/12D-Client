package twelveengine.actors;

import java.util.ArrayList;

import javax.vecmath.*;

import com.bulletphysics.BulletGlobals;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.dynamics.constraintsolver.*;

import twelveengine.Game;
import twelveengine.Log;
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
		contrain();
	}
	
	//Creates and adds this actors physics object to the bullet physics method.
	public void createPhysicsObject(TagSubObject pt) {
		int i = 0;
		ParentedShape[] shps = buildCollisionShape(pt);
		physics = new BulletRigidBody[shps.length];
		while(i < shps.length) {
			TagSubObject cob = pt.getObject(i);
			Transform startTransform = new Transform();
			startTransform.setIdentity();
			startTransform.origin.set(0, 0, 0f);
			physics[i] = game.bsp.bullet.createDynamicRigidBody(mass, new Transform(), shps[i].shape, shps[i].parent, "object");
			Frame fr = getParentFrame(physics[i].parent);
			physics[i].centerOffset = shps[i].offset;
			physics[i].boneOffset = fr.location.copy();
			physics[i].boneOrientation = fr.rotation.copy();
			//Log.log("P/c " + fr.name + "/" + physics[i].parent + " loc/rot " + MathUtil.toString(physics[i].boneOffset) + "/" + MathUtil.toString(physics[i].boneOrientation), "Physics");
			physics[i].setLocation(new Vertex());
			physics[i].setRotation(new Quat());
			physics[i].setOwner(this);
			physics[i].tag = cob;
			i++;
		}
	}
	
	public void contrain() {
		int i = 0;
		while(i < physics.length) {
			//Now we apply constraints if they are defined in the tag!
			if(!physics[i].tag.getProperty("constraint", "none").equals("none")) {
				TagSubObject limits = physics[i].tag.getObject("limits");
				BulletRigidBody parent = getBRBody(limits.getProperty("parent", "null"));
				if(parent != null) {
					Transform tfma = new Transform();
					Transform tfmb = new Transform();
					tfma = parent.getCenterOfMassTransform(tfma);
					tfmb = physics[i].getCenterOfMassTransform(tfmb);
					//tfma.inverse(tfma);
					//tfma.inverse(tfmb);
					//Generic6DofConstraint constraint = new Generic6DofConstraint(parent, physics[i], tfma, tfmb, true);
					//HingeConstraint constraint = new HingeConstraint(physics[i], new Vector3f(-5,-200,-30), new Vector3f(0,1,0));
					//ConeTwistConstraint constraint = new com.bulletphysics.dynamics.constraintsolver.ConeTwistConstraint(rbA, rbAFrame);
					Point2PointConstraint constraint = new Point2PointConstraint(physics[i], parent, new Vector3f(0,0,-1), new Vector3f(0,0,-1));
					/*Vector3f tmp = new Vector3f(-BulletGlobals.SIMD_PI * 0.3f, -BulletGlobals.FLT_EPSILON, -BulletGlobals.SIMD_PI * 0.3f);
					constraint.setAngularLowerLimit(tmp);
					tmp = new Vector3f(BulletGlobals.SIMD_PI * 0.5f, BulletGlobals.FLT_EPSILON, BulletGlobals.SIMD_PI * 0.3f);
					constraint.setAngularUpperLimit(tmp);
					constraint.setLinearLowerLimit(new Vector3f(-1,-1,-1));
					constraint.setLinearUpperLimit(new Vector3f(1,1,1));*/
					game.bsp.bullet.getDynamicsWorld().addConstraint(constraint, true);
					Log.log("Failed to constrain physics object: " + physics[i].tag.getProperty("parent", "null") + "+" + parent.parent + " in tag: " + tag.file, "Physics", 2);
				}
				else {
					Log.log("Failed to constrain physics object: " + physics[i].tag.getProperty("parent", "null") + " in tag: " + tag.file, "Physics", 2);
				}
			}
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
		//Get current state of teh root bullet physics object and apply it to this actor
		location = physics[root].getLocation();
		rotation = physics[root].getRotation();
		velocity = physics[root].getVelocity();
	}
	
	//Originally in ragdoll but I moved it here so I could also use this same method to find the default offset of physics objects and save it in the BulletRigidBody object.
	public Frame getParentFrame(String name) {
		int i = 0;
		while(i < model.models[0].fr.length) {
			if(model.models[0].fr[i].name.equals(name)) {
				//Log.log(model.models[0].fr[i].name + " = " + model.models[0].fr[i].name, "Animation");
				return model.models[0].fr[i];
			}
			i++;
		}
		//Log.log(model.models[0].fr[0].name + " = " + model.models[0].fr[i].name, "Animation");
		return model.models[0].fr[0];
	}
	
	public BulletRigidBody getBRBody(String n) {
		int i = 0;
		while(i < physics.length) {
			if(physics[i] == null) {
				return null;
			}
			if(physics[i].parent.equals(n)) {
				return physics[i];
			}
			i++;
		}
		return null;
	}
}