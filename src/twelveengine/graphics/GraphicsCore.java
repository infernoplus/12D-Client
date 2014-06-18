/*
 * 12characters Snake Engine
 * © 2011 12characters Games
 * http://www.12charactersengines.com/
 * 
 * GraphicsCore
 * Renders window and calls Core methods designated to perform specific graphics rendering.
 */

package twelveengine.graphics;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.ARBSeamlessCubeMap.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;

import twelveengine.Engine;
import twelveengine.Log;
import twelveengine.Settings;
import twelveengine.bsp.Sky;
import twelveengine.data.Vertex;
import twelveutil.FileUtil;
import twelveutil.MathUtil;

public class GraphicsCore {
	
	/** Passed Engine Instance */
	private Engine engine;
	//TODO: CHECK FOR DRIVER SERIALIZAION
	//TODO: FIX ANY WARNING IN OGL COMPILER
	
	//camera
	public static float fov = 90;
	public static float nearClip = 1;
	public static float farClip = 8192;
	public static float aspectRatio;
	public static Vertex tran = new Vertex(0,0,0);
	public static Vertex rot = new Vertex(0,0,0);
	public static Vertex look = new Vertex(0,0,0);
	public static Vertex up = new Vertex(0,0,0);
	
	//lights
	public ArrayList<GlowLight> glowLights = new ArrayList<GlowLight>();
	public ArrayList<LineLight> lineLights = new ArrayList<LineLight>();
	
	//Specify distance for differnt shadow qualities.
	public static float moireNear;
	public static float moireFar;
	public static float pcfNear = 2.2f; //TODO: not set correctly just using presets atm, sets size of pcf lookups must be proportional to eachother in terms of "size"...
	public static float pcfFar = 1f;
	public static float radiusNear;
	public static float radiusFar;
	
	//glsl shaders, vbo, fbo
	public static int worldFBO;
	public static int worldTexture;
	public static int fpFBO;
	public static int fpTexture;
	public static int uiFBO;
	public static int uiTexture;
	
	public static int nearShadowFBO;
	public static int nearDepthTexture;
	public static int nearShadowSize = 4096; //Size = quality
	
	public static int farShadowFBO;
	public static int farDepthTexture;
	public static int farShadowSize = 4096; //Size = quality
	
	public static int vbo;
	
	public static ArrayList<Integer> programs = new ArrayList<Integer>();
	public static ArrayList<Integer> verts = new ArrayList<Integer>();
	public static ArrayList<Integer> frags = new ArrayList<Integer>();
	
	public static String postShaderSource = "multipurpose/glsl/post";
	public static int postProgram;
	public static int postVert;
	public static int postFrag;
	
	public static String shadowMappingShaderSource = "multipurpose/glsl/shadow";
	public static int shadowProgram;
	public static int shadowVert;
	public static int shadowFrag;
	
	public static int activeProgram = 0;
	
	/** Some necessary graphics files. */
	/** Display Settings */
	public int displayWidth;
	public int displayHeight;
	private boolean enableFullscreen;
	private boolean enableVSync;
	/** This is used to "zoom" on the player depending on screen resolution */

	int colorTextureID;
	int framebufferID;
	int depthRenderBufferID;
	
