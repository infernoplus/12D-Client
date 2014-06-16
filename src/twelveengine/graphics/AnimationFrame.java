package twelveengine.graphics;

import twelveengine.data.Quat;
import twelveengine.data.Vertex;

public class AnimationFrame {
	public AnimationNode nodes[];
	public AnimationFrame(String s) {
		String p[] = s.split(">");
		int i = 1;
		nodes = new AnimationNode[p.length-1];
		while(i < p.length) {
			String v[] = p[i].split(" ");
			int a = Integer.parseInt(v[0]);
			Vertex b = new Vertex(Float.parseFloat(v[1].split("/")[0]), Float.parseFloat(v[1].split("/")[1]), Float.parseFloat(v[1].split("/")[2]));
			Quat c = new Quat(Float.parseFloat(v[2].split("/")[0]), Float.parseFloat(v[2].split("/")[1]), Float.parseFloat(v[2].split("/")[2]), Float.parseFloat(v[2].split("/")[3]));
			nodes[i-1] = new AnimationNode(a, b, c);
			i++;
		}
	}
	
	//Used to create an animation frame in code. EXAMPLE: MathUtil.interpolateFrame()
	public AnimationFrame(int length) {
		nodes = new AnimationNode[length];
	}
	
	/*public AnimationNode getNodeByFrameName(String s) {
		int i = 0;
		while(i < nodes.length) {
			if(nodes[i].name.equals(s)) {
				return nodes[i];
			}
			i++;
		}
		return null;
	}*/
}