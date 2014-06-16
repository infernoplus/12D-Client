package twelveengine.actors;


import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;

import twelveengine.Game;
import twelveengine.Log;
import twelveengine.data.*;
import twelveengine.graphics.GraphicsCore;
import twelveengine.physics.BulletRigidBody;
import twelveutil.MathUtil;

public class Hitscan extends Actor {

	public Actor hit = null;
	
	public float range;
	public int lifeSpan;
	
	public float damage;
	
	public Hitscan(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Hitscan";
		
		range = tag.getProperty("range", 1000f);
		lifeSpan = tag.getProperty("lifespan", 10);
		damage = tag.getProperty("damage", 5);
		
		trace();
	}
	
	public void step() {
		lastFrame();
		if(lifeSpan > 0)
			lifeSpan--;
		else
			dead = true;
	}
	
	//This function casts a ray out to see if this hitscan object hits anything.
	public void trace() {
		Vertex e = MathUtil.add(location, MathUtil.multiply(velocity, range));
		ClosestRayResultCallback r = game.bsp.bullet.trace(MathUtil.bConvert(location), MathUtil.bConvert(e));
		
		if(r.hasHit()) {
		    Vector3f end = r.hitPointWorld;
		    Vector3f nrm = r.hitNormalWorld;
		    if(r.collisionObject instanceof BulletRigidBody) {
		    	BulletRigidBody b = (BulletRigidBody) r.collisionObject;
		    	if(b.hasOwner())
		    		Log.log("Shot hit: " + b.getOwner().getName(), "Physics");
		    }
		    
		    GraphicsCore.debug0 = new Vertex(r.rayFromWorld.x, r.rayFromWorld.y, r.rayFromWorld.z-0.1f);
		    GraphicsCore.debug1 = new Vertex(r.hitPointWorld.x, r.hitPointWorld.y, r.hitPointWorld.z);
		    
		    /*GraphicsCore.debug0 = location;
		    GraphicsCore.debug1 = MathUtil.add(location, velocity);*/
		    
		    //System.out.println("hit something! p = " + end.toString() + " nrml = " + nrm.toString() + " obj = " + r.collisionObject.toString());
		}
		else {
			
		}
	}

	public void draw(float f) {

	}
}