package twelveengine.actors;

import java.util.ArrayList;

import twelveengine.Game;
import twelveengine.data.PhysTriangle;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveengine.graphics.TrianglePacket;
import twelveutil.MathUtil;

public class Projectile extends Physical {
	public Actor owner;
	
	public boolean expended = false;
	
	public int life;
	
	public float damage;
	public float damageRadius;
	public float force;
	
	
	public Projectile(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Projectile";
		
		owner = null;
		
		life = tag.getProperty("lifespan", 90);
		damage = tag.getProperty("damage", 10f);
		damageRadius = tag.getProperty("damageradius", 10f);
		force = tag.getProperty("force", 5f);
	}
	
	public void step() {
		lastFrame();
		if(!expended) {
			move();
			projectile();
		}
	}
	
	//Simulates physics without collision, this is just for when there is no packet and we need a guess at where the next step is.
	public void move() {
		setVelocity(MathUtil.multiply(velocity, drag));
		setLocation(MathUtil.add(location, velocity));
	}
	
	public void projectile() {
		if(life > 0) {
			life--;
		}
		else {
			detonate();
		}
	}
	
	public void detonate() {
		expended = true;
	}
	
	public float dampen(Vertex v, PhysTriangle t) {
		float d = MathUtil.normalSteep(MathUtil.normalize(MathUtil.inverse(v)), t);
		return d;
	}
	
	public void draw(ArrayList<TrianglePacket> meshes, float f) {	
		if(!expended) {
				Vertex l = MathUtil.lerp(lastLocation, location, f);
				Quat r = MathUtil.slerpQuat(lastRotation, rotation, f);
				model.pushToDrawQueue(meshes, l, r, scale);
		}
	}
}
