package twelveengine.bsp;

import java.util.ArrayList;
import twelveengine.Game;
import twelveengine.Log;
import twelveengine.physics.BulletBSP;
import twelveengine.data.*;
import twelveengine.graphics.*;
import twelveutil.*;

public class BSP {	
	public Game game;
	public String name;
	public String file;
	
	//Bullet physics representation of the game world BSP.
	public BulletBSP bullet;
	
	//Meshes, physics models, and related information for this bsp
	public Part parts[];
	
	//Sky in this bsp
	public Sky sky;
	
	//Lights in this bsp
	public GlowLight glowLights[]; //TODO: COMBINE LINE LIGHTS AND GLOW LIGHTS INTO A SINGLE SUPER CLASS/ or something....
	public LineLight lineLights[];
	
	public BSP (Game w, String f) {
		game = w;
		file = f;
		
		readBsp();
		
		try {
			bullet = new BulletBSP(this, parts);
		}
		catch(Exception e) {
			System.err.println("Failed to build bullet bsp ~ ");
			e.printStackTrace();
		}
		
		
		//TODO: ....
		//Real talk, this is like the only time we will ever read and create assets and then dispose of them instead of cacheing them. 
		//We only build the PhysModel of the bsp so we can hand it to bullet so that bullet can turn it into a trimesh.
		int i = 0;
		while(i < parts.length) {
			parts[i].collision = null;
			i++;
		}
		System.gc();
	}
	
	public void step() {
		if(bullet != null)
			bullet.step();
		if(sky != null)
			sky.step();
	}
	
	public void readBsp() {
		Tag tag = game.tagger.openTag(file);
		
		//Load model and collision files
		TagSubObject meshList = tag.getObject("meshes");
		int i = 0;
		int j = meshList.getTotalObjects();
		parts = new Part[j];
		while(i < j) {
			TagSubObject mesh = meshList.getObject(i);
			parts[i] = readStatic(mesh.getProperty("model", "NULL"), mesh.getProperty("collision", "NULL"), TagUtil.makeVertex(mesh.getObject("location")), TagUtil.makeQuat(mesh.getObject("rotation")), mesh.getProperty("scale", 1.0f));
			i++;
		}
		
		//Load regular lights //TODO: ALSO THE TODO THATS LISTED ABOVE NEXT TO THE LIGHT ARRAYS
		TagSubObject glowList = tag.getObject("lights").getObject("glow");
		TagSubObject lineList = tag.getObject("lights").getObject("line");
		
		j = glowList.getTotalObjects();
		glowLights = new GlowLight[0];
		
		j = lineList.getTotalObjects();
		lineLights = new LineLight[0];
		
		//Load sky models and information
		TagSubObject skyList = tag.getObject("skys");
		i = 0;
		j = skyList.getTotalObjects();
		if(j != 0) {
			Vertex sunrotation[] = new Vertex[j];
			Vertex sunlightcolor[] = new Vertex[j];
			Vertex sunshadowcolor[] = new Vertex[j];
			
			String model[] = new String[j];
			int waitperiod[] = new int[j];
			int transitionperiod[] = new int[j];
			
			float start[] = new float[j];
			float end[] = new float[j];
			float density[] = new float[j];
			Vertex fogcolor[] = new Vertex[j];
			
			while(i < j) {
				TagSubObject sky = skyList.getObject(i);
				TagSubObject fog = sky.getObject("fog");
				TagSubObject sun = sky.getObject("sun");
				
				sunrotation[i] = new Vertex(sun.getObject("rotation").getProperty("x", 0.0f), 0.0f, sun.getObject("rotation").getProperty("y", 0.0f));
				sunlightcolor[i] = TagUtil.makeColor(sun.getObject("light_color"));
				sunshadowcolor[i] = TagUtil.makeColor(sun.getObject("shadow_color"));
				
				model[i] = sky.getProperty("model", "NULL");
				waitperiod[i] = sky.getProperty("wait_period", 6000);
				transitionperiod[i] = sky.getProperty("transition_period", 6000);
				
				start[i] = fog.getProperty("start", 32.0f);
				end[i] = fog.getProperty("end", 512.0f);
				density[i] = fog.getProperty("density", 0.3f);
				fogcolor[i] = TagUtil.makeColor(fog.getObject("color"));
				
				i++;
			}
			SunLight su = new SunLight(skyList.getProperty("sun_map_radius", 2048.0f), sunrotation, skyList.getProperty("near_shadow_dist", 256.0f), skyList.getProperty("overlap_shadow_dist", 64.0f), skyList.getProperty("far_shadow_dist", 512.0f), sunlightcolor, sunshadowcolor, skyList.getProperty("light_min_intensity", 0.1f), skyList.getProperty("light_max_intensity", 1.0f));
			sky = new Sky(this, su, model, waitperiod, transitionperiod, start, end, density, fogcolor);
		}
	}
	
