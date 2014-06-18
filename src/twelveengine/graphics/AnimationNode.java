package twelveengine.graphics;

import twelveengine.data.Quat;
import twelveengine.data.Vertex;

public class AnimationNode {
	public int frame;
	public Vertex location;
	public Quat rotation;
	public AnimationNode(int id, Vertex x, Quat y) {
		frame = id;
		location = x;
		rotation = y;
	}
	
	public AnimationNode(int id) {
		frame = id;
		location = new Vertex();
		rotation = new Quat();
	}
}