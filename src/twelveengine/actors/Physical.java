package twelveengine.actors;

import java.util.ArrayList;

import javax.vecmath.*;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.linearmath.Transform;

import twelveengine.Game;
import twelveengine.Log;
import twelveengine.data.*;
import twelveengine.graphics.*;
import twelveengine.physics.*;
import twelveutil.*;

public class Physical extends Actor {
	
	public ModelGroup model;
	public BulletRigidBody hitbox[]; //Used for raytesting and other stuff. Should be pretty accurate to the visual model but less complexity is always nice.
	public AnimationGroup animations;
	public Animation animation;
	public int root; 
	public BulletRigidBody physics[];//Used for actual physics. Less complexity = better. Using primitive shapes is always good.
	
	public float scale;
	
	public AnimationFrame frame, lastFrame;
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
		
		createHitboxObject();
		
		if(!tag.getProperty("animation", "").equals("")) {
			animate = true;
			animations = game.getAnimation(tag.getProperty("animation", ""));
			animation = animations.getAnimation(tag.getProperty("playanimation", "default"));
			loop = tag.getProperty("loop", false);
			frame = animation.frames[0];
			lastFrame = animation.frames[0];
		}
		else
			animate = false;
		
		hitboxUpdate();
		root = 0; //TODO: Currently we are just using 0 but we NEED to set up a sytem to decide which phsyics object is the root.
	}
	
	//This method updates the animation of an object. Override this to do other things. By default it just plays animations if it's set to true and looping.
	public int k = 0; //Frame.
	public void animation() {
		if(animate) {
			if(k < animation.frames.length-1) {
				lastFrame = frame;
				frame = animation.frames[k];
				k++;
			}
			else {
				lastFrame = frame;
				frame = animation.frames[k];
				if(loop) {
					k=0;
				}
			}
		}
	}
	
	public void createHitboxObject() {
		int i = 0;
		ParentedShape shps[] = buildCollisionShape(tag.getObject("hitbox"));
		hitbox = new BulletRigidBody[shps.length];
		while(i < shps.length) {
			Transform startTransform = new Transform();
			startTransform.setIdentity();
			startTransform.origin.set(0, 0, 0f);
			hitbox[i] = game.bsp.bullet.createStaticRigidBody(startTransform, shps[i].shape, shps[i].parent, "hitbox");
			hitbox[i].setOwner(this);
			i++;
		}
	}
	
	public void hitboxUpdate() {
		int i = 0;
		while(i < hitbox.length) { //TODO: currently just sticking them all at the center of the object, link them to the model plz.
			if(hitbox == null)
				return;
			Vector3f p = new Vector3f();
			Quat4f q = new Quat4f();
			Transform t = new Transform();
			t = hitbox[i].getWorldTransform(t);
			p = hitbox[i].getCenterOfMassPosition(p);
			t.setRotation(MathUtil.bConvert(rotation));
			//hitbox.translate(new Vector3f(location.x - p.x, location.y - p.y, location.z - p.z));
			hitbox[i].setWorldTransform(t);
			hitbox[i].translate(new Vector3f(location.x - p.x, location.y - p.y, location.z - p.z));
			i++;
		}
	}
	
	public ParentedShape[] buildCollisionShape(TagSubObject tag) {
		int j = 0;
		ParentedShape shps[] = new ParentedShape[tag.getTotalObjects()];
		while(j < tag.getTotalObjects()) {
			TagSubObject t = tag.getObject(j);
			if(t.getProperty("type", "simple").equals("simple")) {
				CollisionShape shp = buildShape(t);
				CompoundShape cmpd = new CompoundShape();
				Transform m = new Transform();
				Vertex o = TagUtil.makeVertex(t.getObject("offset"));
				Vertex l = TagUtil.makeVertex(t.getObject("location"));
				Quat r = TagUtil.makeQuat(t.getObject("rotation"));
				m.transform(new Vector3f(l.x, l.y, l.z));
				m.setRotation(new Quat4f(r.x, r.y, r.z, r.w));
				cmpd.addChildShape(m, shp);
				shps[j] = new ParentedShape(cmpd, t.getProperty("parent", "root"), o);
			}
			else {
				int i = 0;
				CollisionShape cmps[] = new CollisionShape[t.getTotalObjects()];
				Vertex o = TagUtil.makeVertex(t.getObject("offset"));
				Vertex l[] = new Vertex[t.getTotalObjects()];
				Quat r[] = new Quat[t.getTotalObjects()];
				while(i < t.getTotalObjects()) {
					TagSubObject tso = t.getObject(i);
					cmps[i] = buildShape(tso);
					l[i] = TagUtil.makeVertex(tso.getObject("location"));
					r[i] = TagUtil.makeQuat(tso.getObject("rotation"));
					i++;
				}
				
				i = 0;
				CompoundShape cmpd = new CompoundShape();
				while(i < cmps.length) {
					Transform m = new Transform();
					m.transform(new Vector3f(l[i].x, l[i].y, l[i].z));
					m.setRotation(new Quat4f(r[i].x, r[i].y, r[i].z, r[i].w));
					cmpd.addChildShape(m, cmps[i]);
					i++;
				}
				shps[j] = new ParentedShape(cmpd, t.getProperty("parent", "root"), o);
			}
			j++;
		}
		return shps;
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
	
	//TODO: currently just moving the first of the physics objects, need to move the root and then move any other parts by the same amount. do this later.
	public void move(Vertex a) { 
		int i = 0;
		while(i < physics.length) {
			physics[i].move(a);
			i++;
		}
		location = physics[root].getLocation();
	}
	
	public void rotate(Quat a) {
		//TODO: IDR how to rotate a rotation...
	}
	
	public void push(Vertex a) {
		int i = 0;
		while(i < physics.length) {
			physics[i].push(a);
			i++;
		}
		velocity = physics[root].getVelocity();
	}
	
	public void setLocation(Vertex a) {
		int i = 0;
		while(i < physics.length) {
			physics[i].setLocation(a);
			i++;
		}
		location = physics[root].getLocation();
	}
	
	public void setRotation(Quat a) {
		int i = 0;
		while(i < physics.length) {
			physics[i].setRotation(a);
			i++;
		}
		rotation = physics[root].getRotation();
	}
	
	public void setVelocity(Vertex a) {
		int i = 0;
		while(i < physics.length) {
			physics[i].setVelocity(a);
			i++;
		}
		velocity = physics[root].getVelocity();
	}
	
	public void destroy() {
		super.destroy();
		int i = 0;
		while(i < physics.length) {
			physics[i].destroy();
			i++;
		}
	}
	
	public void playAnimation(String a, boolean b) { //TODO:Make a flag so that it will return to previous animation after play MB MB
		if(animation != null) {
			animation = animations.getAnimation(a);
			frame = animation.frames[0];
			lastFrame = animation.frames[0];
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
		if(animate && frame != null && lastFrame != null) //TODO: really nesscary to check all this?
			model.pushToDrawQueue(meshes, l, r, MathUtil.interpolateFrame(lastFrame, frame, f), scale);
		else
			model.pushToDrawQueue(meshes, l, r, scale);
		int i = 0;
		while(i < effects.size()) {
			effects.get(i).draw(meshes, f);
			i++;
		}
	}
}
