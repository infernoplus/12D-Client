package twelveengine.graphics;

import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveutil.MathUtil;

public class Frame {
	public int index;
	public String name;
	public Vertex location;
	public Quat rotation;
	public Vertex defaultLocation;
	public Quat defaultRotation;
	public Frame(int i, String n, Vertex loc, Quat rot) {
		index = i;
		name = n;
		location = loc;
		rotation = rot;
		defaultLocation = loc;
		defaultRotation = rot;
	}

	public Vertex shiftLocation() {
		return MathUtil.subtract(location, defaultLocation);
	}

	public Frame copy() {
		return new Frame(index, name, location, rotation);
	}
}