	/** Constructor for the GraphicsCore. Sets up the Display and initializes OpenGL.
	 * @param passedGame The Engine instance
	 */
	public GraphicsCore(Engine passedGame) {
		Log.log("Initializing Graphics...", "Graphics");
		engine = passedGame;
		displayWidth = Settings.getInt("displayWidth");
		displayHeight = Settings.getInt("displayHeight");
		enableFullscreen = Settings.getBool("enableFullscreen");
		enableVSync = Settings.getBool("enableVSync");
		// Create/Start Display
		Log.log("Initializing Display...", "Graphics");
		try {
		    Display.setTitle(engine.gameTitle);
	    	if (enableFullscreen) {
		    	displayWidth = Display.getDesktopDisplayMode().getWidth();
		    	displayHeight = Display.getDesktopDisplayMode().getHeight();
		    	Display.setDisplayMode(Display.getDesktopDisplayMode());
		    	Display.setFullscreen(enableFullscreen);
		    } else
		    	Display.setDisplayMode(new DisplayMode(displayWidth,displayHeight));
			Display.setVSyncEnabled(enableVSync);	
			Display.create();
			Log.log("Loaded OpenGL device: " + glGetString(GL_RENDERER), "Graphics");
			Log.log("OpenGL Version/Driver: " + glGetString(GL_VERSION), "Graphics");
			Log.log("OpenGL Vendor: " + glGetString(GL_VENDOR), "Graphics");
			Log.log("Display started!", "Graphics");
		} catch (LWJGLException e) {
		    e.printStackTrace();
		    System.exit(0);
		}
		
		aspectRatio = (float)displayWidth/displayHeight;
		
		// Create OpenGL stuff
		Log.log("Initializing OpenGL features...", "Graphics");		
		glViewport (0, 0, displayWidth, displayHeight);	
		glMatrixMode (GL_PROJECTION);
		glLoadIdentity ();		
		GLU.gluPerspective (fov, (float)displayWidth/displayHeight, 1f, 4096.0f);		
		glMatrixMode (GL_MODELVIEW);								
		glLoadIdentity ();								
		

		glClearColor (0.0f, 0.0f, 0.0f, 0.0f);						
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glClearDepth (1.0f);							
		glDepthFunc (GL_LEQUAL);								
		glEnable(GL_DEPTH_TEST);	
		glEnable(GL_CULL_FACE);
		glShadeModel(GL_SMOOTH);				
		glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
		
		glEnable(GL_TEXTURE_CUBE_MAP);
		glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
		
		glEnable (GL_LINE_SMOOTH);
		
		//slow?
		glHint (GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glHint (GL_POINT_SMOOTH_HINT, GL_NICEST);	
		glHint (GL_LINE_SMOOTH_HINT, GL_NICEST);	
		glHint (GL_POLYGON_SMOOTH_HINT, GL_NICEST);	
		
		//fbo
		int n[];
		n = generateRenderFBO(displayWidth, displayHeight);
		worldFBO = n[0];
		worldTexture = n[1];
		n = generateRenderFBO(displayWidth, displayHeight);
		fpFBO = n[0];
		fpTexture = n[1];
		n = generateRenderFBO(displayWidth, displayHeight);
		uiFBO = n[0];
		uiTexture = n[1];
		//TODO: get the shadow map size and pcf sample rate from settings (size = quality = performance)
		n = generateShadowFBO(nearShadowSize);
		nearShadowFBO = n[0];
		nearDepthTexture = n[1];
		n = generateShadowFBO(farShadowSize);
		farShadowFBO = n[0];
		farDepthTexture = n[1];
		
		//vbo
		vbo = createVBOID();
		
		//glsl shaders
		Log.log("Loading shaders...", "Graphics");
		//Shadow Mapping Shaders
		try {
			shadowVert = loadShader(shadowMappingShaderSource + ".vert", ARBVertexShader.GL_VERTEX_SHADER_ARB);
		} catch (Exception e) {
			Log.log("Failed to compile shadowmapping vertex shader~", "Graphics", 3);
			e.printStackTrace();
		}
		try {
			shadowFrag = loadShader(shadowMappingShaderSource + ".frag", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		} catch (Exception e) {
			Log.log("Failed to compile shadowmapping frag shader~", "Graphics", 3);
			e.printStackTrace();
		}
		
		shadowProgram = ARBShaderObjects.glCreateProgramObjectARB();
        ARBShaderObjects.glAttachObjectARB(shadowProgram, shadowVert);
        ARBShaderObjects.glAttachObjectARB(shadowProgram, shadowFrag);
        
        //hardcoding is bad... welp \(._.)/
		GL20.glBindAttribLocation(shadowProgram, 12, "indices");
		GL20.glBindAttribLocation(shadowProgram, 13, "weights");
		GL20.glBindAttribLocation(shadowProgram, 14, "tangent");
        
        ARBShaderObjects.glLinkProgramARB(shadowProgram);
        if (ARBShaderObjects.glGetObjectParameteriARB(shadowProgram, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            Log.log("Failed to initialize shadowmapping vert shader~", "Graphics", 3);
            return;
        }
        
        ARBShaderObjects.glValidateProgramARB(shadowProgram);
        if (ARBShaderObjects.glGetObjectParameteriARB(shadowProgram, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
            Log.log("Failed to initialize shadowmapping frag shader~", "Graphics", 3);
        	return;
        }
		
		//Post processing shader
		try {
			postVert = loadShader(postShaderSource + ".vert", ARBVertexShader.GL_VERTEX_SHADER_ARB);
		} catch (Exception e) {
            Log.log("Failed to initialize postprocessing vert shader~", "Graphics", 3);
			e.printStackTrace();
		}
		try {
			postFrag = loadShader(postShaderSource + ".frag", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		} catch (Exception e) {
            Log.log("Failed to initialize postprocessing frag shader~", "Graphics", 3);
			e.printStackTrace();
		}
		
		postProgram = ARBShaderObjects.glCreateProgramObjectARB();
        ARBShaderObjects.glAttachObjectARB(postProgram, postVert);
        ARBShaderObjects.glAttachObjectARB(postProgram, postFrag);
        
        ARBShaderObjects.glLinkProgramARB(postProgram);
        if (ARBShaderObjects.glGetObjectParameteriARB(postProgram, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            Log.log("Failed to initialize postprocessing shader~", "Graphics", 3);
            return;
        }
        
        ARBShaderObjects.glValidateProgramARB(postProgram);
        if (ARBShaderObjects.glGetObjectParameteriARB(postProgram, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
            Log.log("Failed to initialize postprocessing shader~", "Graphics", 3);
        	return;
        }
        
		Log.log("Graphics started!", "Graphics");
	}
	
	public int compileShaderProgram(String vert, String frag) throws Exception {
		int i = -1, j = -1, k = -1;
		try {
			i = createShader(vert, ARBVertexShader.GL_VERTEX_SHADER_ARB);
		} catch (Exception e) {
            Log.log("Failed to compile vertex shader~", "Graphics", 3);
			Log.log(vert, "Graphics", 3);
			//e.printStackTrace();
		}
		try {
			j = createShader(frag, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		} catch (Exception e) {
            Log.log("Failed to compile frag shader~", "Graphics", 3);
			Log.log(frag, "Graphics", 3);
			//e.printStackTrace();
		}
		
		k = ARBShaderObjects.glCreateProgramObjectARB();
        ARBShaderObjects.glAttachObjectARB(k, i);
        ARBShaderObjects.glAttachObjectARB(k, j);
        
        //hardcoding is bad... welp \(._.)/
		GL20.glBindAttribLocation(k, 12, "indices");
		GL20.glBindAttribLocation(k, 13, "weights");
		GL20.glBindAttribLocation(k, 14, "tangent");
        
        ARBShaderObjects.glLinkProgramARB(k);
        if (ARBShaderObjects.glGetObjectParameteriARB(k, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            Log.log("Failed to link glsl shader~", "Graphics", 2);
            Log.log(GL20.glGetProgramInfoLog(k, 9999999), "Graphics", 2);
            return -1;
        }
        
        ARBShaderObjects.glValidateProgramARB(k);
        if (ARBShaderObjects.glGetObjectParameteriARB(k, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
        	Log.log("Failed to validate glsl shader~", "Graphics", 2);
            Log.log(GL20.glGetProgramInfoLog(k, 9999999), "Graphics", 2);
        	//return -1;
        }
        
        verts.add(i);
        frags.add(j);
        programs.add(k);
        
		return k;
	}
	
	//Loads a shader from file and compiles
    private int loadShader(String filename, int shaderType) throws Exception {
    	int shader = 0;
    	try {
	        shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
	        
	        if(shader == 0)
	        	return 0;
	        
	        ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(filename));
	        ARBShaderObjects.glCompileShaderARB(shader);
	        Log.log(GL20.glGetShaderInfoLog(shader, 9999999), "Graphics", 3);
	        if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
	            throw new RuntimeException("Error creating shader");
	        
	        return shader;
    	}
    	catch(Exception exc) {
    		ARBShaderObjects.glDeleteObjectARB(shader);
    		throw exc;
    	}
    }
    
    //Takes a shader file as a string and compiles it
    private int createShader(String shaderString, int shaderType) throws Exception {
    	int shader = 0;
    	try {
	        shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
	        
	        if(shader == 0)
	        	return 0;
	        
	        ARBShaderObjects.glShaderSourceARB(shader, shaderString);
	        ARBShaderObjects.glCompileShaderARB(shader);
	        if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
	            throw new RuntimeException("Error creating shader");
	        
	        return shader;
    	}
    	catch(Exception exc) {
    		Log.log(GL20.glGetShaderInfoLog(shader, 9999999), "Graphics", 2);
    		ARBShaderObjects.glDeleteObjectARB(shader);
    		throw exc;
    	}
    }
    
    public static int createVBOID() {
    	  if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
    	    IntBuffer buffer = BufferUtils.createIntBuffer(1);
    	    ARBVertexBufferObject.glGenBuffersARB(buffer);
    	    return buffer.get(0);
    	  }
    	  return 0;
    	}
    
    public static void bufferData(int id, FloatBuffer buffer) {
    	  if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
    	    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
    	    ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
    	  }
    	}
    	public static void bufferElementData(int id, IntBuffer buffer) {
    	  if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
    	    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, id);
    	    ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
    	  }
    	}
    
	public static int[] generateShadowFBO(int size) {
		//int shadowMapWidth = (int)(Settings.displayWidth * shadowQuality); //Deprecated now using squarezzzzz
		//int shadowMapHeight = (int)(Settings.displayHeight * shadowQuality); //Deprecated now using squarezzzzz		
		int fbo;
		int depthTexture;
		int FBOstatus;
		
		// Try to use a texture depth component
		depthTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthTexture);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		// Remove artifact on the edges of the shadowmap
		glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		FloatBuffer colorBuffer = ByteBuffer.allocateDirect(4*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		colorBuffer.put(0);
		colorBuffer.put(0);
		colorBuffer.put(0);
		colorBuffer.put(0);
		colorBuffer.rewind();
		glTexParameter(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, colorBuffer);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY); 	
		
		// No need to force GL_DEPTH_COMPONENT24, drivers usually give you the max precision if available
		ByteBuffer depth = null; //ByteBuffer.allocateDirect(shadowMapWidth*shadowMapHeight*4).order(ByteOrder.nativeOrder()); // allocacate texture buffer
		glTexImage2D(GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, size, size, 0, GL_DEPTH_COMPONENT, GL_FLOAT, depth);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		// create a framebuffer object
		IntBuffer buffer = ByteBuffer.allocateDirect(1*4).order(ByteOrder.nativeOrder()).asIntBuffer(); // allocate a 1 int byte buffer
		org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT(buffer);
		fbo = buffer.get();
		org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT(org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fbo);
		
		// Instruct openGL that we won't bind a color texture with the currently bound FBO
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		glEnable(GL_CULL_FACE);
		 
		// attach the texture to FBO depth attachment point
		org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT(org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT, org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, GL_TEXTURE_2D, depthTexture, 0);
		// check FBO status
		FBOstatus = org.lwjgl.opengl.EXTFramebufferObject.glCheckFramebufferStatusEXT(org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
		if(FBOstatus != org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT)
			Log.log("GL_FRAMEBUFFER_COMPLETE_EXT failed, CANNOT use FBO\n", "Graphics");
		
		// switch back to window-system-provided framebuffer
		org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT(org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
		return new int[]{fbo, depthTexture};
	}
	
	public static int[] generateRenderFBO(int width, int height) {		
		int fbo = glGenFramebuffersEXT();
		int colorTexture = glGenTextures();
		int depthBuffer = glGenRenderbuffersEXT();
		
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo);
		 
		glBindTexture(GL_TEXTURE_2D, colorTexture);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); 
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0,GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, colorTexture, 0);
		
		glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthBuffer);
		glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, width, height);
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthBuffer);
		 
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		return new int[]{fbo, colorTexture, depthBuffer};
	}
    	
