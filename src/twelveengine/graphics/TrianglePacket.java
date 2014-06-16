package twelveengine.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.ARBVertexBufferObject;

import twelveengine.data.Quat;
import twelveengine.data.Vertex;

public class TrianglePacket {
	//Contains a buffer of triangles to draw, and information regarding the uniforms for this... 
	//These are sorted by shader and material before drawing to improve performance in OGL and GLSL.
	//Look in GrahpicsCore.renderGraphis(float f) for more information
	public FloatBuffer buffer;
	public int length; //length of buffer
	
	public Shader shader;
	public boolean scalars;
	public float scalarValues[];
	
	public Vertex location;
	public Quat rotation;
	public float scale;
	
	public float radius;
	
	public boolean skinned;
	public Frame frames[];

	public TrianglePacket(FloatBuffer fb, int i, Shader s, Vertex l, Quat r, boolean b, Frame[] fr, float sc, float rd) {
		buffer = fb;
		length = i;
		shader = s;
		location = l;
		rotation = r;
		scale = sc;
		radius = rd;
		skinned = b;
		frames = fr;
		scalars = false;
	}
	
	public TrianglePacket(FloatBuffer fb, int i, Shader s, Vertex l, Quat r, boolean b, Frame[] fr, float sc, float rd, float sv[]) {
		buffer = fb;
		length = i;
		shader = s;
		location = l;
		rotation = r;
		scale = sc;
		radius = rd;
		skinned = b;
		frames = fr;
		scalars = true;
		scalarValues = sv;
	}
	
	//TODO: FFS STOP USING GLGETUNIFORMLOCATION DYNAMICALLY YOU FUCKING CASUAL
	//TODO: THIS GETS 2 TODOS BECAUSE I REALLY FUCKING MEAN IT
	public void draw(int shaderProgram) {
		//Setup all uniforms
		glUniform4f(glGetUniformLocation(shaderProgram, "modelPosition"), location.x, location.y, location.z, 0f);  // Position //Lightvector
		glUniform1f(glGetUniformLocation(shaderProgram, "modelScale"), scale);
		if(rotation.x == 0f && rotation.y == 0f && rotation.z == 0f) {
			glUniform1i(glGetUniformLocation(shaderProgram, "bRotation"), 0);
		}
		else {
			glUniform1i(glGetUniformLocation(shaderProgram, "bRotation"), 1);
			glUniform4f(glGetUniformLocation(shaderProgram, "modelRotation"), rotation.x, rotation.y, rotation.z, rotation.w);  // Rotation
		}
		if(skinned) {			
			glUniform1i(glGetUniformLocation(shaderProgram, "bSkin"), 1);
			int j = 0;
			while(j < frames.length) {
				//TODO: can most likely make this faster using VBO floatbuffer
				glUniform3f(glGetUniformLocation(shaderProgram, "bonesOffset[" + j +"]"), frames[j].shiftLocation().x,  frames[j].shiftLocation().y, frames[j].shiftLocation().z);
				glUniform3f(glGetUniformLocation(shaderProgram, "bonesPos[" + j +"]"), frames[j].defaultLocation.x,  frames[j].defaultLocation.y, frames[j].defaultLocation.z);
				glUniform4f(glGetUniformLocation(shaderProgram, "bonesRot[" + j +"]"), frames[j].rotation.x,  frames[j].rotation.y, frames[j].rotation.z, frames[j].rotation.w);
				j++;
			}
		} 
		else {
			glUniform1i(glGetUniformLocation(shaderProgram, "bSkin"), 0);
		}
		if(scalars)
			shader.glMaterialSet(shaderProgram, scalarValues);
		else
			shader.glMaterialSet(shaderProgram);
	
		//Draw model	
		if(skinned) {
			int stride = 60;
			int gl_Vertex = glGetAttribLocation(shaderProgram, "gl_Vertex");
			int gl_Normal = glGetAttribLocation(shaderProgram, "gl_Normal");
			int gl_MultiTexCoord0 = glGetAttribLocation(shaderProgram, "gl_MultiTexCoord0");
			int indices = glGetAttribLocation(shaderProgram, "indices");
			int weights = glGetAttribLocation(shaderProgram, "weights");
			int fc = length*3;
			
			glEnableClientState(GL_VERTEX_ARRAY);
			glEnableClientState(GL_NORMAL_ARRAY);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, GraphicsCore.vbo);
			glVertexPointer(3, GL_FLOAT, stride, 0);
			glNormalPointer(GL_FLOAT, stride, 12);
			glTexCoordPointer(3, GL_FLOAT, stride, 24);
			glVertexAttribPointer(indices, 3, GL_FLOAT, true, stride, 36);
			glVertexAttribPointer(weights, 3, GL_FLOAT, true, stride, 48);
			
			ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
			
			glEnableVertexAttribArray(gl_Vertex);
			glEnableVertexAttribArray(gl_Normal);
			glEnableVertexAttribArray(gl_MultiTexCoord0);
			glEnableVertexAttribArray(indices);
			glEnableVertexAttribArray(weights);
			
			glDrawArrays(GL_TRIANGLES, 0, fc);
		}
		else {
			int stride = (3 + 3 + 3 + 3) * 4;
			int gl_Vertex = glGetAttribLocation(shaderProgram, "gl_Vertex");
			int gl_Normal = glGetAttribLocation(shaderProgram, "gl_Normal");
			int gl_MultiTexCoord0 = glGetAttribLocation(shaderProgram, "gl_MultiTexCoord0");
			int tangent = glGetAttribLocation(shaderProgram, "tangent");
			int fc = length*3;
				
			glEnableClientState(GL_VERTEX_ARRAY);
			glEnableClientState(GL_NORMAL_ARRAY);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, GraphicsCore.vbo);
			glVertexPointer(3, GL_FLOAT, stride, 0);
			glNormalPointer(GL_FLOAT, stride, 12);
			glTexCoordPointer(3, GL_FLOAT, stride, 24);
			glVertexAttribPointer(tangent, 3, GL_FLOAT, true, stride, 36);
			ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
			
			glEnableVertexAttribArray(gl_Vertex);
			glEnableVertexAttribArray(gl_Normal);
			glEnableVertexAttribArray(gl_MultiTexCoord0);
			glEnableVertexAttribArray(tangent);
			
			glDrawArrays(GL_TRIANGLES, 0, fc);
		}
	}
}
