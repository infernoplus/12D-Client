package twelveengine.physics;

import twelveengine.data.*;

import com.bulletphysics.collision.shapes.CollisionShape;

public class ParentedShape {
	
	public CollisionShape shape;
	public String parent;
	public Vertex offset;
	
	//This is just so I can pass the name of the parent and the collisionshape object at the same time. Less messy this way.
	public ParentedShape(CollisionShape shp, String prnt, Vertex of) {
		shape = shp;
		parent = prnt;
		offset = of;
	}
	
}