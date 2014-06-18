package twelveengine;

import java.util.ArrayList;

import twelveengine.actors.*;
import twelveengine.audio.*;
import twelveengine.bsp.*;
import twelveengine.data.*;
import twelveengine.script.*;
import twelveengine.graphics.*;
import twelveengine.ui.*;
import twelvelib.net.packets.*;
import twelveutil.*;


public class Game {
	//Core Stuff
	public Engine engine;
	
	public Scenario scenario;
	
	public static int drawTime = 16;
	public static int stepTime = 32;
	public static int netTime = 32;
	public int time = 0;
	
	public BSP bsp;
	
	public TagReader tagger = new TagReader();
	public ArrayList<Actor> actors = new ArrayList<Actor>();
	
	//All models animations materials ands sounds used by objects in this game. This saves a lot of memory. 
	//Use getModel() and etc. Do not instantiate it yourself, the only exception to this is if it will be directly modified
	//EXAMPLE: You have 15 cyborgs on the map, instead of loading 15 cyborg models you load 1 and the model data is shared between all 15 cyborgs. Same for animations and what not.
	//TODO: Physmodel?
	public ArrayList<ModelGroup> modelGroups = new ArrayList<ModelGroup>();
	public ArrayList<Model> models = new ArrayList<Model>();
	public ArrayList<ConvexHull> hulls = new ArrayList<ConvexHull>();
	public ArrayList<AnimationGroup> animations = new ArrayList<AnimationGroup>();
	public ArrayList<Shader> shaders = new ArrayList<Shader>();
	public ArrayList<Texture> textures = new ArrayList<Texture>();
	public ArrayList<CubeMap> cubemaps = new ArrayList<CubeMap>();
	public ArrayList<SoundData> sounds = new ArrayList<SoundData>();
	public ArrayList<Tag> effects = new ArrayList<Tag>(); //These three things store the tag because we have to regularly build new systems/particles/effects.
	public ArrayList<Tag> partSys = new ArrayList<Tag>();
	public ArrayList<Tag> particles = new ArrayList<Tag>();
	public ArrayList<Ui> ui = new ArrayList<Ui>();
	
	public ScriptManager scripts;
	
	public Player player;
	
	public boolean menuOpen = true;
	public boolean consoleOpen = false;
	public Ui menu;
	public Ui console;
	
