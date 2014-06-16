package twelveengine.bsp;

import java.util.ArrayList;

import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveengine.graphics.ModelGroup;
import twelveengine.graphics.SunLight;
import twelveengine.graphics.TrianglePacket;
import twelveutil.MathUtil;

public class Sky {
	public BSP bsp;
	public SunLight sun;
	
	public ModelGroup model[];
	
	public int waitPeriod[];
	public int transitionPeriod[];
	
	public float fogStart[];
	public float fogEnd[];
	public float fogDensity[];
	public Vertex fogColor[];
	
	public boolean multiSky;
	
	//TODO: Still needs work...
	public Sky(BSP b, SunLight sn, String m[], int w[], int t[], float s[], float e[], float d[], Vertex c[]) {
		bsp = b;
		sun = sn;
		
		model = new ModelGroup[m.length];
		waitPeriod = w;
		transitionPeriod = t;
		fogStart = s;
		fogEnd = e;
		fogDensity = d;
		fogColor = c;
		
		int i = 0;
		while(i < m.length) {
			model[i] = bsp.game.getModelGroup(m[i]);
			i++;
		}
		
		multiSky = model.length > 1;
		
		//TODO: fix it so get methods auto check for multisky
		if(multiSky) {
		time = 0;
		index = 0;
		next = 1;
		wait = true;
		lerp = 0;
		}
		else {
			time = 0;
			index = 0;
			next = 0;
			wait = true;
			lerp = 0;
		}
	}
	
	int time;
	int index;
	int next;
	boolean wait;
	float lerp;
	
	public void step() {
		if(!multiSky)
			return;
		
		if(wait)
			if(time < waitPeriod[index])
				time++;
			else {
				time = 0;
				wait = false;
			}
		else
			if(time < transitionPeriod[index])
				time++;
			else {
				time = 0;
				index++;
				next++;
				wait = true;
				
				if(index >= model.length)
					index = 0;
				if(next >= model.length)
					next = 0;
			}
		
		//System.out.println("index:" + index + " next:" + next + " time:" + time + " waiting:" + wait + " lerp:" + lerp);
		
		//System.out.println(getFogStart() + " " + getFogEnd() + " " + getFogDensity() + " " + MathUtil.toString(getFogColor()));
		
		if(!wait)
			lerp = (float)(time * (1.0/transitionPeriod[index]));
		else 
			lerp = 0;
	}
	
	/** All of the get methods below here automatically interpolate between skies and return the average value **/
	public float getFogStart() {
		return (lerp * fogStart[next]) + ((1 - lerp) * fogStart[index]);
	}
	
	public float getFogEnd() {
		return (lerp * fogEnd[next]) + ((1 - lerp) * fogEnd[index]);
	}
	
	public float getFogDensity() {
		return (lerp * fogDensity[next]) + ((1 - lerp) * fogDensity[index]);
	}
	
	public Vertex getFogColor() {
		return MathUtil.add(MathUtil.multiply(fogColor[next], lerp), MathUtil.multiply(fogColor[index], (1 - lerp)));
	}
	
	public Vertex getSunLocation() {
		Vertex r = MathUtil.multiply(getSunRotation(), 0.0174532925f); // convert to rad
		return MathUtil.rotate(new Vertex(0,0,-sun.radius), r);
	}
	
	public Vertex getSunRotation() {
		return MathUtil.add(sun.rotation[index], MathUtil.multiply(MathUtil.subtract(sun.rotation[next], sun.rotation[index]), lerp));
	}
	
	public Vertex getSunNormal() {
		Vertex r = MathUtil.multiply(getSunRotation(), 0.0174532925f); // convert to rad
		return MathUtil.normalize(MathUtil.rotate(new Vertex(0,0,-sun.radius), r));
	}
	
	public Vertex getSunLightColor() {
		return MathUtil.add(MathUtil.multiply(sun.lightColor[next], lerp), MathUtil.multiply(sun.lightColor[index], (1 - lerp)));
	}
	
	public Vertex getSunShadowColor() {
		return MathUtil.add(MathUtil.multiply(sun.shadowColor[next], lerp), MathUtil.multiply(sun.shadowColor[index], (1 - lerp)));
	}
	
	//Make sure when drawing a sky that it draws behind everything else in the scene. Don't want a skybox clipping into the real world.
	//Skyboxes have the unique property of being drawn without depth testing so first triangle in the model drawn first, last triangle drawn last.
	//So if you want something drawn on top of everything else, make sure its the last triangle in the sky model.
	//Simple skyboxes will just draw like normal, the depth thing only applies to 3d skys (IE halo skies)
	public void draw(ArrayList<TrianglePacket> meshes) {
		 //TODO: customization? specifically on position and scale
		if(multiSky)
			if(wait)
				model[index].pushToDrawQueue(meshes, new Vertex(0,0,0), new Quat(), 1f, new float[] { 1f });
			else {
				model[index].pushToDrawQueue(meshes, new Vertex(0,0,0), new Quat(), 1f, new float[] { 1f });
				model[next].pushToDrawQueue(meshes, new Vertex(0,0,0), new Quat(), 1f, new float[] { lerp });
			}
		else
			model[0].pushToDrawQueue(meshes, new Vertex(0,0,0), new Quat(), 1f, new float[] { 1f });

	}
	
	//TODO: using scalars to do transparency... //TODO: Specifically hardcoded the ScalarA of sky shaders to be the "transitioning %" make sure to document this or change it.
}
