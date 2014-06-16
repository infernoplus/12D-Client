package twelveengine.graphics;

import java.util.ArrayList;

import twelveengine.audio.Sound;

public class EffectEvent {
	public Effect effect;
	
	public ParticleSystem particles;
	
	public Sound sound;
	
	public boolean loop;
	public int start; //Time
	public int repeat; //Time
	
	public int type;
	//ParticleSystem = 0
	//Sound = 1
	
	//TODO: get rid of multiple constructors and just make subclasses, you fucking casul
	public EffectEvent(Effect e, ParticleSystem prt, boolean l, int s, int r) {
		effect = e;
		particles = prt;
		loop = l;
		start = s;
		repeat = r;
		
		type = 0;
	}
	
	public EffectEvent(Effect e, Sound snd, boolean l, int s, int r) {
		effect = e;
		sound = snd;
		loop = l;
		start = s;
		repeat = r;
		
		type = 1;
	}
	
	//TODO: here too
	//TODO: ALSO FIX LOCATION SHIT
	public void step() {
		if(type == 0) {
			particles.step();
		}
		if(type == 1) {
			sound.setViewSpaceLocation(effect.location, effect.game.player.location, effect.game.player.look);
		}
		
		int i = 0;
		while(i < effect.age.size()) {
			if(effect.age.get(i) == start)
				trigger();
			int j = effect.age.get(i) - start;
			if(loop && j > 1 && j % repeat == 0)
				trigger();
			i++;
		}
	}
	
	//TODO: maybe a switch lel
	public void trigger() {
		if(type == 0) {
			particles.playParticleSystem();
		}
		else if(type == 1) {
			sound.playSound();
		}
	}
	
	public void draw(ArrayList<TrianglePacket> meshes, float f) {
		if(type == 0)
			particles.draw(meshes, f);
	}
}