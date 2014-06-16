package twelveutil;

import twelveengine.data.*;

public class TagUtil {
	//Will take a tagsubobject and attempt to make a vertex out of it.
	public static Vertex makeVertex(TagSubObject tag) {
		if(tag.getProperty("name", "").equals(""))
			return new Vertex(tag.getProperty("x", 0.0f), tag.getProperty("y", 0.0f), tag.getProperty("z", 0.0f));
		else
			return new Vertex(tag.getProperty("x", 0.0f), tag.getProperty("y", 0.0f), tag.getProperty("z", 0.0f), tag.getProperty("name", ""));
	}
	
	public static Vertex makeColor(TagSubObject tag) {
		return MathUtil.multiply(new Vertex(tag.getProperty("r", 0.0f), tag.getProperty("g", 0.0f), tag.getProperty("b", 0.0f)), tag.getProperty("a", 1.0f));
	}
	
	//Will take a tagsubobject and attempt to make a quat out of it.
	public static Quat makeQuat(TagSubObject tag) {
		return new Quat(tag.getProperty("x", 0.0f), tag.getProperty("y", 0.0f), tag.getProperty("z", 0.0f), tag.getProperty("w", 0.0f));
	}
}