package twelveengine.actors;

import twelveengine.Game;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveutil.MathUtil;

public class Equipment extends Item {
	//TODO: after weapons, revamp this too
	public String type;
	public boolean stacks;
	
	public String fileCreateOnUse;
	
	public int total;
	
	public float impulse;
	
	public Equipment(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Equipment";
		
		type = tag.getProperty("type", "Default");
		stacks = tag.getProperty("stacks", true);
		fileCreateOnUse = tag.getProperty("createwhenused", "none");
		total = tag.getProperty("total", 4);
		impulse = tag.getProperty("impulse", 1);
	}
	
	public void useEquipment(Biped b) {
		if(total > 0) {
			Vertex v = MathUtil.multiply(MathUtil.normalize(b.look.copy()), impulse);
			//game.engine.network.packetsOut.add(new Packet10Instantiate(-1, fileCreateOnUse, b.location.x, b.location.y, b.location.z, v.x, v.y, v.z, 0, 0, 0));
			setTotal(total-1);
		}
	}
	
	public void setTotal(int i) {
		//game.engine.network.packetsOut.add(new Packet17Equipment(nid, i));
		total = i;
	}
	
	public String equipmentType() {
		return type;
	}
	
	public String getName() {
		return name + ": " + total;
	}
}