	/** Destroys the engine Display. */
	public void endGraphics() {
		Log.log("Removing some graphics files from memory...", "Graphics");
		Log.log("Stopping Display...", "Graphics");
		Display.destroy();
		Log.log("Graphics stopped.", "Graphics");
	}
	
	//TODO: NO NO NO GO AWAY
	public static Vertex debug0 = new Vertex(0,0,0);
	public static Vertex debug1 = new Vertex(0,0,1);
	
	/** Draws all of the graphics as specified by the settings and current context. */
	public void renderGraphics(float f) {		
		//Start out by grabbing all the geometry we are going to need.
		Sky sky = engine.game.getSky();
		ArrayList<TrianglePacket> meshes = new ArrayList<TrianglePacket>();
		ArrayList<TrianglePacket> culledMeshes = new ArrayList<TrianglePacket>();
		ArrayList<TrianglePacket> transparents = new ArrayList<TrianglePacket>();
		ArrayList<TrianglePacket> skyMeshes = new ArrayList<TrianglePacket>();
		ArrayList<TrianglePacket> fp = new ArrayList<TrianglePacket>();
		engine.game.drawGame(meshes, f);
		engine.game.bsp.drawSky(skyMeshes);
		engine.game.player.drawFP(fp, f);
		
		int i = 0;
		while(i < meshes.size()) {
			if(!meshes.get(i).shader.transparent)
				culledMeshes.add(meshes.get(i));
			else
				transparents.add(meshes.get(i));
			i++;
		}
		
		//TODO: Possibly faster if I cull the geometry in the actors draw function, but I'm doing it here so I can keep all the geometry for shadow calculations and cull the rest for rendering
		//Cull any off screen geometry.
		Frustrum viewFrustrum = new Frustrum(fov, aspectRatio, nearClip, farClip, MathUtil.inverse(tran), MathUtil.inverse(look), rot, up);
		//cullGeometry(culledMeshes, viewFrustrum);
		//cullGeometry(transparents, viewFrustrum); //TODO: THIS IS BROKEN!
		
		//Now sort the triangle packets so that all packets with the same glsl shader are together (IE put glsl shader in > order)
		sortGeometry(culledMeshes);
		sortDepthGeometry(transparents);
		//sortGeometry(sky); //TODO: not sorting sky geometry, pointless. We render skies in a specific order to avoid using the depthbuffer. Can't sort because it breaks shit.

		//Shadow frustrums for cascading shadowmaps
		Frustrum shadowNear = new Frustrum(fov, aspectRatio, nearClip, sky.sun.nearShadow + sky.sun.overlapShadow, tran, look, rot, up);
		Frustrum shadowFar = new Frustrum(fov, aspectRatio, sky.sun.nearShadow - sky.sun.overlapShadow, sky.sun.farShadow, tran, look, rot, up);
	
		/**Do lighting**/
		float dsm[] = drawShadowMap(nearShadowFBO, nearShadowSize, shadowNear, meshes, 7, f);
		moireNear = dsm[0];
		radiusNear = dsm[1]-sky.sun.overlapShadow;
		dsm = drawShadowMap(farShadowFBO, farShadowSize, shadowFar, meshes, 8, f);
		moireFar = dsm[0];
		radiusFar = dsm[1]-sky.sun.overlapShadow;
		//Shadow generation done!
		//Collect all lights in the world
		glowLights = new ArrayList<GlowLight>();
		lineLights = new ArrayList<LineLight>();
		engine.game.getLights(glowLights, lineLights);
		//Cull lights that can't be seen
		cullGlowLights(glowLights, viewFrustrum);
		cullLineLights(lineLights, viewFrustrum);
		//Lights collected!
		
		/**Switch to the world FBO**/
		glBindTexture(GL_TEXTURE_2D, 0); 
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, worldFBO);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport (0, 0, displayWidth, displayHeight);
		
