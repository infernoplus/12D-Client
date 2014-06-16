package twelveengine.bsp;

import java.util.ArrayList;

import twelveengine.data.*;
import twelveengine.graphics.ModelGroup;
import twelveengine.graphics.TrianglePacket;

public class Part {
	//This class is essentially a container for a part of a bsp. 
	//It's so I can just have one object instead of like 6 arrays for each part of a bsp.
	public ModelGroup mesh;
	public PhysModel collision;
	public Vertex location;
	public Quat rotation;
	public float scale;
	
	public Part(ModelGroup m, PhysModel p, Vertex l, Quat r, float s) {
		mesh = m;
		collision = p;
		location = l;
		rotation = r;
		scale = s;
	}
	
	public void draw(ArrayList<TrianglePacket> meshes) {
		mesh.pushToDrawQueue(meshes, location, rotation, scale);
	}
}