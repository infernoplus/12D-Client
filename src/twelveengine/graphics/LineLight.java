package twelveengine.graphics;

import static org.lwjgl.opengl.GL20.*;
import twelveengine.data.Vertex;
import twelveutil.MathUtil;

//Line lights are like glow lights but are lines, great for making a laser beam glow in a line. 
//It's faster to make one line light than 50 or so glow lights in a row, it looks better too.
public class LineLight {
	public Vertex position;
	public Vertex positionA;
	public Vertex positionB;
	public Vertex color;
	public float radius;
	public float cullRadius;
	
	public LineLight(Vertex a, Vertex b, Vertex c, float r) {
		positionA = a;
		positionB = b;
		color = c;
		radius = r;
		
		position = MathUtil.multiply(MathUtil.add(positionA, positionB), 0.5f);
		cullRadius = (MathUtil.length(positionA, positionB)/2)+radius;
	}
	
	public void applyToShader(int n) {
		glUniform3f(glGetUniformLocation(GraphicsCore.activeProgram, "lineA[" + n + "]"), positionA.x, positionA.y, positionA.z);
		glUniform3f(glGetUniformLocation(GraphicsCore.activeProgram, "lineB[" + n + "]"), positionB.x, positionB.y, positionB.z);
		glUniform4f(glGetUniformLocation(GraphicsCore.activeProgram, "lineColor[" + n + "]"), color.x, color.y, color.z, 1.0f);
		glUniform1f(glGetUniformLocation(GraphicsCore.activeProgram, "lineRadius[" + n + "]"), radius);
	}
}
