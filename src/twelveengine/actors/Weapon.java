package twelveengine.actors;

import java.util.ArrayList;

import twelveengine.Game;
import twelveengine.data.Quat;
import twelveengine.data.Vertex;
import twelveengine.graphics.Animation;
import twelveengine.graphics.AnimationGroup;
import twelveengine.graphics.ModelGroup;
import twelveengine.graphics.TrianglePacket;
import twelveutil.MathUtil;

public class Weapon extends Item {
	//TODO: after we revamp inventories, weapons next.
	public ModelGroup fpModel;
	public AnimationGroup fpAnimation;
	
	public boolean simulatedProjectile;
	
	public boolean primary = false;
	public boolean secondary = false;
	
	public String primaryFile;
	public String secondaryFile;
	
	public int rof;
	
	public float impulse;
	public int projectiles;
	
	public Vertex spread;
	
	public int shotCount = 0;
	public int shotTimer = 0;
	
	public int currentFrame = 0;
	public Animation currentAnim;
	
	public Weapon(Game w, int n, String f, Vertex l, Vertex v, Quat r) {
		super(w, n, f, l, v, r);
		internalName += ":Weapon";
		
		simulatedProjectile = tag.getProperty("simulatedprojectile", false);
		primaryFile = tag.getProperty("primaryfire", "");
		secondaryFile = tag.getProperty("secondaryfire", "");
		
		rof = tag.getProperty("rateoffire", 30);
		impulse = tag.getProperty("impulse", 256);
		projectiles= tag.getProperty("projectiles", 1);
		spread = new Vertex(tag.getProperty("spreadx", 0.1f),tag.getProperty("spready", 0.1f),0);
		
		fpModel = game.getModelGroup(tag.getProperty("fpmodel", "graphics/model/primitive/box.model"));
		fpAnimation = game.getAnimation(tag.getProperty("fpanimation", ""));
		
		currentAnim = fpAnimation.getAnimation("idle");
	}
	
	public void equipStep(Biped b) {
		cooldown();
		if(primary)
			primaryTrigger(b);
	}
	
	public void cooldown() {
		if(shotTimer > 0)
			shotTimer--;
	}
	
	public void primaryTrigger(Biped b) {
		if(shotTimer == 0) {
			primaryFire(b);
			shotTimer = rof;
		}
	}
	
	public void primaryFire(Biped b) {

	}
	
	public void drawEquipped(ArrayList<TrianglePacket> meshes, Vertex l, Quat r) {	
		model.pushToDrawQueue(meshes, l, r, scale);
	}
	
	//		new Vertex(1.57079633,1.57079633,0) rotation for fp view
	public void drawFP(ArrayList<TrianglePacket> fp, float f) {
		fpModel.pushToDrawQueue(fp, new Vertex(0,0,0), new Quat(), scale);
	}
}