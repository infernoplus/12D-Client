package twelveutil;

import javax.vecmath.*;

import twelveengine.data.*;
import twelveengine.graphics.*;

//TODO: I'm sure there are speed improvements to be made throughout this class. It's used HEAVILY in 3D math for both graphics and physics so yeah...
//TOOD: In some places I cast doubles to floats and we lose a little precision during calculations. I should only really cast final calculations to float. It's also messy.

public class MathUtil {
	
	//TODO: bullet uses these vec3f and quat4f things, make sure all conversions use this group of methods.
	public static Vector3f bConvert(Vertex v) {
		return new Vector3f(v.x, v.y, v.z);
	}
	
	public static Quat4f bConvert(Quat q) {
		return new Quat4f(q.x, q.y, q.z, q.w);
	}
	
	public static Vertex bConvert(Vector3f v) {
		return new Vertex(v.x, v.y, v.z);
	}
	
	public static Quat bConvert(Quat4f q) {
		return new Quat(q.x, q.y, q.z, q.w);
	}
	
	//TODO: Make sure the ranges are right....
	public static float random(float min, float max) {
		return (float)((Math.random() * (max-min)) + min);
	}
	
	public static Vertex randomVertex(float min, float max) {
		return new Vertex(MathUtil.random(min, max),MathUtil.random(min, max),MathUtil.random(min, max));
	}
	
	public static Vertex add(Vertex a, Vertex b) {
		return new Vertex(a.x+b.x, a.y+b.y, a.z+b.z);
	}
	
	public static Vertex subtract(Vertex a, Vertex b) {
		return new Vertex(a.x-b.x, a.y-b.y, a.z-b.z);
	}
	
	public static Quat add(Quat a,Quat b) {
		Quat c = new Quat(0,0,0,1);
	    c.x = a.x + b.x;
	    c.y = a.y + b.y;
	    c.z = a.z + b.z;
	    c.w = a.w + b.w;
	    return c;
	}
	
	public static Quat subtract(Quat a,Quat b) {
		Quat c = new Quat(0,0,0,1);
	    c.x = a.x - b.x;
	    c.y = a.y - b.y;
	    c.z = a.z - b.z;
	    c.w = a.w - b.w;
	    return c;
	}
	
	public static Vertex multiply(Vertex a, Vertex b) {
		   Vertex c = new Vertex(0f,0f,0f);
		   c.x = a.x * b.x;
		   c.y = a.y * b.y;
		   c.z = a.z * b.z;
		   return c;
	}
	
	public static Vertex multiply(Vertex a, float d) {
		   Vertex c = new Vertex(0f,0f,0f);
		   c.x = a.x * d;
		   c.y = a.y * d;
		   c.z = a.z * d;
		   return c;
	}
	
	//A snap to grid caclulation. Grid spacing is f.
	public static Vertex nearest(Vertex center, float f) {
		float x = (float) (Math.floor(center.x/f)*f);
		float y = (float) (Math.floor(center.y/f)*f);
		float z = (float) (Math.floor(center.z/f)*f);
		return new Vertex(x,y,z);
	}
	
	//lerp from a to b by f. if f == 0.0 returns a , if f == 1.0 returns b, if f == 0.5 returns the midpoint
	public static Vertex lerp(Vertex a, Vertex b, float f) {
		float r = 1.0f-f;
		Vertex c = new Vertex();
		c.x = (a.x*r) + (b.x*f);
		c.y = (a.y*r) + (b.y*f);
		c.z = (a.z*r) + (b.z*f);
		return c;
	}
	
	public static Vertex normalize(Vertex a) {
		float d = (float)Math.sqrt((a.x*a.x)+(a.y*a.y)+(a.z*a.z));
		if(d == 0)
			return new Vertex(0, 0, 0);
		return new Vertex(a.x/d,a.y/d,a.z/d);
	}
	
	public static Vertex crossProduct(Vertex a, Vertex b) {
		   Vertex c = new Vertex(0f,0f,0f);
		   c.x = a.y * b.z - a.z * b.y;
		   c.y = a.z * b.x - a.x * b.z;
		   c.z = a.x * b.y - a.y * b.x;
		   return c;
		}
	
	public static float dotProduct(Vertex a, Vertex b) {
		float t = 0;
		t += a.x*b.x;
		t += a.y*b.y;
		t += a.z*b.z;
		return t;
	}
	
	public static float magnitude(Vertex a) {
		return (float)Math.sqrt((a.x*a.x) + (a.y*a.y) + (a.z*a.z));
	}
	
