package twelveengine.actors;

import java.util.ArrayList;

import javax.vecmath.*;

import com.bulletphysics.linearmath.Transform;

import twelveengine.Game;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveengine.graphics.TrianglePacket;
import twelveengine.physics.BulletRigidBody;
import twelveengine.physics.ParentedShape;
import twelveutil.MathUtil;
import twelveutil.TagSubObject;

public class Scenery extends Physical {	
	public Scenery(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Scenery";
		createPhysicsObject(tag.getObject("collision"));
		setLocation(l);
		setRotation(r);
		setVelocity(v);
	}
	
	public void createPhysicsObject(TagSubObject pt) {
		int i = 0;
		ParentedShape[] shps = buildCollisionShape(pt);
		physics = new BulletRigidBody[shps.length];
		while(i < shps.length) {
			Transform startTransform = new Transform();
			startTransform.setIdentity();
			startTransform.origin.set(0, 0, 0f);
			physics[i] = game.bsp.bullet.createStaticRigidBody(new Transform(), shps[i].shape, shps[i].parent, "world");
			physics[i].setOwner(this);
			i++;
		}
	}
	
	//Normal animation system
	public void step() {
		super.step();
		animation();
		effects();
		physics();
		hitboxUpdate();
	}
	
	//TODO: this. 
	public void physics() {	//TODO:Using physics[0] for the location of the model for now.
		//Get current state of bullet physics object
		Vector3f c = physics[0].getCenterOfMassPosition(new Vector3f());
		Vector3f v = physics[0].getLinearVelocity(new Vector3f());
		Quat4f q = physics[0].getOrientation(new Quat4f());
		//Apply it to actor.
		location = new Vertex(c.x, c.y, c.z);
		rotation = new Quat(q.x, q.y, q.z, q.w);
		velocity = new Vertex(v.x, v.y, v.z);
	}
	
	/*int j = 0;
	public void step() {
		lastFrame();
		lastFrame = frame;
		
		if(animate) {
			if(frame < animation.frames.length-1)
				frame++;
			else {
				if(j < animations.animations.length-1) {
					j++;
				}
				else
					j = 0;
				animation = animations.animations[j];
				frame = 0;
				lastFrame = 0;
			}
		}
		int i = 0;
		while(i < effects.size()) {
			effects.get(i).step();
			i++;
		}
	}*/
	
	/*public void step() {
		if(d < 1.0f) {
			d += 0.01f;
		}
		else {
			//System.out.println("frame: " + i + " nxt " + j);
			d = 0;
			if(i < 3) {
				i++;
			}
			else {
				i = 0;
			}
			
			if(j < 3) {
				j++;
			}
			else {
				j = 0;
			}
			System.out.println("frame " + (30+i) + " : " + "node:" + model.fr[11].name + " rot@ " + MathUtil.toString(animations.animations[0].frames[30+i].nodes[11].rotation));
		}
			model.setAnimation(MathUtil.interpolateFrame(animations.animations[0].frames[30+i], animations.animations[0].frames[30+j], d));
			//model.setAnimation(animations.animations[0].frames[j]);
	}*/
	
	/*public void step() {
		lastFrame();
		if(d < 1.0f) {
			d += 0.05f;
		}
		else {
			if(k < 0) {
				k++;
			}
			else {
				k = 0;
				d = 0;
				
				if(i < animations.animations[0].frames.length-1) {
					i++;
				}
				else {
					i = 0;
				}
				
				if(j < animations.animations[0].frames.length-1) {
					j++;
				}
				else {
					j = 0;
				}
			}
		}
		if(k == 0)
			model.setAnimation(MathUtil.interpolateFrame(animations.animations[0].frames[i], animations.animations[0].frames[j], d));
		else
			model.setAnimation(animations.animations[0].frames[j]);
	}*/
	
	/*int i = 0;
	int j = 1;
	int k = 0;
	boolean b = false;
	
	public void step() {
		if(k < 45) {
			k++;
		}
		else {
			if(i < animations.animations[0].frames.length-1)
				i++;
			else
				i = 0;
			if(j < animations.animations[0].frames.length-1)
				j++;
			else
				j = 0;
			
			k = 0;
		}
		
		b = !b;
		
		if(b)
			model.setAnimation(MathUtil.interpolateFrame(animations.animations[0].frames[i], animations.animations[0].frames[j], 0.0f));
		else
			model.setAnimation(animations.animations[0].frames[i]);
	}*/
	
}