	/** DEPRECATED **/
	/*int mnum = 0;
	public void readBSP(String s) {
		try {
			String currentLine;
			DataInputStream fileIn = new DataInputStream(FileUtil.getFile(s));
		    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));
		    
		    currentLine=fileReader.readLine();
		    while(!currentLine.equals("%EOF")) {
		    	if(currentLine.startsWith("mesh<"))
		    		mnum++;
			    currentLine=fileReader.readLine();
		    }
		    fileReader.close();
		} catch (IOException e) {
			System.err.println("FAILED TO READ SCENARIO: " + s);
		}
		
		System.out.println("\nBSP consists of " + mnum + " meshes... Building: ");
		meshes = new ModelGroup[mnum];
		scales = new float[mnum];
		phys = new PhysModel[mnum];
		meshPos = new Vertex[mnum];
		mnum = 0;
		
		try {
			String currentLine;
			DataInputStream fileIn = new DataInputStream(FileUtil.getFile(s));
		    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));
    		ArrayList<GlowLight> addLights = new ArrayList<GlowLight>();
    		ArrayList<LineLight> addLineLights = new ArrayList<LineLight>();
    		
    		String smodels[] = new String[0];
    		int swait[] = new int[0];
    		int strans[] = new int[0];
    		float sstart[] = new float[0];
    		float send[] = new float[0];
    		float sdense[] = new float[0];
    		Vertex scolor[] = new Vertex[0];
    		
    		Vertex lrotation[] = new Vertex[1];
    		float lradius = 2048;
    		float lnearShadow = 256;
    		float loverlapShadow = 64;
    		float lfarShadow = 1024;
    		Vertex llightColor[] = new Vertex[1];
    		Vertex lshadowColor[] = new Vertex[1];
    		float lmaxIntensity = 1.0f;
    		float lminIntensity = 0.2f;
		    
		    currentLine=fileReader.readLine();
		    while(!currentLine.equals("%EOF")) {
		    	if(currentLine.startsWith("bsp="))
		    		name = currentLine.split("=")[1];
		    	if(currentLine.startsWith("mesh<")) {
		    		String t = currentLine.split("=")[1];
		    		String u = currentLine.split("=")[0].split("<")[1].split(">")[0];
		    		System.out.print(" - ");
		    		readStatic(u, new Vertex(Float.parseFloat(t.split(",")[0]), Float.parseFloat(t.split(",")[1]), Float.parseFloat(t.split(",")[2])), Float.parseFloat(t.split(",")[3]));
		    	}
		    	if(currentLine.startsWith("sky<")) {
		    		String z[] = currentLine.split("<");
		    		smodels = new String[z.length-1];
		    		int i = 0;
		    		while(i < z.length - 1) {
		    			smodels[i] = z[i+1].split(">")[0].trim();
		    			i++;
		    		}
		    	}
		    	if(currentLine.startsWith("skyWaitPeriod<")) {
		    		String z[] = currentLine.split("<");
		    		swait = new int[z.length-1];
		    		int i = 0;
		    		while(i < z.length - 1) {
		    			swait[i] = Integer.parseInt(z[i+1].split(">")[0].trim());
		    			i++;
		    		}
		    	}
		    	if(currentLine.startsWith("skyTransitionPeriod<")) {
		    		String z[] = currentLine.split("<");
		    		strans = new int[z.length-1];
		    		int i = 0;
		    		while(i < z.length - 1) {
		    			strans[i] = Integer.parseInt(z[i+1].split(">")[0].trim());
		    			i++;
		    		}
		    	}
		    	if(currentLine.startsWith("fogStart<")) {
		    		String z[] = currentLine.split("<");
		    		sstart = new float[z.length-1];
		    		int i = 0;
		    		while(i < z.length - 1) {
		    			sstart[i] = Float.parseFloat(z[i+1].split(">")[0].trim());
		    			i++;
		    		}
		    	}
		    	if(currentLine.startsWith("fogEnd<")) {
		    		String z[] = currentLine.split("<");
		    		send = new float[z.length-1];
		    		int i = 0;
		    		while(i < z.length - 1) {
		    			send[i] = Float.parseFloat(z[i+1].split(">")[0].trim());
		    			i++;
		    		}
		    	}
		    	if(currentLine.startsWith("fogDensity<")) {
		    		String z[] = currentLine.split("<");
		    		sdense = new float[z.length-1];
		    		int i = 0;
		    		while(i < z.length - 1) {
		    			sdense[i] = Float.parseFloat(z[i+1].split(">")[0].trim());
		    			i++;
		    		}
		    	}
		    	if(currentLine.startsWith("fogColor<")) {
		    		String z[] = currentLine.split("<");
		    		scolor = new Vertex[z.length-1];
		    		int i = 0;
		    		while(i < z.length - 1) {
		    			String cl = z[i+1].split(">")[0].trim();
			    		Vertex c = new Vertex(Float.parseFloat(cl.split(",")[0]), Float.parseFloat(cl.split(",")[1]), Float.parseFloat(cl.split(",")[2]));
			    		float b = Float.parseFloat(cl.split(",")[3]);
			    		scolor[i] = MathUtil.multiply(c, b);
		    			i++;
		    		}
		    	}
		    	if(currentLine.startsWith("glowLight<")) {
		    		String z = currentLine.split("<")[1].split(">")[0];
		    		String f = currentLine.split(">")[1];
		    		Vertex c = new Vertex(Float.parseFloat(z.split(",")[0]), Float.parseFloat(z.split(",")[1]), Float.parseFloat(z.split(",")[2]));
		    		float b = Float.parseFloat(z.split(",")[3]);
		    		Vertex l = new Vertex(Float.parseFloat(f.split(",")[0]), Float.parseFloat(f.split(",")[1]), Float.parseFloat(f.split(",")[2]));
		    		float d = Float.parseFloat(f.split(",")[3]);
		    		addLights.add(new GlowLight(l, MathUtil.multiply(c, b), d));
		    	}
		    	if(currentLine.startsWith("lineLight<")) {
		    		String z = currentLine.split("<")[1].split(">")[0];
		    		String f = currentLine.split(">")[1];
		    		Vertex c = new Vertex(Float.parseFloat(z.split(",")[0]), Float.parseFloat(z.split(",")[1]), Float.parseFloat(z.split(",")[2]));
		    		float b = Float.parseFloat(z.split(",")[3]);
		    		Vertex l1 = new Vertex(Float.parseFloat(f.split(",")[0]), Float.parseFloat(f.split(",")[1]), Float.parseFloat(f.split(",")[2]));
		    		Vertex l2 = new Vertex(Float.parseFloat(f.split(",")[3]), Float.parseFloat(f.split(",")[4]), Float.parseFloat(f.split(",")[5]));
		    		float d = Float.parseFloat(f.split(",")[6]);
		    		addLineLights.add(new LineLight(l1, l2, MathUtil.multiply(c, b), d));
		    	}
		    	if(currentLine.startsWith("sunNearShadowDistance<")) {
		    		String t = currentLine.split("<")[1].split(">")[0];
		    		lnearShadow = Float.parseFloat(t);
		    	}
		    	if(currentLine.startsWith("sunOverlapShadowDistance<")) {
		    		String t = currentLine.split("<")[1].split(">")[0];
		    		loverlapShadow = Float.parseFloat(t);
		    	}
		    	if(currentLine.startsWith("sunFarShadowDistance<")) {
		    		String t = currentLine.split("<")[1].split(">")[0];
		    		lfarShadow = Float.parseFloat(t);
		    	}
		    	if(currentLine.startsWith("sunMapRadius<")) {
		    		String t = currentLine.split("<")[1].split(">")[0];
		    		lradius = Float.parseFloat(t);
		    	}
		    	if(currentLine.startsWith("sunMinIntensity<")) {
		    		String t = currentLine.split("<")[1].split(">")[0];
		    		lminIntensity = Float.parseFloat(t);
		    	}
		    	if(currentLine.startsWith("sunMaxIntensity<")) {
		    		String t = currentLine.split("<")[1].split(">")[0];
		    		lmaxIntensity = Float.parseFloat(t);
		    	}
		    	if(currentLine.startsWith("sunRotation<")) {
		    		String z[] = currentLine.split("<");
		    		lrotation = new Vertex[z.length-1];
		    		int i = 0;
		    		while(i < z.length - 1) {
		    			String cl = z[i+1].split(">")[0].trim();
			    		Vertex c = new Vertex(Float.parseFloat(cl.split(",")[0]), 0, Float.parseFloat(cl.split(",")[1]));
			    		lrotation[i] = c;
		    			i++;
		    		}
		    	}
		    	if(currentLine.startsWith("sunLightColor<")) {
		    		String z[] = currentLine.split("<");
		    		llightColor = new Vertex[z.length-1];
		    		int i = 0;
		    		while(i < z.length - 1) {
		    			String cl = z[i+1].split(">")[0].trim();
			    		Vertex c = new Vertex(Float.parseFloat(cl.split(",")[0]), Float.parseFloat(cl.split(",")[1]), Float.parseFloat(cl.split(",")[2]));
			    		float d = Float.parseFloat(cl.split(",")[3]);
			    		llightColor[i] = MathUtil.multiply(c, d);
		    			i++;
		    		}
		    	}
		    	if(currentLine.startsWith("sunShadowColor<")) {
		    		String z[] = currentLine.split("<");
		    		lshadowColor = new Vertex[z.length-1];
		    		int i = 0;
		    		while(i < z.length - 1) {
		    			String cl = z[i+1].split(">")[0].trim();
			    		Vertex c = new Vertex(Float.parseFloat(cl.split(",")[0]), Float.parseFloat(cl.split(",")[1]), Float.parseFloat(cl.split(",")[2]));
			    		float d = Float.parseFloat(cl.split(",")[3]);
			    		lshadowColor[i] = MathUtil.multiply(c, d);
		    			i++;
		    		}
		    	}
	    		currentLine=fileReader.readLine();
		    }
		    fileReader.close();
		    
		    SunLight sun = new SunLight(lradius, lrotation, lnearShadow, loverlapShadow, lfarShadow, llightColor, lshadowColor, lmaxIntensity, lminIntensity);
    		sky = new Sky(this, sun, smodels, swait, strans, sstart, send, sdense, scolor);
		    
		    int i = 0;
		    glowLights = new GlowLight[addLights.size()];
		    lineLights = new LineLight[addLineLights.size()];
		    while(i < addLights.size()) {
		    	glowLights[i] = addLights.get(i);
		    	i++;
		    }
		    i =0;
		    while(i < addLineLights.size()) {
		    	lineLights[i] = addLineLights.get(i);
		    	i++;
		    }
			System.out.println("done!");
		} catch (IOException e) {
			System.err.println("FAILED TO READ SCENARIO: " + s);
		}
		
	}*/
	
