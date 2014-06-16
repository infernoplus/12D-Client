package twelveengine.physics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import twelveengine.Log;
import twelveengine.bsp.BSP;
import twelveengine.bsp.Part;
import twelveengine.data.PhysModel;
import twelveengine.data.Vertex;
import twelveutil.MathUtil;

import com.bulletphysics.BulletStats;
import com.bulletphysics.collision.broadphase.*;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestConvexResultCallback;
import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.collision.dispatch.CollisionWorld.ConvexResultCallback;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.constraintsolver.*;
import com.bulletphysics.linearmath.*;
import com.bulletphysics.util.*;

//This contains the Bullet physics representation of the game world BSP.

public class BulletBSP {
	
	protected DiscreteDynamicsWorld dynamicsWorld = null;
	private CProfileIterator profileIterator;
	
	public ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
	public BroadphaseInterface broadphase;
	public CollisionDispatcher dispatcher;
	public ConstraintSolver solver;
	public DefaultCollisionConfiguration collisionConfiguration;
	
	public BulletBSP(BSP b, Part m[]) throws Exception {
		BulletStats.setProfileEnabled(true);
		profileIterator = CProfileManager.getIterator();
		
		initPhysics(m);
	}
	
	public void initPhysics(Part m[]) {
		// Setup a Physics Simulation Environment
		collisionConfiguration = new DefaultCollisionConfiguration();
		// btCollisionShape* groundShape = new btBoxShape(btVector3(50,3,50));
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldMin = new Vector3f(-10000f,-10000f,-10000f); //TODO: THIS!
		Vector3f worldMax = new Vector3f(10000f,10000f,10000f);
		//broadphase = new AxisSweep3(worldMin, worldMax);
		//broadphase = new SimpleBroadphase();
		broadphase = new DbvtBroadphase();
		//btOverlappingPairCache* broadphase = new btSimpleBroadphase();
		solver = new SequentialImpulseConstraintSolver();
		//ConstraintSolver* solver = new OdeConstraintSolver;
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher,broadphase,solver,collisionConfiguration);

		Vector3f gravity = new Vector3f();
		gravity.negate(new Vector3f(0,0,1));
		gravity.scale(10f);
		dynamicsWorld.setGravity(gravity);

		new BspToBulletConverter().convertBsp(m);
		
