package twelveengine.actors;

import java.util.ArrayList;

import javax.vecmath.*;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.linearmath.Transform;

import twelveengine.Game;
import twelveengine.data.*;
import twelveengine.graphics.*;
import twelveengine.physics.BulletRigidBody;
import twelveutil.*;

public class Physical extends Actor {
	
	public ModelGroup model;
	public CollisionShape collision; //Used for actual physics. Less complexity = better. Using primitive shapes is always good.
	public BulletRigidBody hitbox; //Used for raytesting and other stuff. Should be pretty accurate to the visual model but less complexity is always nice.
	public AnimationGroup animations;
	public Animation animation;
	public BulletRigidBody physics;
	
	public float scale;
	
	public int frame = 0, lastFrame = 0;
	public boolean animate;
	public boolean loop;
	
	public Vertex gravity = new Vertex(0,0,-1);
	
	public float mass;
	public float drag = 0.96f;
	public float friction = 0.7f;

	//This is an abstract class that is used to normalize the way physics attributes are calculated across all "physical" objects in the game world.
	//It's basically the super class for anything that will be physically in the world.
	public Physical(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Physical";
		
		scale = tag.getProperty("scale", 1f);
		mass = tag.getProperty("mass", 1f);
		
		model = game.getModelGroup(tag.getProperty("model", "multipurpose/model/box.model"));
		collision = buildCollisionShape(tag.getObject("collision"));
		
		createHitboxObject();
		
		if(!tag.getProperty("animation", "").equals("")) {
			animate = true;
			animations = game.getAnimation(tag.getProperty("animation", ""));
			animation = animations.getAnimation(tag.getProperty("playanimation", "default"));
			loop = tag.getProperty("loop", false);
		}
		else
			animate = false;
		
		hitboxUpdate();
	}
	
	public void createHitboxObject() {
		Transform startTransform = new Transform();
		startTransform.setIdentity();
		startTransform.origin.set(0, 0, 0f);
		hitbox = game.bsp.bullet.createStaticRigidBody(startTransform, buildCollisionShape(tag.getObject("hitbox")), "hitbox");
		hitbox.setOwner(this);
	}
	
	public void hitboxUpdate() {
		if(hitbox == null)
			return;
		Vector3f p = new Vector3f();
		Quat4f q = new Quat4f();
		Transform t = new Transform();
		t = hitbox.getWorldTransform(t);
		p = hitbox.getCenterOfMassPosition(p);
		t.setRotation(MathUtil.bConvert(rotation));
		//hitbox.translate(new Vector3f(location.x - p.x, location.y - p.y, location.z - p.z));
		hitbox.setWorldTransform(t);
		hitbox.translate(new Vector3f(location.x - p.x, location.y - p.y, location.z - p.z));
	}
	
	public CollisionShape buildCollisionShape(TagSubObject tag) {
		TagSubObject t = tag.getObject(0); //TODO: loop this and set it up for attachments.
		if(t.getProperty("type", "simple").equals("simple")) {
			CollisionShape shp = buildShape(t);
			CompoundShape cmpd = new CompoundShape();
			Transform m = new Transform();
			Vertex l = TagUtil.makeVertex(t.getObject("location"));
			Quat r = TagUtil.makeQuat(t.getObject("rotation"));
			m.transform(new Vector3f(l.x, l.y, l.z));
			m.setRotation(new Quat4f(r.x, r.y, r.z, r.w));
			cmpd.addChildShape(m, shp);
			return cmpd;
		}
		else {
			int i = 0;
			CollisionShape shps[] = new CollisionShape[t.getTotalObjects()];
			Vertex l[] = new Vertex[t.getTotalObjects()];
			Quat r[] = new Quat[t.getTotalObjects()];
			while(i < t.getTotalObjects()) {
				TagSubObject tso = t.getObject(i);
				shps[i] = buildShape(tso);
				l[i] = TagUtil.makeVertex(tso.getObject("location"));
				r[i] = TagUtil.makeQuat(tso.getObject("rotation"));
				i++;
			}
			
			i = 0;
			CompoundShape cmpd = new CompoundShape();
			while(i < shps.length) {
				Transform m = new Transform();
				m.transform(new Vector3f(l[i].x, l[i].y, l[i].z));
				m.setRotation(new Quat4f(r[i].x, r[i].y, r[i].z, r[i].w));
				cmpd.addChildShape(m, shps[i]);
				i++;
			}
			return cmpd;
		}
	}
	
