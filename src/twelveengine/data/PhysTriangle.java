package twelveengine.data;

import twelveutil.MathUtil;

public class PhysTriangle {
	//Triangle coordinates
	public Vertex a, b, c;
	public String properties;
	public PhysModel model;
	
	public float radius;
	public Vertex normal;
	public Vertex center;
	
	public PhysTriangle(Vertex aa, Vertex bb, Vertex cc, String p, PhysModel m) {
		a = aa;
		b = bb;
		c = cc;
		properties = p;
		model = m;
		/** Deprecated **/
		//collisionData(); 
	}
	
	public void collisionData() {
		center = centerF();
		normal = MathUtil.normalize(MathUtil.crossProduct(MathUtil.subtract(a,c), MathUtil.subtract(b,c)));
		radius = MathUtil.length(center, new Vertex(a.x, a.y, a.z));
		if(radius < MathUtil.length(center, new Vertex(b.x, b.y, b.z)))
			radius = MathUtil.length(center, new Vertex(b.x, b.y, b.z));
		if(radius < MathUtil.length(center, new Vertex(c.x, c.y, c.z)))
			radius = MathUtil.length(center, new Vertex(c.x, c.y, c.z));
	}
	
	public PhysTriangle normalizeTriangle() {
		Vertex center = centerF();
		Vertex m = new Vertex(a.x - center.x, a.y - center.y, a.z - center.z);
		Vertex n = new Vertex(b.x - center.x, b.y - center.y, b.z - center.z);
		Vertex o = new Vertex(c.x - center.x, c.y - center.y, c.z - center.z);
		return new PhysTriangle(m, n, o, properties, model);
	}
	
	public PhysTriangle move(Vertex p) {
		Vertex m = new Vertex(a.x + p.x, a.y + p.y, a.z + p.z);
		Vertex n = new Vertex(b.x + p.x, b.y + p.y, b.z + p.z);
		Vertex o = new Vertex(c.x + p.x, c.y + p.y, c.z + p.z);
		return new PhysTriangle(m, n, o, properties, model);
	}
	
	public Vertex centerF() {
		return new Vertex((a.x+b.x+c.x)/3,(a.y+b.y+c.y)/3,(a.z+b.z+c.z)/3);
	}
}
