package twelveengine.physics;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import twelveengine.Log;
import twelveengine.actors.*;
import twelveengine.data.*;
import twelveutil.MathUtil;
import twelveutil.TagSubObject;

import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.Transform;

//Redesigned this class to contain multiple methods to simplify the process of acsessing and modifying data related to bullet physics objects. 
//From this point on we call these methods and avoid calling the native bullet methods. Add new ones if needed.

public class BulletRigidBody extends com.bulletphysics.dynamics.RigidBody {
	
	public Actor owner;
	public String parent;
	
	public Vertex centerOffset;
	public Vertex boneOffset;
	public Quat boneOrientation;
	public TagSubObject tag;
	
	public BulletRigidBody(RigidBodyConstructionInfo constructionInfo) {
		super(constructionInfo);
		centerOffset = new Vertex();
		boneOffset = new Vertex();
		boneOrientation = new Quat();
		owner = null;
	}
	
	public void setOwner(Actor a) {
		owner = a;
	}
	
	public Actor getOwner() {
		return owner;
	}
	
	public boolean hasOwner() {
		return owner != null;
	}

	//Get worldspace values
	
	public Vertex getLocation() {
		Vector3f l = getCenterOfMassPosition(new Vector3f());
		Vertex o = MathUtil.rotate(centerOffset, getRotation());
		return MathUtil.subtract(new Vertex(l.x, l.y, l.z), o);
	}
	
	public Vertex getVelocity() {
		Vector3f e = getLinearVelocity(new Vector3f());
		return new Vertex(e.x, e.y, e.z);
	}
	
	public Quat getRotation() {
		Quat4f q = getOrientation(new Quat4f());
		return new Quat(q.x, q.y, q.z, q.w);
	}
	
	//Set worldspace values
	
	//relative
	public void move(Vertex m) {
		translate(new Vector3f(m.x, m.y, m.z));
		activate();
	}
	
	public void push(Vertex p) {
		Vector3f v = new Vector3f();
		v = getLinearVelocity(v);
		Vector3f x = new Vector3f(p.x + v.x, p.y + v.y, p.z + v.z);
		setLinearVelocity(x);
		activate();
	}
	
	public void rotate() {
		//TODO: IDR how to rotate a rotation...
	}
	
	//absolute
	
	public void setLocation(Vertex l) {
		Vector3f p = new Vector3f();
		p = getCenterOfMassPosition(p);
		translate(new Vector3f(l.x - p.x + boneOffset.x + centerOffset.x, l.y - p.y  + boneOffset.y + centerOffset.y, l.z - p.z  + boneOffset.z + centerOffset.z));
		activate();
	}
	
	public void setVelocity(Vertex v) {
		Vector3f x = new Vector3f(v.x, v.y, v.z);
		setLinearVelocity(x);
		activate();
	}
	
	public void setRotation(Quat r) {
		Transform tr = new Transform();
		tr = getCenterOfMassTransform(tr);
		tr.setRotation(new Quat4f(r.x, r.y, r.z, r.w)); //TODO: + default bone rotation values bre
		setCenterOfMassTransform(tr);
		activate();
	}
}