	public CollisionShape buildShape(TagSubObject t) {
		String shp = t.getProperty("shape", "box");
		
		if(shp.equals("hull")) {
			return game.getHull(t.getProperty("file", "multipurpose/model/box.collision")).makeConvexHull(scale);
		}
		else if(shp.equals("sphere")) {
			return new SphereShape(t.getProperty("radius", 1.0f));
		}
		else if(shp.equals("cylinder")) {
			float radius = t.getProperty("radius", 1.0f);
			float height = t.getProperty("height", 1.0f);
			return new CylinderShape(new Vector3f(radius, 0.0f, 0.5f*height));
		}
		else if(shp.equals("cone")) {
			float radius = t.getProperty("radius", 1.0f);
			float height = t.getProperty("height", 1.0f);
			return new ConeShape(radius, height);
		}
		else if(shp.equals("capsule")) {
			float radius = t.getProperty("radius", 1.0f);
			float height = t.getProperty("height", 1.0f);
			return new CapsuleShape(radius, height);
		}
		else {
			float width = t.getProperty("width", 1.0f);
			float length = t.getProperty("length", 1.0f);
			float height = t.getProperty("height", 1.0f);
			return new BoxShape(new Vector3f(width, length, height));
		}
	}
	

	public void move(Vertex a) {
		physics.translate(new Vector3f(a.x, a.y, a.z));
		physics.activate();
		Vector3f c = physics.getCenterOfMassPosition(new Vector3f());
		location = new Vertex(c.x, c.y, c.z);
	}
	
	public void rotate(Quat a) {
		Quat4f q = physics.getOrientation(new Quat4f());
		//TODO: IDR how to rotate a rotation...
	}
	
	public void push(Vertex a) {
		Vector3f v = new Vector3f();
		v = physics.getLinearVelocity(v);
		Vector3f x = new Vector3f(a.x + v.x, a.y + v.y, a.z + v.z);
		physics.setLinearVelocity(x);
		physics.activate();
		Vector3f e = physics.getLinearVelocity(new Vector3f());
		velocity = new Vertex(e.x, e.y, e.z);
	}
	
	public void setLocation(Vertex a) {
		Vector3f p = new Vector3f();
		p = physics.getCenterOfMassPosition(p);
		physics.translate(new Vector3f(a.x - p.x, a.y - p.y, a.z - p.z));
		physics.activate();
		Vector3f c = physics.getCenterOfMassPosition(new Vector3f());
		location = new Vertex(c.x, c.y, c.z);
	}
	
	public void setRotation(Quat a) {
		Transform tr = new Transform();
		tr = physics.getCenterOfMassTransform(tr);
		tr.setRotation(new Quat4f(a.x, a.y, a.z, a.w));
		physics.setCenterOfMassTransform(tr);
		Quat4f q = physics.getOrientation(new Quat4f());
		rotation = new Quat(q.x, q.y, q.z, q.w);
	}
	
	public void setVelocity(Vertex a) {
		Vector3f x = new Vector3f(a.x, a.y, a.z);
		physics.setLinearVelocity(x);
		physics.activate();
		Vector3f e = physics.getLinearVelocity(new Vector3f());
		velocity = new Vertex(e.x, e.y, e.z);
	}
	
	public void destroy() {
		super.destroy();
		physics.destroy();
	}
	
	public void playAnimation(String a, boolean b) { //TODO:Make a flag so that it will return to previous animation after play MB MB
		if(animation != null) {
			frame = 0;
			lastFrame = 0;
			animation = animations.getAnimation(a);
			loop = b;
		}
	}
	
	public Vertex getAttachmentPoint(String a) {
		if(model != null) {
			int i = 0;
			while(i < model.getModel().ma.length) {
				if(model.getModel().ma[i].name.equals(a))
					return MathUtil.add(location.copy(), model.getModel().ma[i].getPosition());
				i++;
			}
		}
		return location.copy();
	}
	
	public void invoke(String m, String p[]) {
		super.invoke(m, p);

		if(m.equals("animation")) {
			playAnimation(p[0], Boolean.parseBoolean(p[1]));
			return;
		}
	}
	
	public void setScale(float f) { //TODO: collision
		scale = f;
	}

	public void draw(ArrayList<TrianglePacket> meshes, float f) {	
		Vertex l = MathUtil.lerp(lastLocation, location, f);
		Quat r = rotation;
		if(animate && animations != null)
			model.pushToDrawQueue(meshes, l, r, MathUtil.interpolateFrame(animation.frames[lastFrame], animation.frames[frame], f), scale);
		else
			model.pushToDrawQueue(meshes, l, r, scale);
		int i = 0;
		while(i < effects.size()) {
			effects.get(i).draw(meshes, f);
			i++;
		}
	}
}
