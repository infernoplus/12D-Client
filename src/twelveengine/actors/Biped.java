package twelveengine.actors;

import java.util.ArrayList;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.Transform;

import twelveengine.Game;
import twelveengine.data.Collision;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveengine.graphics.TrianglePacket;
import twelveengine.physics.*;
import twelveutil.MathUtil;

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
		
		createPhysicsObject();
		setLocation(l);
		setRotation(r);
		setVelocity(v);
	}
	
	//Creates and adds this actors physics object to the bullet physics method.
	//TODO: Why exactly did I decided to call them both RigidBody.....
	public void createPhysicsObject() {
		BulletRigidBody r = game.bsp.bullet.createDynamicRigidBody(mass, new Transform(), collision, "player");
		r.setSleepingThresholds(0.0f, 0.0f);
		r.setAngularFactor(0.0f);
		r.setFriction(friction);
		r.setOwner(this);
		physics = r;
	}
	
	public void step() {
		super.step();
		if(dead)
			return;
		physics();
		hitboxUpdate();
	}
	
	//TODO: this.
	public void physics() {
		//Get current state of bullet physics object
		Vector3f c = physics.getCenterOfMassPosition(new Vector3f());
		Vector3f v = physics.getLinearVelocity(new Vector3f());
		Quat4f q = physics.getOrientation(new Quat4f());
		//Apply it to actor.
		location = new Vertex(c.x, c.y, c.z);
		rotation = new Quat(q.x, q.y, q.z, q.w);
		velocity = new Vertex(v.x, v.y, v.z);
		
		onGround = game.bsp.bullet.isOnGround(physics, new Vector3f(0,0,-3.0f));
		
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
	
	public void destroy() {
		super.destroy();
		physics.destroy();
	}

}