	public Game(Engine e, String s) {
		engine = e;
		loadLevel(s);
		console = new Ui(this, "ui/console/console.ui");
		ui.add(console);
		menu = new Ui(this, "ui/basic/basic.ui");
		ui.add(menu);
		
		player = new Player(this);
		
 		/**Actor a = createTag("scenery/anim/weapon.scenery", 3, new Vertex(0,300,75), new Vertex(0,0,0), new Quat(0,0,0.77f,1));
		addActor(a);
 		a = createTag("scenery/anim/arms.scenery", 3, new Vertex(0,300,75), new Vertex(0,0,0), new Quat(0,0,0,0));
		addActor(a);**/
		
 		/*Actor b = createTag("character/generic/generic.pawn", 4, new Vertex(0,300,125), new Vertex(0,0,0), new Quat(0,0,0,0));
		addActor(b);
		player.givePawn((Pawn)b);*/
		
		Actor a = createTag("character/steve/steve.ragdoll", 5, new Vertex(0,200, 100), new Vertex(0,0,0), new Quat(0,0,0,1));
		addActor(a);
	
		//testHud.setScalar("shield bar fill", 0f, 0, 0, 0);
		//testHud.setScalar("health bar fill", 0.5f, 0.9f, 0.88f, 0.254f);
		//testHud.setScalar("health bar fill", 1.0f, 0.466f, 0.654f, 1.0f);
		//testHud.setScalar("health bar fill", 0.4f, 1.0f, 0.154f, 0.066f);
		
		//Actor a = createTag("object/scenery/test.scenery", -1, new Vertex(0,0,-20), new Vertex(), new Quat());
		//addActor(a);
		
		//a = createTag("object/item/equipment/t4frag.equipment", -1, new Vertex(0,0,0), new Vertex(0,0,0), new Vertex(0,0,0));
		//addActor(a);
		
		/*a = createTag("object/scenery/testpistol.scenery", -1, new Vertex(100,0,0), new Vertex(0,0,0), new Vertex(0,0,0));
		addActor(a);
		a = createTag("object/scenery/testarms.scenery", -1, new Vertex(100,0,0), new Vertex(0,0,0), new Vertex(0,0,0));
		addActor(a);
		
		/*
 		Actor a = createTag("object/scenery/test.scenery", -1, new Vertex(0,0,0), new Vertex(0,0,0), new Vertex(0,0,0));
		addActor(a);
		a = createTag("object/character/generic.biped", -1, new Vertex(0,0,0), new Vertex(0,0,0), new Vertex(0,0,0));
		addActor(a);
		a = createTag("object/item/weapon/57smg.weapon", -1, new Vertex(0,0,0), new Vertex(0,0,0), new Vertex(0,0,0));
		addActor(a);
		a = createTag("object/item/equipment/t4frag.equipment", -1, new Vertex(0,0,0), new Vertex(0,0,0), new Vertex(0,0,0));
		addActor(a);
		a = createTag("object/item/brokenkey.item", -1, new Vertex(0,0,0), new Vertex(0,0,0), new Vertex(0,0,0));
		addActor(a);
		a = createTag("object/projectile/cr55bullet.hitscan", -1, new Vertex(0,0,0), new Vertex(0,1,0), new Vertex(0,0,0));
		addActor(a);
		a = createTag("object/projectile/t4frag.projectile", -1, new Vertex(0,0,0), new Vertex(0,1,0), new Vertex(0,0,0));
		addActor(a);*/
		
		//bsp.meshes[0].move(new Vertex(100,-0.1,0.3));
		
		//Actor b = addActor(new Bender(this, -1, "graphics/model/item/weapon/57smg/57smg.tbj", "graphics/model/item/weapon/57smg/57smg-reload.taf"));
		//b.move(new Vertex(0,30,0));
		//b.rotate(new Vertex(0,0,1.3));
		//Actor c = addActor(new Bender(this, -1, "graphics/model/item/weapon/arms.tbj", "graphics/model/item/weapon/57smg/57smg-reload.taf"));
		//c.move(new Vertex(0,30,0));
		//c.rotate(new Vertex(0,0,1.3));
		
		//addSound("audio/music/sleepwalker.flac", new Vertex(0,0,0), 10f, 0.5f, false);
		
		//SoundFX test = new SoundFX(this, 0, "audio/music/alone.flac", 20.0f, 1.0f, true);
		//test.play();
		//addActor(test);
	}
	
	/*/** totes not debug method i promise meng **/
	public void randomPhysicsBox() {
		/*Actor a = createTag("scenery/box/box.rigidbody", -2, new Vertex(0,270,70), MathUtil.randomVertex(-1, 1), new Quat(0,0,0,1));
		addActor(a);
		a = createTag("item/weapon/sniperrifle/sniperrifle.item", -2, new Vertex(0,270,65), MathUtil.randomVertex(-1, 1), new Quat(0,0,0,1));
		addActor(a);
		a = createTag("item/weapon/assaultrifle/assaultrifle.item", -2, new Vertex(0,270,75), MathUtil.randomVertex(-1, 1), new Quat(0,0,0,1));
		addActor(a);*/
		Actor a = createTag("character/steve/steve.ragdoll", 5, new Vertex(0,200, 125), new Vertex(0,0,0), new Quat(0,0,0,1));
		addActor(a);
	}
	
	public void loadLevel(String s) {
		scenario = new Scenario(this, s);
		bsp = new BSP(this, scenario.bsp);
		scripts = new ScriptManager(this, scenario.script);
	}
	
	public void step() {
		//Game step
		//Step actors, and bsp
		player.input();
		bsp.step(); //Update bsp and bullet physics objects.
		if(engine.game.getUiFocus() != null)
			engine.game.getUiFocus().step();
		int i = 0;
		while(i  <  actors.size()) {
			if(actors.get(i).garbage) {
				actors.remove(i);
				i--;
			}
			else
				actors.get(i).step();
			i++;
		}
		scripts.step();
		player.post();
	}
	
	public void shaderStep(float f) {
		//Step shaders for animated shaders n stuff
		//f is the amount we step because this updates like a framerate
		int i = 0;
		while(i < shaders.size()) {
			shaders.get(i).step(f);
			i++;
		}
	}
	
	//This method is called before network.step(). Add any packets to the out queue 
	public void netStep() {
		player.netStep();
	}
	