	public static float magnitude(Quat a) {
		return (float)Math.sqrt((a.x*a.x) + (a.y*a.y) + (a.z*a.z));
	}
	
	//return distance a to b
	public static float length(Vertex a, Vertex b) {
		float i = a.x-b.x;
		float j = a.y-b.y;
		float k = a.z-b.z;
		return (float)Math.sqrt((i*i)+(j*j)+(k*k));
	}
	
	public static Vertex inverse(Vertex a) {
		return new Vertex(-1*a.x,-1*a.y,-1*a.z);
	}
	
	//starting at location a, move in the direction b distance d and return the new location
	public static Vertex onVector(Vertex a, Vertex b, float d) {
		Vertex c = add(a, multiply(normalize(b), new Vertex(d,d,d)));
		return c;
	}
	
	//Quaternion rotation!
	public static Vertex rotate(Vertex v, Quat r) {
		float q00 = 2.0f * r.x * r.x;
		float q11 = 2.0f * r.y * r.y;
		float q22 = 2.0f * r.z * r.z;

		float q01 = 2.0f * r.x * r.y;
		float q02 = 2.0f * r.x * r.z;
		float q03 = 2.0f * r.x * r.w;

		float q12 = 2.0f * r.y * r.z;
		float q13 = 2.0f * r.y * r.w;

		float q23 = 2.0f * r.z * r.w;

		return new Vertex((1.0f - q11 - q22) * v.x + (q01 - q23) * v.y
				+ (q02 + q13) * v.z, (q01 + q23) * v.x + (1.0f - q22 - q00) * v.y
				+ (q12 - q03) * v.z, (q02 - q13) * v.x + (q12 + q03) * v.y
				+ (1.0f - q11 - q00) * v.z);
	}
	
	//Euler rotation!
	public static Vertex rotate(Vertex v, Vertex r) {
		Vertex f = rotateX(v, r.x);
		f = rotateY(f, r.y);
		f = rotateZ(f, r.z);
		return f;
	}
	
	public static Triangle rotate(Triangle t, Vertex r) {
		Triangle n = new Triangle(rotate(t.a,r), rotate(t.b,r), rotate(t.c,r), rotate(t.i,r), rotate(t.j,r), rotate(t.k,r), t.u, t.v, t.w, t.shdr);
		return n;
	}
	
    public static Vertex rotateX(Vertex v, float rotation)
    {
        //Here we use Euler's matrix formula for rotating a 3D point x degrees around the x-axis

        //[ a  b  c ] [ x ]   [ x*a + y*b + z*c ]
        //[ d  e  f ] [ y ] = [ x*d + y*e + z*f ]
        //[ g  h  i ] [ z ]   [ x*g + y*h + z*i ]

        //[ 1    0        0   ]
        //[ 0   cos(x)  sin(x)]
        //[ 0   -sin(x) cos(x)]
    	
    	Vertex r = v.copy();

        float cDegrees = rotation;
        float cosDegrees = (float)Math.cos(cDegrees);
        float sinDegrees = (float)Math.sin(cDegrees);

        float y = (r.y * cosDegrees) + (r.z * sinDegrees);
        float z = (r.y * -sinDegrees) + (r.z * cosDegrees);
        
        r.y = y;
        r.z = z;

        return r;
    }
    
    public static Vertex rotateY(Vertex v, float rotation)
    {
        //Y-axis

        //[ cos(x)   0    sin(x)]
        //[   0      1      0   ]
        //[-sin(x)   0    cos(x)]
    	
    	Vertex r = v.copy();

        float cDegrees = rotation;
        float cosDegrees = (float)Math.cos(cDegrees);
        float sinDegrees = (float)Math.sin(cDegrees);

        float x = (r.x * cosDegrees) + (r.z * sinDegrees);
        float z = (r.x * -sinDegrees) + (r.z * cosDegrees);
        
        r.x = x;
        r.z = z;

        return r;
    }
    
    public static Vertex rotateZ(Vertex v, float rotation)
    {
        //Z-axis

        //[ cos(x)  sin(x) 0]
        //[ -sin(x) cos(x) 0]
        //[    0     0     1]
    	
    	Vertex r = v.copy();

        float cDegrees = rotation;
        float cosDegrees = (float)Math.cos(cDegrees);
        float sinDegrees = (float)Math.sin(cDegrees);

        float x = (r.x * cosDegrees) + (r.y * sinDegrees);
        float y = (r.x * -sinDegrees) + (r.y * cosDegrees);
        
        r.x = x;
        r.y = y;

        return r;
    }
    
