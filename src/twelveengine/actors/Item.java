package twelveengine.actors;

import java.util.ArrayList;

import com.bulletphysics.linearmath.Transform;

import twelveengine.Game;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveengine.graphics.TrianglePacket;
import twelveengine.physics.BulletRigidBody;
import twelveutil.MathUtil;

public class Item extends RigidBody {
	public Actor owner;
	public boolean autoPick; //If a player walks over this they will automatically pick it up.
	
	public Item(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Item";
		
		autoPick = tag.getProperty("autopickup", false);
		owner = null;
	}
	
	public void step() {
		if(owner == null)
			super.step();
		else
			held();
	}
	
	//If this item is in somethings inventory and a game tick passes we call this method. It's to update it's location and to perform various actions on it.
	public void held() {
		
	}
	
	//When this item i picked up into somethings inventory call this method.
	public void picked(Actor a) {
		owner = a;
		int i = 0;
		while(i < physics.length) {
			physics[i].destroy();
			i++;
		}
	}
	
	//When this item is dropped out of somethings inventory call this method. v = vector to drop the item, magnitude of v = velocity
	public void drop(Vertex l, Vertex v, Quat r) {
		createPhysicsObject(tag.getObject("collision"));
		setLocation(l);
		setVelocity(v);
		setRotation(r);
		location = l;
		rotation = r;
		lastFrame();
		owner = null;
	}
	
	public void draw(ArrayList<TrianglePacket> meshes, float f) {
		if(owner == null)
			super.draw(meshes, f);
	}

}
