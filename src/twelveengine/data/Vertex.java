package twelveengine.data;

import twelveengine.graphics.Weight;
import twelveutil.MathUtil;

public class Vertex {
	public float x;
	public float y;
	public float z;	
	
	//Weights to bones, for animation
	public Weight[] weights;
	
	//Name is mostly unused but is important for some things
	public String name;
	
	public Vertex() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vertex(float a, float b, float c) {
		x = a;
		y = b;
		z = c;
	}
	
	public Vertex(float a, float b, float c, String n) {
		x = a;
		y = b;
		z = c;
		name = n;
	}
	
	//Max amount of weights on a vertex is 3. weights must add up to exactly 1.0f. if there are less than 3 weights the weight list is filled with 0.0 weights to compensate.
	int count = 0;
	public void addWeight(Weight w) {
		if(count == 0) {
			weights = new Weight[3];
			weights[0] = w;
			weights[1] = new Weight(w.frame, 0.0f);
			weights[2] = new Weight(w.frame, 0.0f);	
			count++;
		}
		else if(count == 1) {
			weights[1] = w;
			count++;
		}
		else if(count == 2) {
			weights[2] = w;
			count++;
		}
	}
	
	public Weight getWeight(int i) {
		if(i < 0 || i > 2)
			return null;
		else
			return weights[i];
	}
	
	//DEPRECATED, ANIMATION CALCULATIONS DONE ON GPU NOW, SEE Model.draw();
	//return CPU calculated vertex weights
	public Vertex getVertex()  {
		if(weights != null) {
			int i = 0;
			
			float aa = 0;
			float bb = 0;
			float cc = 0;
			
			float mm = 0;
			float nn = 0;
			float oo = 0;
			
			i = 0;
			while(i < weights.length) {
				aa += weights[i].frame.defaultLocation.x * weights[i].weight;
				bb += weights[i].frame.defaultLocation.y * weights[i].weight;
				cc += weights[i].frame.defaultLocation.z * weights[i].weight;
				i++;
			}
			i = 0;
			while(i < weights.length) {
				mm += weights[i].frame.shiftLocation().x * weights[i].weight;
				nn += weights[i].frame.shiftLocation().y * weights[i].weight;
				oo += weights[i].frame.shiftLocation().z * weights[i].weight;
				i++;
			}
			Vertex d = new Vertex(aa, bb, cc);
			Vertex r = new Vertex(x,y,z);
			r = MathUtil.subtract(r, d);
			
			i = 0;
			Vertex avg[] = new Vertex[weights.length];
			while(i < avg.length) {
				avg[i] = MathUtil.rotate(r, new Quat(weights[i].frame.rotation.x, weights[i].frame.rotation.y, weights[i].frame.rotation.z, weights[i].frame.rotation.w));
				i++;
			}
			
			i = 0;
			Vertex rot = new Vertex(0,0,0);
			while(i < avg.length) {
				rot = MathUtil.add(rot, avg[i]);
				i++;
			}
			
			r = MathUtil.multiply(rot, (1.0f/avg.length));
			
			r = MathUtil.add(r, d);
			r = MathUtil.add(r, new Vertex(mm, nn, oo));
			
			return r;
		}
		else {
			return new Vertex(x,y,z);
		}
	}
	
	public Vertex copy() {
		return new Vertex(x,y,z);
	}
}
