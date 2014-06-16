package twelveengine.data;

public class Quat {
	public float x;
	public float y;
	public float z;
	public float w;
	
	public Quat(float a, float b, float c, float d) {
		x = a;
		y = b;
		z = c;
		w = d;
		float mag = (float)Math.sqrt(x * x + y * y + z * z + w * w);
		if (mag > 0.0f) {
			this.x = x / mag;
			this.y = y / mag;
			this.z = z / mag;
			this.w = w / mag;
		} else {
			this.x = 0;
			this.y = 0;
			this.z = 0;
			this.w = 1;
		}
	}
	
	//Creates a blank quat {0,0,0,1}
	public Quat() {
		x = 0;
		y = 0;
		z = 0;
		w = 1;
	}

	public Quat copy() {
		return new Quat(x,y,z,w);
	}
}