		/**Render the skybox in the background**/
		//Disable depth so that the sky renders materials in the order that they are in the obj. IE first material on bottom, last material on top.
		
		setSkyProjection();
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_TEXTURE_2D);
		i = 0;
		while(i < skyMeshes.size()) {
			if(activeProgram != skyMeshes.get(i).shader.glslProgram)
				activeProgram(skyMeshes.get(i).shader.glslProgram, f);
			skyMeshes.get(i).draw(activeProgram);
			i++;
		}
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_DEPTH_TEST);
		
		glCullFace(GL_BACK);
		glDepthFunc(GL_LEQUAL);
		glEnable(GL_CULL_FACE);
		
		//Bind shadowMaps...
		glActiveTexture(GL_TEXTURE7);
		glBindTexture(GL_TEXTURE_2D, nearDepthTexture);
		glBindSampler(nearDepthTexture, 7);
		
		glActiveTexture(GL_TEXTURE8);
		glBindTexture(GL_TEXTURE_2D, farDepthTexture);
		glBindSampler(farDepthTexture, 8);
		
		/**Render the world**/
		setWorldProjection();
		
		activeProgram(0, f); //TODO: DEBUG LINE DRAG GO AWAE PLS STAHP
		glBegin(GL_LINES);
		glColor4f(1f,0f,0f,1f);
		glVertex3f(debug0.x, debug0.y, debug0.z);
		glVertex3f(debug1.x, debug1.y, debug1.z);
		glEnd();
		
		glEnable(GL_TEXTURE_2D);
		i = 0;
		while(i < culledMeshes.size()) {
			if(activeProgram != culledMeshes.get(i).shader.glslProgram) {
				activeProgram(culledMeshes.get(i).shader.glslProgram, f);
			}
			culledMeshes.get(i).draw(activeProgram);
			i++;
		}
		i = 0;
		glDepthMask(false);
		while(i < transparents.size()) {
			if(activeProgram != transparents.get(i).shader.glslProgram) {
				activeProgram(transparents.get(i).shader.glslProgram, f);	
			}
			transparents.get(i).draw(activeProgram);
			i++;
		}
		glDepthMask(true);
		glDisable(GL_TEXTURE_2D);
		
		/**Switch to the first person FBO**/
		glBindTexture(GL_TEXTURE_2D, 0); 
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fpFBO);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport (0, 0, displayWidth, displayHeight);
		
		/**Render first person view**/
		setFirstPersonProjection();
		activeProgram(0, f);
		
		//TODO: [after we fix shadows and frustrums]edit fp rendering so it is consistent with the world space so shadows are cast on FP models correctly.
		glEnable(GL_TEXTURE_2D);
		i = 0;
		while(i < fp.size()) {
			if(activeProgram != fp.get(i).shader.glslProgram) {
				activeProgram(fp.get(i).shader.glslProgram, f);
			}
			fp.get(i).draw(activeProgram);
			i++;
		}
		glDisable(GL_TEXTURE_2D);
		
		/**Switch to the ui FBO**/
		glBindTexture(GL_TEXTURE_2D, 0); 
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, uiFBO);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport (0, 0, displayWidth, displayHeight);
		
		/**Render UI**/
		engine.ui.draw(f, displayWidth, displayHeight);
		
		/**Switch back to fixed pipeline**/
		glFlush ();
		glLoadIdentity();
		activeProgram(0, f);
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		
		/**Render to window w/ post processing**/
		activeProgram(postProgram, f);
		glUniform1f(glGetUniformLocation(activeProgram, "pixelX"), 1.0f/displayWidth);
		glUniform1f(glGetUniformLocation(activeProgram, "pixelY"), 1.0f/displayHeight);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0,displayWidth,0,displayHeight,1,20);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glColor4f(1,1,1,1);
		glDisable(GL_LIGHTING);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, worldTexture);
		org.lwjgl.opengl.ARBShaderObjects.glUniform1iARB(glGetUniformLocation(activeProgram, "world"), 0);
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, fpTexture);
		org.lwjgl.opengl.ARBShaderObjects.glUniform1iARB(glGetUniformLocation(activeProgram, "fp"), 1);
		glActiveTexture(GL_TEXTURE2);
		glBindTexture(GL_TEXTURE_2D, uiTexture);
		org.lwjgl.opengl.ARBShaderObjects.glUniform1iARB(glGetUniformLocation(activeProgram, "ui"), 2);
		glEnable(GL_TEXTURE_2D);
		glTranslated(0,0,-1);
		glBegin(GL_QUADS);
		glTexCoord2d(0,0);glVertex3f(0,0,0);
		glTexCoord2d(1,0);glVertex3f(displayWidth,0,0);
		glTexCoord2d(1,1);glVertex3f(displayWidth,displayHeight,0);
		glTexCoord2d(0,1);glVertex3f(0,displayHeight,0);
		glEnd();
		
		glDisable(GL_TEXTURE_2D);
		 
		/*setupMatrices((float)tran.x,(float)tran.y,(float)tran.z,(float)rot.x,(float)rot.y,(float)rot.z);
			glDisable(GL_LIGHTING);
			glDisable(GL_TEXTURE_2D);
			glBegin(GL_LINES);
			glColor3d(1.0, 0.0, 0.0);          
			glVertex3d(lightVecb.x, lightVecb.y, lightVecb.z);  
			glColor3d(0.0, 1.0, 0.0); 
			glVertex3d(lightVeca.x, lightVeca.y, lightVeca.z);  
			glEnd();
			glEnable(GL_LIGHTING);
			glEnable(GL_TEXTURE_2D);*/
		

		/*
		//HUD currently just drawing a crosshair, create actual hud system
		glLoadIdentity();
		glRotated(0,1.0,0.0,0.0);
		glRotated(0,0.0,1.0,0.0);
		glRotated(0,0.0,0.0,1.0);
		glTranslated(0, 0, -0.1);
		
		glLineWidth(0.1f);
		glEnable(GL_LIGHTING);
		glEnable(GL_TEXTURE_2D);
		engine.game.player.drawHud();
		glDisable(GL_LIGHTING);
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_LINES);
		glColor3d(1.0, 1.0, 1.0);          
		glVertex3d(-0.001, -0.001, 0.0);
		glVertex3d(0.001, 0.001, 0.0);  
		glVertex3d(-0.001, 0.001, 0.0);
		glVertex3d(0.001, -0.001, 0.0);
		glEnd();
		glEnable(GL_LIGHTING);
		glEnable(GL_TEXTURE_2D);*/
		
		/*setWorldProjection();
		viewFrustrum = new Frustrum(45, 1, 32, 128, new Vertex(0,250,0), new Vertex(1,0,0), new Vertex(0,0,0), up);
		viewFrustrum.debugDraw();
		viewFrustrum.normalizedExtrusion(10).debugDraw();*/
		
		//viewFrustrum.debugDraw();
		//viewFrustrum.normalizedExtrusion(10).debugDraw();
		
		//setShadowProjection(d[0], d[1], d[2], d[3], d[4], d[5], d[6]);
		//viewFrustrum = new Frustrum(fov, aspectRatio, 256, 512, MathUtil.inverse(tran), look, rot, up);
		//viewFrustrum.debugDraw();
		
		/*setWorldProjection();
		test = new Frustrum(45, aspectRatio, 32, 128, new Vertex(100,0,0), new Vertex(1,0,0), new Vertex(0,0,0), up);
		test.debugDraw();
		test.normalizedExtrusion(128).debugDraw();*/
		
	    Display.update();
	    if (Display.isCloseRequested())
	    	engine.stopRuntime = true;
	}
	
	//TODO: THAHHAHA AHAHF AHH IS THAT A FUCKING BUBBLE SORT OMFG LOL KILL MYSELF LATER KTHX
	//TODO: For the sake of my sanity, please replace this with a better sorting algorithim later.
	public void sortGeometry(ArrayList<TrianglePacket> geo) {
		int i = -1;
		boolean done = false;
		if(geo.size() < 2)
			return;
		while(!done) {
			if(i < geo.size()-2)
				i++;
			else {
				done = true;
				i = 0;
			}
			
			if(geo.get(i).shader.glslProgram > geo.get(i+1).shader.glslProgram) {
				TrianglePacket t = geo.get(i);
				geo.set(i, geo.get(i+1));
				geo.set(i+1, t);
				done = false;
			}
		}
	}
	
	//HERE TOO LOL
	public void sortDepthGeometry(ArrayList<TrianglePacket> geo) {
		int i = -1;
		boolean done = false;
		if(geo.size() < 2)
			return;
		//System.out.println("sort depth on this frame:");
		while(!done) {
			done = true;
			i = 0;
			//System.out.println("pass: " + i);
			while(i < geo.size() - 1) {
				//System.out.print(" || " + MathUtil.length(geo.get(i).location, engine.game.player.location) + "<?" + MathUtil.length(geo.get(i+1).location, engine.game.player.location));
				if(MathUtil.length(geo.get(i).location, engine.game.player.location) < MathUtil.length(geo.get(i+1).location, engine.game.player.location)) { //TODO: Slow, repeated length calculations. Pre calc that shit
					TrianglePacket t = geo.get(i);
					geo.set(i, geo.get(i+1));
					geo.set(i+1, t);
					done = false;
				}
				i++;
			}
		}
	}
	
	public void cullGeometry(ArrayList<TrianglePacket> geo, Frustrum frus) {
		int i = 0;
		while(i < geo.size()) {
			if(!frus.contains(geo.get(i).location, geo.get(i).radius)) {
				geo.remove(i);
				i--;
			}
			i++;
		}		
	}
	
	public void cullGlowLights(ArrayList<GlowLight> lights, Frustrum frus) {
		int i = 0;
		while(i < lights.size()) {
			if(!frus.contains(lights.get(i).position, lights.get(i).radius)) {
				lights.remove(i);
				i--;
			}
			i++;
		}
	}
	
	public void cullLineLights(ArrayList<LineLight> lights, Frustrum frus) {
		int i = 0;
		while(i < lights.size()) {
			if(!frus.contains(lights.get(i).position, lights.get(i).cullRadius)) {
				lights.remove(i);
				i--;
			}
			i++;
		}
	}
	
	//TODO: Kind of a mess :/
	//TODO: Also, just save uniform locations in a class somewhere plz kthx.
	public int activeProgram(int i, float f) {
		ARBShaderObjects.glUseProgramObjectARB(i);
		activeProgram = i;
		if(i != 0 && i != shadowProgram && i != postProgram) {
			//Set base scale value
			org.lwjgl.opengl.ARBShaderObjects.glUniform1fARB(glGetUniformLocation(activeProgram, "modelScale"), 1.0f);
			
			//Send glsl all the shadow and lighting uniforms it needs
			org.lwjgl.opengl.ARBShaderObjects.glUniform1iARB(glGetUniformLocation(activeProgram, "nearShadowMap"), 7);
			org.lwjgl.opengl.ARBShaderObjects.glUniform1iARB(glGetUniformLocation(activeProgram, "farShadowMap"), 8);
			
			//Get sky values...
			Sky sky = engine.game.getSky();
			glUniform1f(glGetUniformLocation(activeProgram, "fogStart"), sky.getFogStart());
			glUniform1f(glGetUniformLocation(activeProgram, "fogEnd"), sky.getFogEnd());
			glUniform1f(glGetUniformLocation(activeProgram, "fogDensity"), sky.getFogDensity());
			Vertex color = sky.getFogColor();
			glUniform4f(glGetUniformLocation(activeProgram, "fogColor"), color.x, color.y, color.z, 1);
			
			//Get sun values...
			Vertex norm = sky.getSunNormal();
			Vertex loc = sky.getSunLocation();
			Vertex lightColor = sky.getSunLightColor();
			Vertex shadowColor = sky.getSunShadowColor();
			float min = sky.sun.minIntensity;
			float max = sky.sun.maxIntensity;
			
			//Get camera values
			Vertex lok = MathUtil.normalize(engine.game.player.lookRad);
			Vertex pos = tran;
			
			glUniform3f(glGetUniformLocation(activeProgram, "sunDir"), norm.x, norm.y, norm.z);
			glUniform4f(glGetUniformLocation(activeProgram, "sunLocation"), -loc.x, -loc.y, -loc.z, 1.0f);
			glUniform4f(glGetUniformLocation(activeProgram, "sunColor"), lightColor.x, lightColor.y, lightColor.z, 1.0f);
			glUniform4f(glGetUniformLocation(activeProgram, "sunShadowColor"), shadowColor.x, shadowColor.y, shadowColor.z, 1.0f);
			glUniform1f(glGetUniformLocation(activeProgram, "lightMaxIntensity"), max);
			glUniform1f(glGetUniformLocation(activeProgram, "lightMinIntensity"), min);
			glUniform1f(glGetUniformLocation(activeProgram, "nearPixelOffset"), 1.0f / nearShadowSize);
			glUniform1f(glGetUniformLocation(activeProgram, "farPixelOffset"), 1.0f / farShadowSize);
			glUniform1f(glGetUniformLocation(activeProgram, "moireNearQuality"), moireNear);
			glUniform1f(glGetUniformLocation(activeProgram, "moireFarQuality"), moireFar);
			glUniform1f(glGetUniformLocation(activeProgram, "pcfNear"), pcfNear);
			glUniform1f(glGetUniformLocation(activeProgram, "pcfFar"), pcfFar);
			glUniform1f(glGetUniformLocation(activeProgram, "radNear"), radiusNear);
			glUniform1f(glGetUniformLocation(activeProgram, "radOverlap"), sky.sun.overlapShadow); //TODO: this isn't in the "same format" as the other 2
			glUniform1f(glGetUniformLocation(activeProgram, "radFar"), radiusFar);
			
			glUniform3f(glGetUniformLocation(activeProgram, "specEye"), lok.x, lok.y, lok.z);
			glUniform3f(glGetUniformLocation(activeProgram, "specLight"), loc.x, loc.y, loc.z);
			glUniform3f(glGetUniformLocation(activeProgram, "specPosition"), -pos.x, -pos.y, -pos.z);
			
			
			//Upload lights to glsl
			int j = 0;
			while(j < glowLights.size()) {
				glowLights.get(j).applyToShader(j);
				j++;
			}
			glUniform1i(glGetUniformLocation(activeProgram, "glowLights"), glowLights.size());
			
			j = 0;
			while(j < lineLights.size()) {
				lineLights.get(j).applyToShader(j);
				j++;
			}
			glUniform1i(glGetUniformLocation(activeProgram, "lineLights"), lineLights.size());
		}
		return i;
	}
	
	//Uh... well im lazy so...
	//TODO: explain this later it's kind of fucking complicated. 
	//just remember tex 0 - 6 is for shaders, 7, 8, 9 is for shadows, 10, 11, 12 is for ui 'n shit
	//Also max tex matrices is 7 so we are going to lower a few to get texmatrices on 5 6 7
	public float[] drawShadowMap(int fbo, int shadowSize, Frustrum view, ArrayList<TrianglePacket> meshes, int textureMatrix, float f) {
		Sky sky = engine.game.getSky();
		float[] d = view.createLightFrustrum(sky.getSunLocation());
		
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fbo);	//Rendering offscreen to depth fbo
		
		//Switch to the shadowMapping glsl shader...
		activeProgram(shadowProgram, f);
		
		//Adjust viewport to shadowMapSize	(bigger is better ~)	
		glViewport(0,0,shadowSize,shadowSize);
		
		// Clear previous frame values
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		//Disable color rendering, we only want to write to the Z-Buffer
		glColorMask(false, false, false, false); 
		
		glCullFace(GL_NONE);
		glDepthFunc(GL_LESS);
		glDisable(GL_CULL_FACE);
		
		setShadowProjection(d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7]);
		
		/**Render shadow for shadow mapping**/
		int i = 0;
		while(i < meshes.size()) {
			if(meshes.get(i).shader.castShadows)
				meshes.get(i).draw(activeProgram);
			i++;
		}
		//glPopMatrix();
		
		//Save modelview/projection matrices into texture 7 and 8, also add a biais
		setTextureMatrix(textureMatrix);
		
		// Now rendering from the camera POV, using the FBO to generate shadows
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		
		glViewport(0,0,displayWidth,displayHeight);
		
		//Enabling color write (previously disabled for light POV z-buffer rendering)s
		glColorMask(true, true, true, true); 
		
		// Clear previous frame values
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		//System.out.println(d[7] + " " + d[7]*0.000001);
		//TODO: the first calculation is for moire offset, it's wrong fix it you cunt
		//TODO: the second calculation is for the radius of the shadow done perpixel, also wrong you casual
		return new float[] { (d[7]*0.000001f), (d[0]/1.34f)};
	}
	
	public void setTextureMatrix(int textureMatrix)
	{
		//float modelView[]  = new float[16];
		//float projection[]  = new float[16];
		
		// Moving from unit cube [-1,1] to [0,1]  
		double bias[] = {0.5, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.5, 0.5, 0.5, 1.0};
		
		// Grab modelview and transformation matrices
		DoubleBuffer bia = ByteBuffer.allocateDirect(16*8).order(ByteOrder.nativeOrder()).asDoubleBuffer(); // allocacate modview buffer
		DoubleBuffer mod = ByteBuffer.allocateDirect(16*8).order(ByteOrder.nativeOrder()).asDoubleBuffer(); // allocacate modview buffer
		DoubleBuffer proj = ByteBuffer.allocateDirect(16*8).order(ByteOrder.nativeOrder()).asDoubleBuffer(); // allocacate modview buffer
		int i = 0;
		while(i < bias.length) {
			bia.put(bias[i]);
			i++;
		}
		bia.rewind();
		
		glGetDouble(GL_MODELVIEW_MATRIX, mod);
		glGetDouble(GL_PROJECTION_MATRIX, proj);
		
		glMatrixMode(GL_TEXTURE);
		glActiveTexture(GL_TEXTURE0 + textureMatrix - 1);
		
		glLoadIdentity();	
		glLoadMatrix(bia);
		
		// concatating all matrices into one.
		glMultMatrix(proj);
		glMultMatrix(mod);
		
		// Go back to normal matrix mode
		glMatrixMode(GL_MODELVIEW);
	}
	
	public void setShadowProjection(float a, float b, float c, float d, float e, float f, float g, float h) {
		Sky sky = engine.game.getSky();
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		//GLU.gluPerspective((float)sun.fov, (float)displayWidth/displayHeight, (float)sun.nearClip, (float)sun.farClip);// deprecated, using ortho for light		
		//glOrtho(sun.left, sun.right, sun.bottom, sun.top, sun.nearClip, sun.farClip); //deprecated, now fitting shadows to view frustrum
		glOrtho(a, b, c, d, 32, h);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glTranslated(0, 0, -sky.sun.radius);
		glRotated(sky.getSunRotation().x,1.0,0.0,0.0);
		glRotated(sky.getSunRotation().z,0.0,0.0,1.0);
		glTranslated(e, f, g);
	}
	
	public void setSkyProjection() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective (fov, (float)displayWidth/displayHeight, 0f, 65536.0f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glTranslated(0, 0, 0);
		glRotated(rot.x,1.0,0.0,0.0);
		glRotated(rot.y,0.0,1.0,0.0);
		glRotated(rot.z,0.0,0.0,1.0);
	}
	
	public void setWorldProjection() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective (fov, (float)displayWidth/displayHeight, 1f, 8192.0f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glTranslated(0, 0, 0);
		glRotated(rot.x,1.0,0.0,0.0);
		glRotated(rot.y,0.0,1.0,0.0);
		glRotated(rot.z,0.0,0.0,1.0);
		glTranslated(tran.x, tran.y, tran.z);
	}
	
	public void setFirstPersonProjection() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		GLU.gluPerspective (fov, (float)displayWidth/displayHeight, 0.01f, 128.0f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}
	
	public void cameraVector(float xx, float yy) {
		rot.x -= yy;
		rot.y = 0;
		rot.z -= xx;
		
		if(rot.x <= -180)
			rot.x = -180;
		if(rot.x >= 0)
			rot.x = 0;
		
		if(rot.z <= -360)
			rot.z += 360;
		if(rot.z >= 360)
			rot.z -= 360;
	}
	
	public int zoom = 1000;
	public void zoomHandle(int i) {
		zoom = zoom + i*50;
		if(zoom < 10)
			zoom = 10;
		if(zoom > 10000)
			zoom = 10000;
	}
	
	public void listenMouse() {
		
	}
	
    private String readFileAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();
        
        DataInputStream in = new DataInputStream(FileUtil.getFile(filename));
        
        Exception exception = null;
        
        BufferedReader reader;
        try{
            reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            
            Exception innerExc= null;
            try {
            	String line;
                while((line = reader.readLine()) != null)
                    source.append(line).append('\n');
            }
            catch(Exception exc) {
            	exception = exc;
            }
            finally {
            	try {
            		reader.close();
            	}
            	catch(Exception exc) {
            		if(innerExc == null)
            			innerExc = exc;
            		else
            			exc.printStackTrace();
            	}
            }
            
            if(innerExc != null)
            	throw innerExc;
        }
        catch(Exception exc) {
        	exception = exc;
        }
        finally {
        	try {
        		in.close();
        	}
        	catch(Exception exc) {
        		if(exception == null)
        			exception = exc;
        		else
					exc.printStackTrace();
        	}
        	
        	if(exception != null)
        		throw exception;
        }
        
        return source.toString();
    }
}