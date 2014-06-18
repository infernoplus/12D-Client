package twelveengine.physics;

import com.bulletphysics.collision.shapes.CollisionShape;

public class ParentedShape {
	
	public CollisionShape shape;
	public String parent;
	
	//This is just so I can pass the name of the parent and the collisionshape object at the same time. Less messy this way.
	public ParentedShape(CollisionShape shp, String prnt) {
		shape = shp;
		parent = prnt;
	}
	
}