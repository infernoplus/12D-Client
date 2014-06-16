package twelveengine.actors;

import java.util.ArrayList;

import twelveengine.Game;
import twelveengine.data.*;
import twelveengine.graphics.TrianglePacket;
import twelveutil.MathUtil;

public class Pawn extends Biped {
	
	public float reach;
	public float toss;
	
	public ArrayList<Item> inventory;
	
	public boolean fp = false;
	
	public Pawn(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Pawn";
		
		inventory = new ArrayList<Item>();
		reach = tag.getProperty("reach", 3);
		toss = tag.getProperty("toss", 5);
	}
	
	public Vertex camera(float f) {
		Vertex p = MathUtil.lerp(MathUtil.inverse(lastLocation), MathUtil.inverse(location), f);
		p.z -= eye;
		return p;
	}
	
	public void pickup(Item i) {
		inventory.add(i);
		i.picked(this);
	}
	
	public void drop(Item i, Vertex l, Vertex v, Quat r) {
		inventory.remove(i);
		i.drop(l, v, r);
	}
	
	/** DEBUG **/
	public String inventoryContents() {
		int i = 0;
		String s = "";
		while(i < inventory.size()) {
			if(inventory.get(i) != null)
				s += (i+1) + ": " + inventory.get(i).getName() + "\n";
			else
				s += (i+1) + ":\n";
			i++;
		}
		return s;
	}
	
	public void drawFP(ArrayList<TrianglePacket> fp, float f) {
		
	}
	
	public void draw(ArrayList<TrianglePacket> meshes, float f) {
		Vertex l = MathUtil.lerp(lastLocation, location, f);
		Quat r = MathUtil.slerpQuat(lastRotation, rotation, f);
		l.z += 10;
		
		if(fp) {
			//nickery
		}
		else {			
			model.pushToDrawQueue(meshes, l, r, scale);
		}
	}
	
}
