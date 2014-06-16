package twelveengine.graphics;

import twelveengine.data.Vertex;

public class SunLight {	
	public Vertex rotation[];
	public float radius;
	
	public float nearShadow;
	public float overlapShadow;
	public float farShadow;
	
	public Vertex lightColor[];
	public Vertex shadowColor[];
	
	public float maxIntensity;
	public float minIntensity;
	
	//Sun light is literally the lighting information for the sun. This is created by the BSP class while reading the bsp tag.	
	public SunLight(float r, Vertex y[], float n, float o, float f, Vertex a[], Vertex b[], float i, float j) {
		rotation = y;
		radius = r;
		nearShadow = n;
		overlapShadow = o;
		farShadow = f;
		lightColor = a;
		shadowColor = b;
		maxIntensity = j;
		minIntensity = i;
	}
	
	//DEPRECATED
	/*public Vertex sunNormal() {
		Vertex r = MathUtil.multiply(rotation, 0.0174532925); // convert to rad
		return MathUtil.normalize(MathUtil.rotate(new Vertex(0,0,-radius), r));
	}
	
	public Vertex sunLocation() {
		Vertex r = MathUtil.multiply(rotation, 0.0174532925); // convert to rad
		return MathUtil.rotate(new Vertex(0,0,-radius), r);
	}*/
}
