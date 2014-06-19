package twelveengine.physics;

import twelveengine.actors.*;
import twelveengine.data.*;

import com.bulletphysics.dynamics.RigidBodyConstructionInfo;

public class BulletRigidBody extends com.bulletphysics.dynamics.RigidBody {
	
	public Actor owner;
	public String parent;
	public Vertex offset;
	public Quat orientation;

	public BulletRigidBody(RigidBodyConstructionInfo constructionInfo) {
		super(constructionInfo);
		owner = null;
	}
	
	public void setOwner(Actor a) {
		owner = a;
	}
	
	public Actor getOwner() {
		return owner;
	}
	
	public boolean hasOwner() {
		return owner != null;
	}

}