		clientResetScene();
	}
	
	public void step() {
		//dynamicsWorld.stepSimulation(0.33f);
		dynamicsWorld.stepSimulation(0.066f, 4);
	}
	
	public void clientResetScene() {
		//#ifdef SHOW_NUM_DEEP_PENETRATIONS
		BulletStats.gNumDeepPenetrationChecks = 0;
		BulletStats.gNumGjkChecks = 0;
		//#endif //SHOW_NUM_DEEP_PENETRATIONS

		int numObjects = 0;
		if (dynamicsWorld != null) {
			dynamicsWorld.stepSimulation(1f / 60f, 0);
			numObjects = dynamicsWorld.getNumCollisionObjects();
		}

		for (int i = 0; i < numObjects; i++) {
			CollisionObject colObj = dynamicsWorld.getCollisionObjectArray().getQuick(i);
			RigidBody body = RigidBody.upcast(colObj);
			if (body != null) {
				if (body.getMotionState() != null) {
					DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
					myMotionState.graphicsWorldTrans.set(myMotionState.startWorldTrans);
					colObj.setWorldTransform(myMotionState.graphicsWorldTrans);
					colObj.setInterpolationWorldTransform(myMotionState.startWorldTrans);
					colObj.activate();
				}
				// removed cached contact points
				dynamicsWorld.getBroadphase().getOverlappingPairCache().cleanProxyFromPairs(colObj.getBroadphaseHandle(), getDynamicsWorld().getDispatcher());

				body = RigidBody.upcast(colObj);
				if (body != null && !body.isStaticObject()) {
					RigidBody.upcast(colObj).setLinearVelocity(new Vector3f(0f, 0f, 0f));
					RigidBody.upcast(colObj).setAngularVelocity(new Vector3f(0f, 0f, 0f));
				}
			}

			/*
			//quickly search some issue at a certain simulation frame, pressing space to reset
			int fixed=18;
			for (int i=0;i<fixed;i++)
			{
			getDynamicsWorld()->stepSimulation(1./60.f,1);
			}
			*/
		}
	}
	
	//Collision channels
		public final short ray = 128;
		public final short world = 256;
		public final short hitbox = 512;
		public final short player = 1024;
		public final short object = 2048;
		public final short item = 4096;
	
	public BulletRigidBody createStaticRigidBody(Transform startTransform, CollisionShape shape, String mask) {
		Vector3f localInertia = new Vector3f(0f, 0f, 0f);
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo(0, myMotionState, shape, localInertia);
		BulletRigidBody body = new BulletRigidBody(cInfo);
		
		if(mask.equals("world"))
			dynamicsWorld.addRigidBody(body, world, (short)(player | object | item | ray));
		else
			dynamicsWorld.addRigidBody(body, hitbox, ray);
		return body;
	}
	
	public BulletRigidBody createDynamicRigidBody(float mass, Transform startTransform, CollisionShape shape, String mask) {
		Vector3f localInertia = new Vector3f(0f, 0f, 0f);
		shape.calculateLocalInertia(mass, localInertia);
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo(mass, myMotionState, shape, localInertia);
		BulletRigidBody body = new BulletRigidBody(cInfo);

		if(mask.equals("player"))
			dynamicsWorld.addRigidBody(body, player, (short)(world | player | object));
		else if(mask.equals("item"))
			dynamicsWorld.addRigidBody(body, item, (short)(world | object | item));
		else
			dynamicsWorld.addRigidBody(body, object, (short)(world | player | object | item));
		
		return body;
	}
	
	/** DEPRECATED - now using a class called ConvexHull in twelveengine.data **/
	/*public ConvexHullShape makeConvexHull(PhysModel m) {
		ObjectArrayList<Vector3f> vertices = new ObjectArrayList<Vector3f>();
		int j = 0;
		while(j < m.v.length) {
			vertices.add(new Vector3f(m.v[j].x, m.v[j].y, m.v[j].z));
			j++;
		}
		ConvexHullShape hull = new com.bulletphysics.collision.shapes.ConvexHullShape(vertices);
		collisionShapes.add(hull);
		return hull;
	}*/
	
	class BspToBulletConverter extends BspConverter {
		@Override
		public void createTriMesh(ObjectArrayList<Vector3f> vertices) {
			if (vertices.size() > 0) {
				Transform startTransform = new Transform();
				// can use a shift
				startTransform.setIdentity();
				startTransform.origin.set(0, 0, 0f);
				
				// this create an internal copy of the vertices
				//CollisionShape shape = new ConvexHullShape(vertices);
				//CollisionShape shape = new TriangleShape(vertices.get(0), vertices.get(1), vertices.get(2));
				
				ByteBuffer vbb, ibb;
				
				vbb = ByteBuffer.allocateDirect(vertices.size() * 3 * 4);
				vbb.order(ByteOrder.nativeOrder());
				vbb.clear();
				int i = 0;
				while(i < vertices.size()) {
					vbb.putFloat(vertices.get(i).x);
					vbb.putFloat(vertices.get(i).y);
					vbb.putFloat(vertices.get(i).z);
					i++;
				}
				vbb.rewind();
				
				ibb = ByteBuffer.allocateDirect(vertices.size() * 4);
				ibb.order(ByteOrder.nativeOrder());
				ibb.clear();
				
				i = 0;
				while(i < vertices.size()) {
					   ibb.putInt(i);
					   i++;
					   ibb.putInt(i);
					   i++;
					   ibb.putInt(i);
					   i++;
				}
				ibb.rewind();
				
				TriangleIndexVertexArray mesh = new com.bulletphysics.collision.shapes.TriangleIndexVertexArray(vertices.size()/3, ibb, 3 * 4, vertices.size(), vbb, 3 * 4);
				CollisionShape shape = new BvhTriangleMeshShape(mesh, true, true);
				collisionShapes.add(shape);
				
				createStaticRigidBody(startTransform, shape, "world");
			}
		}
	}
	
	public DynamicsWorld getDynamicsWorld() {
		return dynamicsWorld;
	}
	
	//TODO: we are temporarily using a ray test for "on ground" we need to fix this at some point.
	public boolean isOnGround(com.bulletphysics.dynamics.RigidBody rb, Vector3f grv) {
		Transform pos = new Transform();
		Transform dest = new Transform();
		pos = rb.getCenterOfMassTransform(pos);
		Matrix4f m = pos.getMatrix(new Matrix4f());
		dest.set(m);
		
		dest.origin.x += grv.x;
		dest.origin.y += grv.y;
		dest.origin.z += grv.z;
		
		ClosestRayResultCallback r = new ClosestRayResultCallback(pos.origin, dest.origin);
		r.collisionFilterGroup = ray;
		r.collisionFilterMask = (short)(world | hitbox);
		
		dynamicsWorld.rayTest(pos.origin, dest.origin, r);
		
		if(r.hasHit()) {
		    Vector3f nrm = r.hitNormalWorld;
		    
		    Vertex n = MathUtil.normalize(new Vertex(nrm.x, nrm.y, nrm.z));
		    Vertex g = MathUtil.normalize(new Vertex(grv.x, grv.y, grv.z));
		    
		    if(MathUtil.dotProduct(g, n) > -0.76f) {
		    	return false;
		    }
		    else
		    	return true;
		}
		else
			return false;
	}

	public ClosestRayResultCallback trace(Vector3f start, Vector3f end) {
		ClosestRayResultCallback r = new ClosestRayResultCallback(start, end);
		r.collisionFilterGroup = ray;
		r.collisionFilterMask = (short)(world | hitbox);
		dynamicsWorld.rayTest(start, end, r);
		return r;
	}


	/** DEPRECATED keeping for reference, remove later. reason: convex sweep test is not compatible with trimmesh, go figure...**/
	/*public boolean isOnGround(com.bulletphysics.dynamics.RigidBody rb, Vector3f grv) {
		Transform pos = new Transform();
		Transform dest = new Transform();
		pos = rb.getCenterOfMassTransform(pos);
		Matrix4f m = pos.getMatrix(new Matrix4f());
		dest.set(m);
		
		dest.origin.x += grv.x;
		dest.origin.y += grv.y;
		dest.origin.z += grv.z;
		
		//Vector3f c = new Vector3f();
		//c = rb.getCenterOfMassPosition(c);
		//Vector3f d = new Vector3f(c.x + grv.x, c.y + grv.y, c.z + grv.z);
		
		//dest.transform(grv);
		
		ClosestConvexResultCallback result = new ClosestConvexResultCallback(new Vector3f(), new Vector3f());
		dynamicsWorld.convexSweepTest((ConvexShape) rb.getCollisionShape(), pos, dest, result);
		
		//System.out.println(result.hasHit() + "  |  {" + pos.origin.x + ", " + pos.origin.y + ", " + pos.origin.z + "} vs {" + dest.origin.x + ", " + dest.origin.y + ", " + dest.origin.z + "}");
		return result.hasHit();
	}*/
}