	/**Register any new types of actors here!**/
	//If you want to create an actor from a tag then this is the method to do it from.
	public Actor createTag(String f, int n, Vertex l, Vertex v, Quat r) {
		String s = f.split("\\.")[f.split("\\.").length-1];
		Actor a = null;
		
		if(s.equals("actor"))
			a = new Actor(this, n, f, l, v, r);
		
		if(s.equals("physical"))
			a = new Physical(this, n, f, l, v, r);
		
		if(s.equals("rigidbody"))
			a = new RigidBody(this, n, f, l, v, r);
		
		if(s.equals("ragdoll"))
			a = new Ragdoll(this, n, f, l, v, r);
		
		if(s.equals("scenery"))
			a = new Scenery(this, n, f, l, v, r);
		
		if(s.equals("biped"))
			a = new Biped(this, n, f, l, v, r);
		
		if(s.equals("pawn"))
			a = new Pawn(this, n, f, l, v, r);
		
		if(s.equals("item"))
			a = new Item(this, n, f, l, v, r);
		
		if(s.equals("weapon"))
			a = new Weapon(this, n, f, l, v, r);
		
		if(s.equals("equipment"))
			a = new Equipment(this, n, f, l, v, r);
		
		if(s.equals("projectile"))
			a = new Projectile(this, n, f, l, v, r);
		
		if(s.equals("hitscan"))
			a = new Hitscan(this, n, f, l, v, r);
		
		if(s.equals("soundfx"))
			a = new SoundFX(this, n, f, l, v, r);
		
		if(a == null) {
			System.err.println("UNRECOGNIZED TAG FORMAT: " + f);
		}
		return a;
	}
	
	public Actor addActor(Actor a) {
		actors.add(a);
		return a;
	}
	
	public void addSimulatedObject(Actor a) {
		//Packet10Instantiate i = new Packet10Instantiate(-1, a.file, a.location.x, a.location.y, a.location.z, a.velocity.x, a.velocity.y, a.velocity.z, a.rotation.x, a.rotation.y, a.rotation.z);
		//i.simulated = true;
	   // engine.network.packetsOut.add(i);
	    addActor(a);
	}
	
	//By SID
	public Actor getActor(String s) {
		int i = 0;
		while(i < actors.size()) {
			if(actors.get(i).sid.equals(s))
				return actors.get(i);
			i++;
		}
		return null;
	}
	
	//By NID
	public Actor getActor(int n) {
		int i = 0;
		while(i < actors.size()) {
			if(actors.get(i).nid == n)
				return actors.get(i);
			i++;
		}
		return null;
	}
	
	public void removeActor(int n) {
		int i = 0;
		while(i < actors.size()) {
			if(actors.get(i).nid == n) {
				if(player.pawn != null)
					if(actors.get(i).nid == player.pawn.nid)
						player.pawnKilled();
				actors.remove(i);
			}
			i++;
		}
	}
	
	//Gets the next negative nid (negative nids are clientside and don't sync)
	int onid = 0;
	public int getNid() {
		onid--;
		return onid;
	}
	
	public Ui getUiFocus() {
		return player.uiFocus;
	}
	

	public ModelGroup getModelGroup(String s) {
		int i = 0;
		while(i < modelGroups.size()) {
			if(modelGroups.get(i).file.equals(s)) {
				return modelGroups.get(i);
			}
			i++;
		}
		ModelGroup m = new ModelGroup(this, s);
		modelGroups.add(m);
		return m;
	}	
	
	public Model getModel(String s) {
		int i = 0;
		while(i < models.size()) {
			if(models.get(i).file.equals(s)) {
				return models.get(i);
			}
			i++;
		}
		Model m = new Model(this, s);
		models.add(m);
		return m;
	}
	
	public ConvexHull getHull(String s) {
		int i = 0;
		while(i < hulls.size()) {
			if(hulls.get(i).file.equals(s)) {
				return hulls.get(i);
			}
			i++;
		}
		ConvexHull c = new ConvexHull(s);
		hulls.add(c);
		return c;
	}
	
	public Shader getShader(String s) {
		int i = 0;
		while(i < shaders.size()) {
			if(shaders.get(i).file.equals(s)) {
				return shaders.get(i);
			}
			i++;
		}
		Shader a = new Shader(this, s);
		shaders.add(a);
		return a;
	}
	