    //Converts a quat to an euler angle. For reasons...
    public static Vertex convertQuat2Euler1(Quat quaternion) {
    	float sqw = quaternion.w * quaternion.w;
    	float sqx = quaternion.x * quaternion.x;
    	float sqy = quaternion.y * quaternion.y;
    	float sqz = quaternion.z * quaternion.z;
    	float unit = sqx + sqy + sqz + sqw; // if normalised is one, otherwise is correction factor
    	float test = quaternion.x * quaternion.y + quaternion.z * quaternion.w;

    	float heading, attitude, bank;

    	if (test > 0.499f * unit)
    	{ // singularity at north pole
    		heading = 2.0f * (float)Math.atan2(quaternion.x, quaternion.w);
    		attitude = (float)Math.PI / 2.0f;
    		bank = 0.0f;
    	}
    	else if (test < -0.499 * unit)
    	{ // singularity at south pole
    		heading = -2.0f * (float)Math.atan2(quaternion.x, quaternion.w);
    		attitude = -(float)Math.PI / 2.0f;
    		bank = 0.0f;
    	}
    	else
    	{
    		heading = (float)Math.atan2(2.0f * quaternion.y * quaternion.w - 2.0f * quaternion.x * quaternion.z, sqx - sqy - sqz + sqw);
    		attitude = (float)Math.asin(2.0f * test / unit);
    		bank = (float)Math.atan2(2.0f * quaternion.x * quaternion.w - 2.0f * quaternion.y * quaternion.z, -sqx + sqy - sqz + sqw);
    	}

    	return new Vertex(bank, heading, attitude);
    }
    
    public static Vertex convertQuat2Euler2(Quat q1) {
    	float heading;
    	float attitude;
    	float bank;
    	
    	float test = q1.x*q1.y + q1.z*q1.w;
    	if (test > 0.499) { // singularity at north pole
    		heading = 2 * (float)Math.atan2(q1.x,q1.w);
    		attitude = (float)(Math.PI/2);
    		bank = 0;
    		return new Vertex(bank, heading, attitude);
    	}
    	if (test < -0.499) { // singularity at south pole
    		heading = (float)(-2 * Math.atan2(q1.x,q1.w));
    		attitude = (float)(-Math.PI/2);
    		bank = 0;
    		return new Vertex(bank, heading, attitude);
    	}
        float sqx = q1.x*q1.x;
        float sqy = q1.y*q1.y;
        float sqz = q1.z*q1.z;
        heading = (float)Math.atan2(2*q1.y*q1.w-2*q1.x*q1.z , 1 - 2*sqy - 2*sqz);
    	attitude = (float)Math.asin(2*test);
    	bank = (float)Math.atan2(2*q1.x*q1.w-2*q1.y*q1.z , 1 - 2*sqx - 2*sqz);
		return new Vertex(bank, heading, attitude);
    }
    
    public static Vertex convertQuat2Euler3(Quat q1) {
    	float heading;
    	float attitude;
    	float bank;
    	
        float sqw = q1.w*q1.w;
        float sqx = q1.x*q1.x;
        float sqy = q1.y*q1.y;
        float sqz = q1.z*q1.z;
    	float unit = sqx + sqy + sqz + sqw; // if normalised is one, otherwise is correction factor
    	float test = q1.x*q1.y + q1.z*q1.w;
    	if (test > 0.499*unit) { // singularity at north pole
    		heading = (float)(2 * Math.atan2(q1.x,q1.w));
    		attitude = (float)(Math.PI/2);
    		bank = 0;
    		return new Vertex(bank, heading, attitude);
    	}
    	if (test < -0.499*unit) { // singularity at south pole
    		heading = (float)(-2 * Math.atan2(q1.x,q1.w));
    		attitude = (float)(-Math.PI/2);
    		bank = 0;
    		return new Vertex(bank, heading, attitude);
    	}
        heading = (float)Math.atan2(2*q1.y*q1.w-2*q1.x*q1.z , sqx - sqy - sqz + sqw);
    	attitude = (float)Math.asin(2*test/unit);
    	bank = (float)Math.atan2(2*q1.x*q1.w-2*q1.y*q1.z , -sqx + sqy - sqz + sqw);
    	
		return new Vertex(bank, heading, attitude);
    }
    
