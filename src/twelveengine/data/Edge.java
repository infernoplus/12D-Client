package twelveengine.data;

import twelveengine.data.Vertex;

public class Edge {
	public Vertex a;
	public Vertex b;
	
	public Vertex center;
	
	public float radius;
	
	public Edge(Vertex x, Vertex y) {
		a = x;
		b = y;
		center = new Vertex((a.x + b.x)/2, (a.y + b.y)/2, (a.z + b.z)/2);
		
		float i = a.x-b.x;
		float j = a.y-b.y;
		float k = a.z-b.z;
		radius = (float)(Math.sqrt((i*i)+(j*j)+(k*k))*0.5f);
	}
	
	public float length() {
		float i = a.x-b.x;
		float j = a.y-b.y;
		float k = a.z-b.z;
		return (float)(Math.sqrt((i*i)+(j*j)+(k*k)));
	}
	
	public Edge inverse() {
		return new Edge(b, a);
	}
	
	public boolean edgeEquals(Edge e) {
		if(e.a.x == a.x && e.a.y == a.y && e.a.z == a.z && e.b.x == b.x && e.b.y == b.y && e.b.z == b.z) {
			return true;
		}
		if(e.b.x == a.x && e.b.y == a.y && e.b.z == a.z && e.a.x == b.x && e.a.y == b.y && e.a.z == b.z) {
			return true;
		}
		return false;
	}
}
