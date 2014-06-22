package twelveengine.actors;

import java.util.ArrayList;

import javax.vecmath.*;

import com.bulletphysics.linearmath.Transform;

import twelveengine.Game;
import twelveengine.Log;
import twelveengine.data.*;
import twelveengine.graphics.*;
import twelveengine.physics.*;
import twelveutil.*;

public class Ragdoll extends RigidBody {	
	//A ragdoll is a ragdoll. 
	//The ragdoll tag must define a rigged model, collision shapes to attach to the bones of that model and IK constraints for those bones.
	//Ragdoll handles location/velocity/rotation values in a special way.
	public Ragdoll(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		animate = true;
	}
	
	public void step() {
		super.step();
		animation();
		physics();
		hitboxUpdate();
	}
	
	//TODO: setup a system to define which physics object is root.
	//Overridden because in a ragdoll we take the physics objects and skin the model to them basically.
	public void animation() {
		int i = 0;
		AnimationFrame nufrm = new AnimationFrame(model.models[0].fr.length);
		while(i < nufrm.nodes.length) {
			nufrm.nodes[i] = new AnimationNode(i);
			i++;
		}
		i = 0;
		while(i < physics.length) {
			//Get current state of bullet physics object and apply it to animationnode.
			Vertex l = physics[i].getLocation();
			Quat r = physics[i].getRotation();
			int ind = getParentFrame(physics[i].parent).index;
			//Log.log(physics[i].parent + " @ " + MathUtil.toString(l) + " : " + MathUtil.toString(r) + " - Ind: " + ind, "Animation");
			nufrm.nodes[ind] = new AnimationNode(ind+1, MathUtil.subtract(l, location), r);
			i++;
		}
		lastFrame = frame;
		frame = nufrm;
	}
	
	public void draw(ArrayList<TrianglePacket> meshes, float f) {	
		Vertex l = MathUtil.lerp(lastLocation, location, f);
		Quat r = rotation;
		if(animate && frame != null && lastFrame != null) //TODO: really nesscary to check all this?
			model.pushToDrawQueue(meshes, l, new Quat(), MathUtil.interpolateFrame(lastFrame, frame, f), scale);
		else
			model.pushToDrawQueue(meshes, l, new Quat(), scale);
		int i = 0;
		while(i < effects.size()) {
			effects.get(i).draw(meshes, f);
			i++;
		}
	}
}