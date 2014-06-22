package twelveengine.actors;

import java.util.ArrayList;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.linearmath.Transform;

import twelveengine.Game;
import twelveengine.data.Collision;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveengine.graphics.TrianglePacket;
import twelveengine.physics.*;
import twelveutil.MathUtil;
import twelveutil.TagSubObject;

public class Biped extends Physical {	
	public Vertex move;
	public Vertex look;
	
	public float maxHealth;
	public float health;
	
	public Vertex moving;
	
	public boolean jumping = false;
	public boolean onGround = false;
	public Collision ground = null;
	
	public float eye;
	
	public float moveSpeed;
	public float acceleration;
	
	public float jumpHeight;
	public float airControl;
	public float friction;
	
	public boolean primary = false;
	public boolean secondary = false;
	
	public Biped(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Biped";
		
		move = new Vertex(0, 1, 0);
		look = new Vertex(0, 1, 0);
		moving = new Vertex();
		
		eye = tag.getProperty("eye", 3f);
		
		jumpHeight = tag.getProperty("jumpheight", 1.5f);
		airControl = tag.getProperty("aircontrol", 0.08f);
		friction = tag.getProperty("friction", 1.0f);
		
		moveSpeed = tag.getProperty("movespeed", 3.0f);
		acceleration = tag.getProperty("acceleration", 0.9f);
		
		maxHealth = tag.getProperty("maxhealth", 100f);
		health = maxHealth;
		
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
			physics[i] = game.bsp.bullet.createDynamicRigidBody(mass, new Transform(), shps[i].shape, shps[i].parent, "player");
			physics[i].centerOffset = shps[i].offset;
			physics[i].setSleepingThresholds(0.0f, 0.0f);
			physics[i].setAngularFactor(0.0f);
			physics[i].setFriction(friction);
			physics[i].setOwner(this);
			i++;
		}
	}
	
	public void step() {
		super.step();
		if(dead)
			return;
		animation();
		physics();
		hitboxUpdate();
	}
	
	//TODO: this.
	public void physics() { 	//TODO:Using physics[0] for the location of the model for now.
		//Get current state of bullet physics object
		Vector3f c = physics[0].getCenterOfMassPosition(new Vector3f());
		Vector3f v = physics[0].getLinearVelocity(new Vector3f());
		Quat4f q = physics[0].getOrientation(new Quat4f());
		//Apply it to actor.
		location = new Vertex(c.x, c.y, c.z);
		rotation = new Quat(q.x, q.y, q.z, q.w);
		velocity = new Vertex(v.x, v.y, v.z);
		
		onGround = game.bsp.bullet.isOnGround(physics[0], new Vector3f(0,0,-3.0f));
		
		Vertex x = new Vertex(v.x, v.y, v.z);
		float i = MathUtil.magnitude(x);
		float k = MathUtil.magnitude(MathUtil.add(x, moving));
		//System.out.println("velocity " + MathUtil.toString(velocity) + " | magnitude i: " + i + " magnitude k: " + k + " movespeed: " + moveSpeed + " | push force: " + MathUtil.toString(moving));
		
		//TODO: k for now but needs lots of work to make it feel right
		if(i >= moveSpeed)
			;
		else
			push(moving);
		
		moving = new Vertex();
	}
	
	public void movement(Vertex a) {
		if(MathUtil.magnitude(a) <= 0)
			return;
		Vertex m = new Vertex();
		if(onGround) {
			m = MathUtil.add(m, moveOnLookX(move, a.x));
			m = MathUtil.add(m, moveOnLookY(move, a.y));
			m = MathUtil.multiply(MathUtil.normalize(m), acceleration);
		}
		else {		
			m = MathUtil.add(m, moveOnLookX(move, a.x));
			m = MathUtil.add(m, moveOnLookY(move, a.y));
			m = MathUtil.multiply(MathUtil.multiply(MathUtil.normalize(m), acceleration), airControl);
		}
		moving = m;
	}
	
	public Vertex moveOnLookX(Vertex b, float d) {
		Vertex c = MathUtil.normalize(MathUtil.multiply(b, new Vertex(d,d,d)));
		return c;
	}
	
	public Vertex moveOnLookY(Vertex b, float d) {
		Vertex e = new Vertex(-b.y, b.x, b.z);
		Vertex c = MathUtil.normalize(MathUtil.multiply(e, new Vertex(d,d,d)));
		return c;
	}
	
	public void jump() {
		if(onGround) {
			if(velocity.z < 0)
				setVelocity(new Vertex(velocity.x, velocity.y, 0));
			push(new Vertex(0, 0, jumpHeight*2));
			jumping = true;
		}
	}
	
	public void setHealth(float h) {
		health = h;
	}
	
	public void invoke(String m, String p[]) {
		super.invoke(m, p);
		
		if(m.equals("health")) {
			setHealth(Float.parseFloat(p[0]));
			return;
		}
		if(m.equals("jump")) {
			jump();
			return;
		}
		if(m.equals("jumpheight")) {
			jumpHeight = Float.parseFloat(p[0]);
		}
		if(m.equals("movespeed")) {
			moveSpeed = Float.parseFloat(p[0]);
		}
	}

}