	//This compares exsisting glsl shaders with the one that we provide.
	//If it finds a glsl shader that matches the one we want, we get it's index, otherwise we return -1 and the shader will generate one.
	public int getGLSLShader(String[] s, boolean u, boolean c, boolean m) {
		int i = 0;
		int j = 0;
		while(j < shaders.size()) {
			boolean b = true;
			Shader h = shaders.get(j);
			if(h.unlit != u)
				b = false;
			if(h.castShadows != c)
				b = false;
			if(h.mipMaps != m)
				b = false;
			while(i < h.channels.length && b) {
				if(h.channels[i] != null && s[i] != null) {
					if(!h.channels[i].equals(s[i])) {
						b = false;
					}
				}
				else {
					if(!(h.channels[i] == null && s[i] == null))
						b = false;
				}
				i++;
			}
			if(b)
				return h.glslProgram;
			
			i = 0;
			j++;
		}
		return -1;
	}
	
	//Throws exception if the texture file does not real.
	public Texture getTexture(String s, boolean m) throws Exception {
		int i = 0;
		while(i < textures.size()) {
			if(textures.get(i).file.equals(s)) {
				return textures.get(i);
			}
			i++;
		}
		Texture t = new Texture(s, m);
		textures.add(t);
		return t;
	}
	
	//Throws exception if the cubemap file does not real.
	public CubeMap getCubeMap(String s, boolean m) throws Exception {
		int i = 0;
		while(i < cubemaps.size()) {
			if(cubemaps.get(i).file.equals(s)) {
				return cubemaps.get(i);
			}
			i++;
		}
		CubeMap t = new CubeMap(s, m);
		cubemaps.add(t);
		return t;
	}
	
	public AnimationGroup getAnimation(String s) {
		int i = 0;
		while(i < animations.size()) {
			if(animations.get(i).file.equals(s)) {
				return animations.get(i);
			}
			i++;
		}
		AnimationGroup a = new AnimationGroup(s);
		animations.add(a);
		return a;
	}
	
	public SoundData getSound(String s) {
		int i = 0;
		while(i < sounds.size()) {
			if(sounds.get(i).file.equals(s))
				return sounds.get(i);
			i++;
		}
		SoundData sd = new SoundData(s);
		sounds.add(sd);
		return sd;
	}
	
	public Tag getEffect(String s) {
		int i = 0;
		while(i < effects.size()) {
			if(effects.get(i).file.equals(s)) {
				return effects.get(i);
			}
			i++;
		}
		Tag t = tagger.openTag(s);
		effects.add(t);
		return t;
	}
	
	public Tag getParticleSystem(String s) {
		int i = 0;
		while(i < partSys.size()) {
			if(partSys.get(i).file.equals(s)) {
				return partSys.get(i);
			}
			i++;
		}
		Tag t = tagger.openTag(s);
		partSys.add(t);
		return t;
	}
	
	public Tag getParticle(String s) {
		int i = 0;
		while(i < particles.size()) {
			if(particles.get(i).file.equals(s)) {
				return particles.get(i);
			}
			i++;
		}
		Tag t = tagger.openTag(s);
		particles.add(t);
		return t;
	}
	
	public void pauseSounds() {
		
	}
	
	public void resumeSounds() {
		
	}
	
	public void adjustSoundVolume() {
		
	}
	
	public void unloadGame() {
		int i = 0;
		while(i < actors.size()) {
			actors.get(i).unload();
			i++;
		}
		bsp.unload();
		tagger.unload();
	}

	public void checkSoundArray() {
		
	}
	
	public Sky getSky() {
		return bsp.sky;
	}
	
	public void getLights(ArrayList<GlowLight> glow, ArrayList<LineLight> line) {
		bsp.getLights(glow, line);
	}
	
	public void drawGame(ArrayList<TrianglePacket> meshes, float f) {
		drawMap(meshes);
		drawActors(meshes, f);
	}
	
	public void drawMap(ArrayList<TrianglePacket> meshes) {
		bsp.draw(meshes);
	}
	
	public void drawActors(ArrayList<TrianglePacket> meshes, float f) {
		//origin.draw();
		int i = 0;
		while(i  <  actors.size()) {
			actors.get(i).draw(meshes, f);
			i++;
		}
	}

	public boolean isOnline() {
		return engine.network.online;
	}
	
	public void sendPacket(Packet p) {
		engine.network.send(p);
	}
}
