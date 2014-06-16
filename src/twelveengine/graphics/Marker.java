package twelveengine.graphics;

import twelveengine.data.Vertex;
import twelveutil.MathUtil;

public class Marker {
	public String name;
	public Frame parent; //What this is connected to
	public Vertex offset; //Relative location from parent frame
	public Vertex vector; //A normal pointing in a direction. Used for like particle sprays //TODO: To quat or not to quat? that is the question
	
	
	public Marker(String n, Frame p, Vertex o, Vertex v) {
		name = n;
		parent = p;
		offset = o;
		vector = v;
	}
	
	//Returns this markers position on the model, 
	//DOES NOT return the position in world space, you need to add the actors location to the model space to get world space.
	public Vertex getPosition() {
		return MathUtil.add(parent.shiftLocation(), MathUtil.add(offset, parent.defaultLocation)); //TODO: account for frames rotation
	}
}