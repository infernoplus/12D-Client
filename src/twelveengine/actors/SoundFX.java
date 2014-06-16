package twelveengine.actors;

import twelveengine.Game;
import twelveengine.audio.Sound;
import twelveengine.audio.SoundData;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveutil.MathUtil;

public class SoundFX extends Actor {
	public Sound sound;
	
	public float radius;
	public float volume;
	public boolean loop;
	
	//NEVER INSTANTIATE AN ACTOR OR ACTOR SUBCLASS MANUALLY / use engine.game.addObject(); Actor classes are just template for a tag like assets/object/character/generic to fill out and become an object in the world through
	public SoundFX(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":SoundFX";
		
		radius = tag.getProperty("radius", 10f);
		volume = tag.getProperty("volume", 1f);
		loop = tag.getProperty("loop", false);
		
		try {
			SoundData sd = game.getSound(tag.getProperty("sound", ""));
			sound = new Sound(sd, location, game.player.location, game.player.look, radius, volume, loop);
		}
		catch(Exception e) {
			System.err.println("~COULD NOT CREATE SOUND " + f + "~");
			e.printStackTrace();
		}
		
		update();
	}
	
	public void play() {
		sound.playSound();
	}
	
	public void pause() {
		sound.pauseSound();
	}
	
	public void stop() {
		sound.stopSound();
	}
	
	public void step() {
		lastFrame();
		update();
	}
	
	public void update() {
		Vertex o = MathUtil.subtract(MathUtil.inverse(game.player.location), location);
		o.x = -o.x;
		o = MathUtil.rotateZ(o, (game.player.look.z) * 0.0174532925f);
		o = MathUtil.multiply(o, 1/radius);
		
		sound.moveSound(o.x, o.y, o.z);
		sound.setVolume(volume);
	}
	
	public void move(Vertex a) {
		location = MathUtil.add(location, a);
		update();
	}
	
	public void setLocation(Vertex a) {
		location = a;
		update();
	}
	
	public String getName() {
		return name;
	}
	
	public void unload() {
		sound.killSound();
	}
}
