package twelveengine.graphics;

public class Animation {
	public String name;
	public AnimationFrame[] frames;
	public Animation(String anim, AnimationFrame[] frms) {
		name = anim;
		frames = frms;
	}
}