    //Interpolates 2 animation frames by float d. EXAMPLE: 0.0 will return frame a, 1.0 will return frame b, 0.5 will return the mid point between the two.  
    public static AnimationFrame interpolateFrame(AnimationFrame a, AnimationFrame b, float bd) {
    	float ad = 1.0f - bd;
    	int i = 0;
    	AnimationFrame c = new AnimationFrame(a.nodes.length);
    	while(i < a.nodes.length) {
    		float x = (float)a.nodes[i].location.x * ad;
    		float y = (float)a.nodes[i].location.y * ad;
    		float z = (float)a.nodes[i].location.z * ad;
    		x += (float)b.nodes[i].location.x * bd;
    		y += (float)b.nodes[i].location.y * bd;
    		z += (float)b.nodes[i].location.z * bd;
    		
    		c.nodes[i] = new AnimationNode(a.nodes[i].frame, new Vertex(x, y, z), slerpQuat(a.nodes[i].rotation.copy(), b.nodes[i].rotation.copy(), bd));
    		i++;
    	}
    	return c;
    }
    
    //Interpolates 2 quats
    public static Quat slerpQuat(Quat qa, Quat qb, float t) {
    	//TODO: messy?
    	//qa = qa.copy();
    	//qb = qb.copy();
    	// quaternion to return
    	Quat qm = new Quat();
    	// Calculate angle between them.
    	float cosHalfTheta = qa.w * qb.w + qa.x * qb.x + qa.y * qb.y + qa.z * qb.z;
    	if (cosHalfTheta < 0) {
    		  qb.w = -qb.w; qb.x = -qb.x; qb.y = -qb.y; qb.z = -qb.z;
    		  cosHalfTheta = -cosHalfTheta;
    		}
    	// if qa=qb or qa=-qb then theta = 0 and we can return qa
    	if (Math.abs(cosHalfTheta) >= 1.0){
    		qm.w = qa.w;qm.x = qa.x;qm.y = qa.y;qm.z = qa.z;
    		return qm;
    	}
    	// Calculate temporary values.
    	float halfTheta = (float)Math.acos(cosHalfTheta);
    	float sinHalfTheta = (float)Math.sqrt(1.0 - cosHalfTheta*cosHalfTheta);
    	// if theta = 180 degrees then result is not fully defined
    	// we could rotate around any axis normal to qa or qb
    	if (Math.abs(sinHalfTheta) < 0.001){ // fabs is floating point absolute
    		qm.w = (qa.w * 0.5f + qb.w * 0.5f);
    		qm.x = (qa.x * 0.5f + qb.x * 0.5f);
    		qm.y = (qa.y * 0.5f + qb.y * 0.5f);
    		qm.z = (qa.z * 0.5f + qb.z * 0.5f);
    		return qm;
    	}
    	float ratioA = (float)(Math.sin((1 - t) * halfTheta) / sinHalfTheta);
    	float ratioB = (float)(Math.sin(t * halfTheta) / sinHalfTheta); 
    	//calculate Quaternion.
    	qm.w = (qa.w * ratioA + qb.w * ratioB);
    	qm.x = (qa.x * ratioA + qb.x * ratioB);
    	qm.y = (qa.y * ratioA + qb.y * ratioB);
    	qm.z = (qa.z * ratioA + qb.z * ratioB);
    	return qm;
    }
    
    //Returns true if the normal is facing the normal false if the point is behind the normal (IE back facing or front facing of a triangle normal)
    public static boolean normalFacing(Vertex p, Vertex n) {
		if(length(p, n) >= length(p, inverse(n)))
			return true;
		return false;
    }

	
	//returns the steepness of the triangle in comparison to the given normal.
	public static float normalSteep(Vertex n, PhysTriangle t) {
		return length(n, t.normal)/2;
	}
	
	//starting at a and moving the magnitude of b in the direction of b, return the triangle and the reflection angle of collision or null if hitting nothing 
	public static Collision lineTriangleIntersection(Vertex a, Vertex b, PhysTriangle t) {
		//does the line intersect plane of triangle?
		Vertex c = add(a, b);
		Vertex v = normalize(b);
		Vertex i = t.a;
		Vertex j = t.b;
		Vertex k = t.c;
		Vertex normal = t.normal;
		float dp = dotProduct(normal, subtract(k, a)) / dotProduct(normal, v);
		Vertex p = new Vertex(a.x + (dp*v.x),a.y + (dp*v.y),a.z + (dp*v.z));
		//make sure we are not getting a collision in the inverse direction
		if(length(v, normal) >= length(v, inverse(normal))) {
			//does the point land in the triangle?
			Vertex m = subtract(k, i);
			Vertex n = subtract(j, i);
			Vertex o = subtract(p, i);
			
			float dot00 = dotProduct(m, m);
			float dot01 = dotProduct(m, n);
			float dot02 = dotProduct(m, o);
			float dot11 = dotProduct(n, n);
			float dot12 = dotProduct(n, o);
			
			float denom = 1f / ((dot00 * dot11) - (dot01 * dot01));
			float x = ((dot11 * dot02) - (dot01 * dot12)) * denom;
			float y = ((dot00 * dot12) - (dot01 * dot02)) * denom;
			//if the point is in the triangle return a collision
			if(x >= 0 && y >= 0 && x + y < 1) {	
				float ad = length(a, p);
				float bd = length(a, c);
				float cd = length(c, p);
				if(ad <= bd && cd <= bd) {
					Vertex iv = inverse(v);
					dp = dotProduct(normal, iv);
					Vertex r = onVector(new Vertex(0,0,0), subtract(multiply(normal, new Vertex(2*dp,2*dp,2*dp)), iv), length(p, c));
					return new Collision(p, r, t, ad);
				}
			}
		}
		return null;
	}
	
