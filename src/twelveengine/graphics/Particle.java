package twelveengine.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import twelveengine.data.PhysTriangle;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveutil.MathUtil;

public class Particle {
	public ParticleEvent event;
	
	public String name;
	
	public Shader shader;
	
	public Vertex location, lastLocation;
	public Vertex velocity, lastVelocity;
	
	public boolean collide;
	public boolean attached;
	public float gravity;
	public float drag;
	public float bounce;
	
	public int life;
	
	public float size;
	public float scaling;
	
	public int age;
	
	public boolean dead;

	public Particle(ParticleEvent p, Vertex l, Vertex v, String n, Shader s, boolean c, boolean a, float r, float d, float b, int m, float i, float x) {
		event = p;
		
		name = n;
		
		shader = s;
		
		location = l.copy();
		velocity = v.copy();
	
		collide = c;
		attached = a;
		gravity = r;
		drag = d;
		bounce = b;
		
		life = m;
		
		size = i;
		scaling = x;
		
		lastLocation = l.copy();
		lastVelocity = v.copy();
	}
	
	//Called on each game tick (basically an update)
	public void step() {
		if(age > life)
			dead = true;
		else {
			lastFrame();
			physics();
		}
		age++;
	}
	
	//Used for interpolating graphics between game steps.
	public void lastFrame() {
		lastLocation = location.copy();
		lastVelocity = velocity.copy();
	}
	
	//Move this particle by velocity \('v')/
	public void physics() {
		if(collide) {			
			//TODO: KNICKERY!
		}
		else {
			setLocation(MathUtil.add(location, velocity));
			velocity = MathUtil.add(velocity, MathUtil.multiply(new Vertex(0,0,-1), gravity)); //TODO: Gravity.... this is more of a notice
			velocity = MathUtil.multiply(velocity, drag);
		}
	}
	
	public float dampen(Vertex v, PhysTriangle t) {
		float d = MathUtil.normalSteep(MathUtil.normalize(MathUtil.inverse(v)), t);
		return d;
	}
	
	public void setLocation(Vertex v) {
		location = v;
	}

	public void setVelocity(Vertex v) {
		velocity = v;
	}
	
	public void destroy() {
		
	}
	
	//Since particles can change size and can be set to screen facing we have to create the geometry each time it's rendered.
	//TODO: implement (screenfacing, crosssection)
	//TODO: we now have geometry scaling in the shader so that's a thing we can use to make this run a little faster.
	public FloatBuffer createBuffer(float f) {
		Triangle t[] = event.getGeometry();
		
		//Vertex cam = MathUtil.add(event.partSys.effect.game.player.look, new Vertex(0,0,90));
		Vertex cam = MathUtil.add(new Vertex(GraphicsCore.rot.y,GraphicsCore.rot.x,GraphicsCore.rot.z), new Vertex(-90,-90,-90));
		
		Triangle r[] = new Triangle[t.length];
		int i = 0;
		while(i < t.length) {
			r[i] = MathUtil.rotate(t[i], MathUtil.multiply(cam, 0.0174532925f));
			i++;
		}
		
		t = r;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(t.length * ((3 + 3 + 3 + 3) * 3) * 4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = vbb.asFloatBuffer();
		i = 0;
		while(i < t.length) {
			fb.put(t[i].a.x); 
			fb.put(t[i].a.y); 
			fb.put(t[i].a.z); 
			fb.put(t[i].i.x); 
			fb.put(t[i].i.y); 
			fb.put(t[i].i.z); 
			fb.put(t[i].u.x); 
			fb.put(t[i].u.y); 
			fb.put(t[i].u.z);
			fb.put(t[i].m.x);
			fb.put(t[i].m.y);
			fb.put(t[i].m.z);
			
			fb.put(t[i].b.x); 
			fb.put(t[i].b.y); 
			fb.put(t[i].b.z);
			fb.put(t[i].j.x); 
			fb.put(t[i].j.y); 
			fb.put(t[i].j.z);
			fb.put(t[i].v.x); 
			fb.put(t[i].v.y); 
			fb.put(t[i].v.z);
			fb.put(t[i].n.x);
			fb.put(t[i].n.y);
			fb.put(t[i].n.z);
			
			fb.put(t[i].c.x); 
			fb.put(t[i].c.y); 
			fb.put(t[i].c.z); 
			fb.put(t[i].k.x); 
			fb.put(t[i].k.y); 
			fb.put(t[i].k.z); 
			fb.put(t[i].w.x); 
			fb.put(t[i].w.y); 
			fb.put(t[i].w.z);
			fb.put(t[i].o.x);
			fb.put(t[i].o.y);
			fb.put(t[i].o.z);
			
			i++;
		}
		
		fb.rewind();
		return fb;
	}
	
	public void draw(ArrayList<TrianglePacket> meshes, float f) {
		if(dead)
			return;
		FloatBuffer fb = createBuffer(f);
		Vertex l = MathUtil.lerp(lastLocation, location, f);
		TrianglePacket tp = new TrianglePacket(fb, 2, shader, l, new Quat(), false, new Frame[1], size, 1);
		meshes.add(tp);
	}
}