	public Part readStatic(String m, String c, Vertex l, Quat r, float d) {
		    ModelGroup mod = game.getModelGroup(m); 
		    PhysModel p = new PhysModel(c);
		    return new Part(mod, p, l, r, d);
	}
	
	public String toString() {
		return "BSP:" + name;
	}
	
	public void unload() {
		//TODO: stuff
	}
	
	public void getLights(ArrayList<GlowLight> glow, ArrayList<LineLight> line) {
		int i = 0; 
		while(i < glowLights.length) {
			glow.add(glowLights[i]);
			i++;
		}
		i = 0; 
		while(i < lineLights.length) {
			line.add(lineLights[i]);
			i++;
		}
	}
	
	public void drawSky(ArrayList<TrianglePacket> meshes) {
		if(sky != null)
			sky.draw(meshes);
	}
	
	public void draw(ArrayList<TrianglePacket> meshes) {
		int i = 0;
		while(i < parts.length) {
			parts[i].draw(meshes);
			i++;
		}
		/*
		int i = 0;
		glDisable(GL_LIGHTING);
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_LINES);
		glColor3f(1,1,1); 
		while(i < tris.size()) {
			glVertex3d(tris.get(i).a.x, tris.get(i).a.y, tris.get(i).a.z);
			glVertex3d(tris.get(i).b.x, tris.get(i).b.y, tris.get(i).b.z);
			
			glVertex3d(tris.get(i).b.x, tris.get(i).b.y, tris.get(i).b.z);
			glVertex3d(tris.get(i).c.x, tris.get(i).c.y, tris.get(i).c.z);
			
			glVertex3d(tris.get(i).c.x, tris.get(i).c.y, tris.get(i).c.z);
			glVertex3d(tris.get(i).a.x, tris.get(i).a.y, tris.get(i).a.z);
			i++;
		}
		glEnd();
		glEnable(GL_LIGHTING);
		glEnable(GL_TEXTURE_2D);
		*/
		//int j = 0;
		//int k = 0;
		
		/*while(i < draw.length) {
		if(draw[i] != null) {
			draw[i].draw();
		}
		i++;
		}*/
		/*
		while(k < z) {
			while(j < y) {
				while(i < x) {
					chunks[i][j][k].draw();
					i++;
				}
				i = 0;
				j++;
			}
			j = 0;
			k++;
		}*/
	}
}