	public static Collision linePlaneIntersection(Vertex a, Vertex b, PhysTriangle t) {
		//does the line intersect plane of triangle?
		Vertex c = add(a, b);
		Vertex v = normalize(b);
		Vertex k = t.c;
		Vertex normal = t.normal;
		float dp = dotProduct(normal, subtract(k, a)) / dotProduct(normal, v);
		Vertex p = new Vertex(a.x + (dp*v.x),a.y + (dp*v.y),a.z + (dp*v.z));
		//make sure we are not getting a collision in the inverse direction
		if(length(v, normal) >= length(v, inverse(normal))) {
			float ad = length(a, p);
			Vertex iv = inverse(v);
			dp = dotProduct(normal, iv);
			Vertex r = onVector(new Vertex(0,0,0), subtract(multiply(normal, new Vertex(2*dp,2*dp,2*dp)), iv), length(p, c));
			return new Collision(p, r, t, ad);
		}
		return null;
	}
	
	//Returns a collision if sphere at location a, with radius r, intersects triangle t
	public static Collision sphereTriangleIntersection(Vertex a, float r, PhysTriangle t) {
		//http://realtimecollisiondetection.net/blog/?p=103
		Vertex A = subtract(t.a, a);
		Vertex B = subtract(t.b, a);
		Vertex C = subtract(t.c, a);
		float rr = r * r;
		Vertex V = crossProduct(subtract(B, A), subtract(C, A));
		float d = dotProduct(A, V);
		float e = dotProduct(V, V);
		boolean sep1 = d * d > rr * e;
		float aa = dotProduct(A, A);
		float ab = dotProduct(A, B);
		float ac = dotProduct(A, C);
		float bb = dotProduct(B, B);
		float bc = dotProduct(B, C);
		float cc = dotProduct(C, C);
		boolean sep2 = (aa > rr) & (ab > aa) & (ac > aa);
		boolean sep3 = (bb > rr) & (ab > bb) & (bc > bb);
		boolean sep4 = (cc > rr) & (ac > cc) & (bc > cc);
		Vertex AB = subtract(B, A);
		Vertex BC = subtract(C, B);
		Vertex CA = subtract(A, C);
		float d1 = ab - aa;
		float d2 = bc - bb;
		float d3 = ac - cc;
		float e1 = dotProduct(AB, AB);
		float e2 = dotProduct(BC, BC);
		float e3 = dotProduct(CA, CA);
		Vertex Q1 = subtract(multiply(A, e1), multiply(AB, d1));
		Vertex Q2 = subtract(multiply(B, e2), multiply(BC, d2));
		Vertex Q3 = subtract(multiply(C, e3), multiply(CA, d3));
		Vertex QC = subtract(multiply(C, e1), Q1);
		Vertex QA = subtract(multiply(A, e2), Q2);
		Vertex QB = subtract(multiply(B, e3), Q3);
		boolean sep5 = (dotProduct(Q1, Q1) > rr * e1 * e1) & (dotProduct(Q1, QC) > 0);
		boolean sep6 = (dotProduct(Q2, Q2) > rr * e2 * e2) & (dotProduct(Q2, QA) > 0);
		boolean sep7 = (dotProduct(Q3, Q3) > rr * e3 * e3) & (dotProduct(Q3, QB) > 0);
		boolean separated = sep1 | sep2 | sep3 | sep4 | sep5 | sep6 | sep7;
		if(!separated) {
			float dist = linePlaneIntersection(a, multiply(inverse(t.normal), r), t).d;
			return new Collision(a, new Vertex(0,0,0), t, dist);
		}
		return null;
		
	}
	
	public static String toString(Vertex a) {
		return "{"+a.x+","+a.y+","+a.z+"}";
	}

	public static String toString(Quat a) {
			return "{"+a.x+","+a.y+","+a.z+","+a.w+"}";
	}
}
