package twelveengine.data;

import twelveengine.actors.Actor;

public class Collision {
	public Vertex p; //Point of collision
	public Vertex r; //Normal of reflection
	public PhysTriangle t; //Triangle
	public float d; //Distance to collision
	
	public Actor a; //Actor hit if testing for actors
	
	public Collision(Vertex a, Vertex b, PhysTriangle c , float e) {
		p = a;
		r = b;
		t = c;
		d = e;
		a = null;
	}
}