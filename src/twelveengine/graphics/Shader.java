package twelveengine.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL33.glBindSampler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import twelveengine.Game;
import twelveengine.Log;
import twelveutil.FileUtil;
//TODO:Optimize cubemap calls?
public class Shader {
	public Game game;
	
	public String file;
	public String name;

	public boolean mipMaps;
	public boolean castShadows;
	public boolean unlit;
	public boolean allowFog; //Default is true. If false then do not draw any fog on this shader.
	public int scalars; //If not zero, this shader has input values that it uses in drawing. Like for a health bar the scalar function would give the % of health to draw the bar correctly. //The value of the int corresponds to how many scalars we have.
	public boolean cubemaps; //Flagged true if this shader uses a cubemap.
	
	public float scalarValues[] = new float[27];
	public Texture texture[] = new Texture[7];
	public CubeMap cube[] = new CubeMap[2];
	public String channels[] = new String[5];
	
	public float color[];
	
	public int glslProgram;
	public String glslSource;
	
	public float uvAnimation[];
	public float rotAnimation;
	
	public boolean transparent;
	
	public Shader(Game w, String s) {
		game = w;
		file = s;
		try {
			getMaterial(s);
			int i = game.getGLSLShader(channels, unlit, castShadows, mipMaps);
			if(i > 0)
				glslProgram = i;
			else
				generateGLSL();
			Log.log("Shader built: " + file, "Shader");
		}
		catch(Exception e) {
			try {
				getMaterial("multipurpose/shader/default.shader");
				int i = game.getGLSLShader(channels, unlit, castShadows, mipMaps);
				if(i > 0)
					glslProgram = i;
				else
					generateGLSL();
				Log.log("Can't find shader ~" + s + " : using ~default.shader instead...", "Shader", 1);
			} catch (Exception e1) {
				Log.log("Can't find: ~multipurpose/shader/default.shader, This program will now panic and die", "Shader", 3);
				e1.printStackTrace();
			}
		}
		transparent = channels[4] != null;
	}
	
	public void getMaterial(String s) throws Exception {
	    mipMaps = true;
	    castShadows = true;
	    unlit = false;
	    scalars = 0;
	    allowFog = true;
	    cubemaps = false;
    
		String currentLine;
		DataInputStream fileIn = new DataInputStream(FileUtil.getFile(s));
	    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileIn));

	    currentLine=fileReader.readLine();
	    while(currentLine != null) {
	    	//Vars
	    	if(currentLine.startsWith("name=")) {
	    		name=currentLine.split("=")[1];
	    	}
	    	if(currentLine.startsWith("unlit=")) {
	    		unlit=Boolean.parseBoolean(currentLine.split("=")[1]);
	    	}
	    	if(currentLine.startsWith("castshadows=")) {
	    		castShadows=Boolean.parseBoolean(currentLine.split("=")[1]);
	    	}
	    	if(currentLine.startsWith("mipmaps=")) {
	    		mipMaps=Boolean.parseBoolean(currentLine.split("=")[1]);
	    	}
	    	if(currentLine.startsWith("fog=")) {
	    		allowFog=Boolean.parseBoolean(currentLine.split("=")[1]);
	    	}
	    	//Gets textures
	    	if(currentLine.startsWith("texture<")) {
	    		int t = Integer.parseInt(currentLine.split("<")[1].split(">")[0]);
	    		String p = currentLine.split("=")[1];
	    		texture[t]=getTexture(p);
	    	}
	    	//Gets cubemaps
	    	if(currentLine.startsWith("cubemap<")) {
	    		int t = Integer.parseInt(currentLine.split("<")[1].split(">")[0]);
	    		String p = currentLine.split("=")[1];
	    		cube[t]=getCubeMap(p);
	    		cubemaps = true;
	    	}
	    	//Gets sources
	    	if(currentLine.startsWith("diffuse=")) {
	    		channels[0]=currentLine.split("=")[1];
	    	}
	    	if(currentLine.startsWith("normal=")) {
	    		channels[1]=currentLine.split("=")[1];
	    	}
	    	if(currentLine.startsWith("specular=")) {
	    		channels[2]=currentLine.split("=")[1];
	    	}
	    	if(currentLine.startsWith("illumination=")) {
	    		channels[3]=currentLine.split("=")[1];
	    	}
	    	if(currentLine.startsWith("transparency=")) {
	    		channels[4]=currentLine.split("=")[1];
	    	}
		    currentLine=fileReader.readLine();
	    }
	    fileReader.close();
	    int i = 0;
	    while(i < channels.length) {
	    	if(channels[i] != null)
	    		if(channels[i].contains("scalar")) {
	    			int j = 1;
	    			String c[] = channels[i].split("scalar");
	    			while(j < c.length) {
	    				int k = alpha2Int(Character.toLowerCase(c[j].charAt(0)));
	    				if(k > scalars)
	    					scalars = k;
	    				j++;
	    			}
	    		}
	    	i++;
	    }
	}
	
	//TODO: texture file structure next :D
	public Texture getTexture(String s) {
		try {
			return game.getTexture(s, mipMaps);
		} catch (Exception e) {
			try {
				return game.getTexture("multipurpose/texture/default.png", mipMaps);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	//TODO: cube file structure next :D
	public CubeMap getCubeMap(String s) {
		try {
			return game.getCubeMap(s, mipMaps);
		} catch (Exception e) {
			try {
				return game.getCubeMap("multipurpose/texture/default.cubemap", mipMaps);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	public int getChannel(String s) {	
			if(s.equals("rgb") || s.equals("color"))
				return 0;
			if(s.equals("r") || s.equals("red"))
				return 1;
			if(s.equals("g") || s.equals("green"))
				return 2;
			if(s.equals("b") || s.equals("blue"))
				return 3;
			if(s.equals("a") || s.equals("alpha"))
				return 4;
			return 0;
	}
	
	//TODO: OH DEAR LORD THIS IS THE BIGGEST TODO OF ALL. OPTIMIZE ALL THIS GLSL CODE. ALL OF IT. ALL OF IT. ALL OF IT.
	//TODO: shadow mapping partially optimized, plenty more can be done though I bet
	/** GLSL FRAG SHADER GENERATION STATIC INFORMATION **/
	public String glslBaseVars[] = {"varying vec4 position;", "varying vec3 normal;"};
	public String glslFogVars[] = {"uniform float fogStart;", "uniform float fogEnd;", "uniform float fogDensity;", "uniform vec4 fogColor;"};
	public String glslNormalVars[] = {"uniform vec3 specEye;", "uniform vec3 specLight;", "uniform vec3 specPosition;", "varying vec3 lightVec;", "varying vec3 halfVec;", "varying vec3 eyeVec;"};
	public String glslLightingVars[] = {"uniform sampler2DShadow nearShadowMap;", "uniform sampler2DShadow farShadowMap;", "uniform vec3 sunDir;", "uniform vec4 sunColor;", "uniform vec4 sunShadowColor;", 
										"uniform int glowLights;", "uniform vec3 glowLoc[32];", "uniform vec4 glowColor[32];", "uniform float glowRadius[32];",
										"uniform int lineLights;", "uniform vec3 lineA[32];", "uniform vec3 lineB[32];", "uniform vec4 lineColor[32];",
										"uniform float lineRadius[32];", "varying vec4 nearShadowCoord;", "varying vec4 farShadowCoord;"};
	public String glslGetDepthFunction[] = {"float getDepth(sampler2DShadow shadowMap, float x, float y, vec4 coord, float offset) {", 
											"return shadow2DProj(shadowMap, coord + vec4(offset * x, offset * y, 0, 0)).w;",
											"}"};
	public String glslGetShadowFunction[] = {
			"float getShadow(vec4 shadowCoord, sampler2DShadow shadowMap, float moire, float a, float b, float pcf, float pixelOffset) {",
			"	float shadow = 0;",
			"	vec4 divide = shadowCoord;",
			"	divide.z -= moire;", 
			"	float x = -a;", 
			"	float y = -a;", 
			"	int i = 0;",
			"	while(x <= a) {", 
			"		while(y <= a) {", 
			"			shadow += getDepth(shadowMap, x*pcfNear, y*pcfNear, divide, pixelOffset);", 
			"			y += b;", 
			"			i++;",
			"		}", 
			"		x += b;", 
			"		y = -a;", 
			"	}", 
			"	", 
			"	return min(max(shadow/i, 0), 1);",
			"}"
	};
	public String glslPointLineDistFunction[] = {"vec3 pointLineDist(vec3 P, vec3 A, vec3 B) {",
												"vec3 v = B - A;",
												"vec3 w = P - A;",
												"float c1 = dot(w,v);",
												"if ( c1 <= 0 )",
												"return A;",
												"float c2 = dot(v,v);",
												"if ( c2 <= c1 )",
												"return B;",
												"float b = c1 / c2;",
												"vec3 Pb = A + b * v;",
												"return Pb;",
												"}"};
	public String glslLerpFunction[] = {"vec4 lerp(vec4 mask, vec4 texa, vec4 texb) {",
										"return vec4((texa * mask.a) + (texb * (1 - mask.a)));",
										"}"
	};
	
	public String glslDiffFunction[] = {"vec4 diff(vec4 texa, vec4 texb) {",
			"return vec4(1 - abs(texa.r - texb.r), 1 - abs(texa.g - texb.g), 1 - abs(texa.b - texb.b), 1 - abs(texa.a - texb.a));",
			"}"
	};
	
	public String glslPowFunction[] = {"vec4 pow(vec4 texa, float p) {",
			"return vec4(1 - abs(texa.r - texb.r), 1 - abs(texa.g - texb.g), 1 - abs(texa.b - texb.b), 1 - abs(texa.a - texb.a));",
			"}"
	};
	
	public String glslColorFunction[] = {"vec4 color(float r, float g, float b, float a) {",
			"return vec4(r,g,b,a);",
			"}"
	};
	
	public String glslCoordRotateFunction[] = {"vec2 coordRotate(vec2 coord, float rot) {",
			"float cosDegrees = cos(rot);",
			"float sinDegrees = sin(rot);",
			"float x = (coord.s * cosDegrees) + (coord.t * sinDegrees);",
			"float y = (coord.s * -sinDegrees) + (coord.t * cosDegrees);",
			"return vec2(x,y);",
			"}"
	};
	
	//TODO: the interpolation between shadow maps is hideously bad and wrong fix it.
	//TODO: fix the issue where height can cause blank space between near and far shadows
	public String glslLightingBlock[] = {
			"		vec3 L = normalize(sunDir);",
			"		float shaDist = length(vec3(0,0,0) - vec3(gl_ModelViewProjectionMatrix * position));",
			"		", 
			"		//Calculate shadows and sunlight", 
			"		float shadow = 0;", 
			"		", 
			"		float lightNormal = max(dot(N, L), 0.0);", 
			"		", 
			"		//If triangle is backfacing to the sun just skip shadow calculations and put it in shadow.", 
			"		if(lightNormal < 0.4) {",
			"		vec4 divide = nearShadowCoord;", 
			"		divide.z -= moireNearQuality;", 
			"			if(shaDist > radFar)",
			"				shadow = 1;",
			"			else if(shaDist >= radNear - radOverlap && shaDist <= radNear + radOverlap) {",
			"				float farShadow = getShadow(farShadowCoord, farShadowMap, moireFarQuality, 3, 1.5, pcfFar, farPixelOffset);",
			"				float nearShadow = getShadow(nearShadowCoord, nearShadowMap, moireNearQuality, 3, 1, pcfNear, nearPixelOffset);",
			"				float oratio = min(max(((radOverlap*2) / (shaDist - (radNear - radOverlap)))/4, 0), 1);",
			"      			shadow = (oratio*nearShadow) + ((1 - oratio)*farShadow);",
			"      		}",
			"			else if(shaDist < radNear)",
			"				shadow = getShadow(nearShadowCoord, nearShadowMap, moireNearQuality, 3, 1, pcfNear, nearPixelOffset);",
			"			else {",
			"				shadow = getShadow(farShadowCoord, farShadowMap, moireFarQuality, 3, 2, pcfFar, farPixelOffset);",
			"			}",
			"		}",
			"		else {",
			"			shadow = min(shadow * (max(0.5 - lightNormal, 0) * 2), 1);",
			"		}",
			"		if(shadow <= 0.1) {",
			"			inShadow = true;",
			"			specularDirection = vec3(0,0,1);",
			"		}",
			"		vec4 lightColor = (sunColor * shadow) + (sunShadowColor * (1 - shadow));",
			"		", 
			"		light = lightColor;", 
			"		", 
			"		//Apply point glow lights", 
			"		int i = 0;", 
			"		vec3 pos = vec3(position.x, position.y, position.z);", 
			"		", 
			"		while(i < glowLights) {", 
			"			vec3 mag;", 
			"	  		mag.x = glowLoc[i].x - position.x;", 
			"	  		mag.y = glowLoc[i].y - position.y;", 
			"	  		mag.z = glowLoc[i].z - position.z;", 
			"	  		float dist = sqrt((mag.x*mag.x) + (mag.y*mag.y) + (mag.z*mag.z));", 
			"	  		", 
			"			vec3 np = normalize(pos - glowLoc[i]);", 
			"			float lN = max(dot(N, np), 0.0);", 
			"			if(lN < 0.35) {			", 
			"				if(dist < glowRadius[i]) {", 
			"					shadow = (glowRadius[i] - dist)/glowRadius[i];", 
			"					light += glowColor[i] * shadow;",
			"					if(inShadow)",
			"						specularDirection += (normalize(position - glowLoc[i]) * 1) * shadow;", 
			"				}", 
			"			}", 
			"			i++;", 
			"		}", 
			"		", 
			"		//Apply line glow lights", 
			"		i = 0;", 
			"		while(i < lineLights) {", 
			"			vec3 p = pointLineDist(pos, lineA[i], lineB[i]);", 
			"			", 
			"			vec3 np = normalize(pos - p);", 
			"			float lN = max(dot(N, np), 0.0);", 
			"			if(lN < 0.35) {			", 
			"				float dist = length(pos - p);", 
			"				if(dist < lineRadius[i]) {", 
			"					float normFade = 1; //to get rid of sharp edges on lights, fade the edges of normals", 
			"					if(lN > 0.20)", 
			"						normFade = (0.15 - (lN - 0.20))/0.15;", 
			"					shadow = ((lineRadius[i] - dist)/lineRadius[i]) * normFade;", 
			"					light += lineColor[i] * shadow;", 
			"				}", 
			"			}", 
			"			i++;", 
			"		}", 
			"		", 
			"		//Clean up final light value", 
			"		int x;", 
			"		for (x = 0; x < 3 ; x+=1) {", 
			"			if(light[x] > lightMaxIntensity)", 
			"				light[x] = lightMaxIntensity;", 
			"			if(light[x] < lightMinIntensity)", 
			"				light[x] = lightMinIntensity;", 
			"		}", 
			"		specularDirection = normalize(specularDirection);", 
			"		light.a = 1;"
			};
	public String glslNormalBlock[] = {
			"		vec3 tnormal = 2.0 * normalMaterial - 1.0;", 
			"		tnormal = normalize (tnormal);", 
			"		vec3 fnormal = normalize(vec3(0.0, 0.0, 1.0));", 
			"		", 
			"		// compute diffuse lighting", 
			"		float lamberFactor = max(dot(fnormal, tnormal), 0.0);", 
			"		lamberFactor = ((lamberFactor*lamberFactor) + lamberFactor)/2;", 
			"		", 
			"		if (lamberFactor > 0.0) {", 
			"			vec3 sN = normalize(normal + (tnormal - fnormal));",
			"			vec3 sL = normalize(specularDirection);",
			"			vec3 sH = reflect(-specEye,sN);",
			"			vec3 sE = normalize(position - specPosition);",
			"			shininess = pow(max(0.0, dot(sL, sH)), 2.0) * pow(max(dot(specEye, sE), 0), 2.0);",
			"			", 
			"			frag = diffuseMaterial * lamberFactor;", 
			"			frag +=	pow(specularLight, vec4(2.0)) * (specularMaterial * shininess);", 
			"		}"
			};
	
	//TODO: Mirror changes to shadowMapping vert shader.
	/** GLSL VERT SHADER GENERATION STATIC INFORMATION **/
	public String glslVertexVars[] = {
			"uniform bool bRotation;", 
			"uniform vec4 modelPosition;", 
			"uniform vec4 modelRotation;", 
			"uniform float modelScale;",
			"uniform bool bSkin;", 
			"uniform vec3 bonesOffset[64];", 
			"uniform vec3 bonesPos[64];", 
			"uniform vec4 bonesRot[64];", 
			"uniform vec4 sunLocation;", 
			"uniform vec3 sunDir;", 
			"",
			"varying vec3 halfVec;", 
			"varying vec3 eyeVec;", 
			"varying vec3 lightVec;", 
			"", 
			"varying vec4 position;", 
			"varying vec3 normal;", 
			"", 
			"attribute vec3 indices;", 
			"attribute vec3 weights;", 
			"attribute vec3 tangent;", 
			"", 
			"varying vec4 nearShadowCoord;",
			"varying vec4 farShadowCoord;", 
			" ", 
			"vec3 quatRotation(vec3 v, vec4 r) {",
			"			float q00 = 2.0 * r.x * r.x;", 
			"			float q11 = 2.0 * r.y * r.y;", 
			"			float q22 = 2.0 * r.z * r.z;", 
			"	", 
			"			float q01 = 2.0 * r.x * r.y;", 
			"			float q02 = 2.0 * r.x * r.z;", 
			"			float q03 = 2.0 * r.x * r.w;", 
			"	", 
			"			float q12 = 2.0 * r.y * r.z;", 
			"			float q13 = 2.0 * r.y * r.w;", 
			"	", 
			"			float q23 = 2.0 * r.z * r.w;", 
			"			vec3 f = vec3(0,0,0);",
			"			f.x = (1.0 - q11 - q22) * v.x + (q01 - q23) * v.y + (q02 + q13) * v.z;", 
			"			f.y = (q01 + q23) * v.x + (1.0 - q22 - q00) * v.y + (q12 - q03) * v.z;", 
			"			f.z = (q02 - q13) * v.x + (q12 + q03) * v.y + (1.0 - q11 - q00) * v.z;", 
			"			return f;",
			"}",
			"void main() {", 
			"	gl_TexCoord[0] = gl_MultiTexCoord0;", 
			"	vec4 pos = gl_Vertex;",
			"	normal = gl_Normal;",
			"	", 
			"	//Skin transformation and quaternion rotation", 
			"	if(bSkin) {", 
			"		int i = 0;", 
			"		", 
			"		//offset - transform for this frame", 
			"		float wx = 0;", 
			"		float wy = 0;", 
			"		float wz = 0;", 
			"		", 
			"		//position - normal position of bone", 
			"		float wa = 0;", 
			"		float wb = 0;", 
			"		float wc = 0;", 
			"		", 
			"		while(i < 3) {", 
			"			if(weights[i] != 0) {", 
			"				wx += (bonesOffset[int(indices[i])].x*weights[i]);", 
			"				wy += (bonesOffset[int(indices[i])].y*weights[i]);", 
			"				wz += (bonesOffset[int(indices[i])].z*weights[i]);", 
			"				wa += (bonesPos[int(indices[i])].x*weights[i]);", 
			"				wb += (bonesPos[int(indices[i])].y*weights[i]);", 
			"				wc += (bonesPos[int(indices[i])].z*weights[i]);", 
			"			}", 
			"			i++;", 
			"		}", 
			"		", 
			"		//Subtract location so we can rotate around origin 0,0,0", 
			"		pos.x -= wa;", 
			"		pos.y -= wb;", 
			"		pos.z -= wc;", 
			"		", 
			"		//number of actual weights (for performance, not done due to bug...)", 
			"				", 
			"		//do rotation", 
			"		i = 0;", 
			"		vec4 v = pos;", 
			"		vec4 rot[3];", 
			"		vec4 nor[3];", 
			"		", 
			"		while(i < 3) {", 
			"			vec4 r = bonesRot[int(indices[i])];", 
			"			rot[i] = vec4(quatRotation(vec3(v), r), 0);",
			"			nor[i] = vec4(quatRotation(normal, r), 0);",
			"			i++;", 
			"		}", 
			"		", 
			"		//Average the rotations by weight and apply them", 
			"		i=0;", 
			"		vec4 final = vec4(0,0,0,1);",
			"		normal = vec3(0,0,0);",
			"		while(i < 3) {", 
			"			final.x += (rot[i].x * weights[i]);", 
			"			final.y += (rot[i].y * weights[i]);", 
			"			final.z += (rot[i].z * weights[i]);", 
			"			normal.x += (nor[i].x * weights[i]);", 
			"			normal.y += (nor[i].y * weights[i]);", 
			"			normal.z += (nor[i].z * weights[i]);", 
			"			i++;", 
			"		}", 
			"		", 
			"		pos = final;", 
			"		", 
			"		//Finish up skinning by adding the location and offset", 
			"		pos.x += wa;", 
			"		pos.y += wb;", 
			"		pos.z += wc;", 
			"		", 
			"		pos.x += wx;", 
			"		pos.y += wy;", 
			"		pos.z += wz;		", 
			"	}", 
			"	", 
			"	//Euler rotation in the model", 
			"	if(bRotation) {", 
			"		pos = vec4(quatRotation(vec3(pos), modelRotation), 1);",
			"		normal = quatRotation(normal, modelRotation);",
			"	}", 
			"	//Scale of this model", 
			"	pos = pos * vec4(modelScale,modelScale,modelScale,1);",
			"	", 
			"	//Location offset in model", 
			"	pos += modelPosition;", 
			"	", 
			"	// Building the matrix Eye Space -> Tangent Space", 
			"	vec3 n = normalize (gl_NormalMatrix * gl_Normal);", 
			"	vec3 t = normalize (gl_NormalMatrix * tangent);", 
			"	vec3 b = cross (n, t);", 
			"	", 
			"	vec3 vertexPosition = vec3(gl_ModelViewMatrix *  pos);", 
			"	vec3 lightDir = normalize((gl_ModelViewMatrix * sunLocation) - vertexPosition);", 
			"		", 
			"		", 
			"	// transform light and half angle vectors by tangent basis", 
			"	vec3 v;", 
			"	v.x = dot (lightDir, t);", 
			"	v.y = dot (lightDir, b);", 
			"	v.z = dot (lightDir, n);", 
			"	lightVec = normalize (v);", 
			"	", 
			"	  ", 
			"	v.x = dot (vertexPosition, t);", 
			"	v.y = dot (vertexPosition, b);", 
			"	v.z = dot (vertexPosition, n);", 
			"	eyeVec = normalize (v);", 
			"	", 
			"	", 
			"	", 
			"	/* Normalize the halfVector to pass it to the fragment shader */", 
			"", 
			"	// No need to divide by two, the result is normalized anyway.", 
			"	// vec3 halfVector = normalize((vertexPosition + lightDir) / 2.0); ", 
			"	vec3 halfVector = normalize(vertexPosition + lightDir);", 
			"	v.x = dot (halfVector, t);", 
			"	v.y = dot (halfVector, b);", 
			"	v.z = dot (halfVector, n);", 
			"", 
			"	// No need to normalize, t,b,n and halfVector are normal vectors.", 
			"	//normalize (v);", 
			"	halfVec = v ; ", 
			"	", 
			"	//Finish up!", 
			"	position = pos;",  
			"	nearShadowCoord = gl_TextureMatrix[6] * pos;", 
			"	farShadowCoord = gl_TextureMatrix[7] * pos;", 
			"    gl_Position = gl_ModelViewProjectionMatrix * pos;", 
			"    gl_FrontColor = gl_Color;", 
			"}", 
			"", 
	};
	
	public void generateGLSL() throws Exception {
		//Debug print shader information
		/*System.out.println("/**SHADER SOURCE INFORMATION");
		System.out.println("name=" + name);
		
		while(i < texture.length) {
			if(texture[i] != null)
				System.out.println("texture[" + i + "]=" + texture[i]);
			i++;
		}
		if(channels[0] != null)
			System.out.println("diffuse=" + channels[0]);
		if(channels[1] != null)
			System.out.println("normal=" + channels[1]);
		if(channels[2] != null)
			System.out.println("specular=" + channels[2]);
		if(channels[3] != null)
			System.out.println("illumination=" + channels[3]);
		if(channels[4] != null)
			System.out.println("transparency=" + channels[4]);*/
		//System.out.println("END SHADER INFORMATION**/");
		//System.out.println();
		
		int i = 0;
		ArrayList<String> frag = new ArrayList<String>();
		ArrayList<String> vert = new ArrayList<String>();
		frag.add("//THIS IS A GLSL FRAGMENT SHADER GENERATED BY 12D SHADER ENGINE// ");
		vert.add("//THIS IS A GLSL VERTEX SHADER GENERATED BY 12D SHADER ENGINE// ");
		
		/** GENERATING VERTEX SHADER **/
		i = 0;
		while(i < glslVertexVars.length) {
			vert.add(glslVertexVars[i]);
			i++;
		}
		
		/** GENERATING FRAGMENT SHADER **/
		/**Create variables**/
		//Basic vars
		i = 0;
		while(i < glslBaseVars.length) {
			frag.add(glslBaseVars[i]);
			i++;
		}
		frag.add(" ");
		
		//Fog vars
		i = 0;
		while(i < glslFogVars.length) {
			frag.add(glslFogVars[i]);
			i++;
		}
		frag.add(" ");
		
		//Texture vars
		i = 0;
		while(i < texture.length) {
			if(texture[i] != null)
				frag.add("uniform sampler2D texture" + i + ";");
			i++;
		}
		frag.add("uniform vec2 uvAnim;");
		frag.add("uniform float rotAnim;");
		frag.add(" ");
		
		//Cubemap vars
		i = 0;
		while(i < cube.length) {
			if(cube[i] != null)
				frag.add("uniform samplerCube cubemap" + i + ";");
			i++;
		}
		frag.add(" ");
		
		//Scalar vars
		i = 0;
		while(i < scalars) {
			frag.add("uniform float scalar" + Character.toUpperCase(int2Alpha(i)) + ";");
			i++;
		}
		
		//Normal vars
		if(channels[1] != null || channels[2] != null || cubemaps) {
			i = 0;
			while(i < glslNormalVars.length) {
				frag.add(glslNormalVars[i]);
				i++;
			}
			frag.add(" ");
		}
		
		//Lighting vars + lighting functions
		if(!unlit) {
			i = 0;
			while(i < glslLightingVars.length) {
				frag.add(glslLightingVars[i]);
				i++;
			}
			frag.add(" ");
			
			//SunLight sun = game.getSun();
			frag.add("const float lightMaxIntensity = " + 1.0 + ";"); //TODO:need to get correct sun values for this D:
			frag.add("const float lightMinIntensity = " + 0.2 + ";");
			frag.add("uniform float nearPixelOffset;");
			frag.add("uniform float farPixelOffset;");
			frag.add("uniform float moireNearQuality;");
			frag.add("uniform float moireFarQuality;");
			frag.add("uniform float pcfNear;");
			frag.add("uniform float pcfFar;");
			frag.add("uniform float radNear;");
			frag.add("uniform float radOverlap;");
			frag.add("uniform float radFar;");
			frag.add(" ");
			
			i = 0;
			while(i < glslGetDepthFunction.length) {
				frag.add(glslGetDepthFunction[i]);
				i++;
			}
			frag.add(" ");
			
			i = 0;
			while(i < glslGetShadowFunction.length) {
				frag.add(glslGetShadowFunction[i]);
				i++;
			}
			frag.add(" ");
			
			i = 0;
			while(i < glslPointLineDistFunction.length) {
				frag.add(glslPointLineDistFunction[i]);
				i++;
			}
			frag.add(" ");
		}
		//TODO: optimize adding in the lerp, color, pow, and diff functions maybe...
		i = 0;
		while(i < glslLerpFunction.length) {
			frag.add(glslLerpFunction[i]);
			i++;
		}
		frag.add(" ");
		
		i = 0;
		while(i < glslDiffFunction.length) {
			frag.add(glslDiffFunction[i]);
			i++;
		}
		frag.add(" ");
		
		i = 0;
		while(i < glslColorFunction.length) {
			frag.add(glslColorFunction[i]);
			i++;
		}
		frag.add(" ");
		
		i = 0;
		while(i < glslCoordRotateFunction.length) {
			frag.add(glslCoordRotateFunction[i]);
			i++;
		}
		frag.add(" ");
		
		/**Create shader main**/
		//Set main and base values
		frag.add("void main() {");
		frag.add("bool inShadow = false;");
		if(channels[1] == null && channels[2] == null)
			frag.add("vec3 specularDirection = vec3(0,0,1);");
		else
			frag.add("vec3 specularDirection = specLight;");
		if(!unlit)
			frag.add("vec4 light = vec4(0,0,0,1);");
		else
			frag.add("vec4 light = vec4(1,1,1,1);");
		if(!unlit || channels[1] != null)
			frag.add("vec3 N = normalize(normal);");
		frag.add(" ");
		
		//Get all texture samples and do material level operations on them (stuff in the .shader file)
		frag.add("vec4 frag = 0;");
		if(channels[0] != null) {
			i = 0;
			String cs[] = parseChannel("diffuseMaterial", channels[0]);
			while(i < cs.length) {
				frag.add(cs[i]);
				i++;
			}
		}
		if(channels[1] != null) {
			i = 0;
			String cs[] = parseChannel("normalMaterial", channels[1]);
			while(i < cs.length) {
				frag.add(cs[i]);
				i++;
			}
		}
		if(channels[2] != null) {
			i = 0;
			String cs[] = parseChannel("specularMaterial", channels[2]);
			while(i < cs.length) {
				frag.add(cs[i]);
				i++;
			}
		}
		if(channels[3] != null) {
			i = 0;
			String cs[] = parseChannel("illuminationMaterial", channels[3]);
			while(i < cs.length) {
				frag.add(cs[i]);
				i++;
			}
		}
		if(channels[4] != null) {
			i = 0;
			String cs[] = parseChannel("transparencyMaterial", channels[4]);
			while(i < cs.length) {
				frag.add(cs[i]);
				i++;
			}
		}
		frag.add(" ");
		
		//Light calculations
		if(!unlit) {
			i = 0;
			while(i < glslLightingBlock.length) {
				frag.add(glslLightingBlock[i]);
				i++;
			}
		}
		
		//Diffuse calculations
		frag.add("frag = diffuseMaterial;");
		frag.add("vec4 diffuseLight = vec4(1,1,1,1);");
		frag.add(" ");
		
		//Specular calculation
		if(channels[2] != null) {
			frag.add("vec4 specularLight = light;");
			frag.add("float shininess;");
			if(channels[1] == null) {
				frag.add("vec3 sN = normal;");
				frag.add("vec3 sL = normalize(specularDirection);");
				frag.add("vec3 sH = reflect(-specEye,sN);");
				frag.add("vec3 sE = normalize(position - specPosition);");
				frag.add("shininess = pow(max(0.0, dot(sL, sH)), 2.0) * pow(max(dot(specEye, sE), 0), 2.0);");
				frag.add("frag += specularMaterial * specularLight * shininess;");
				/*
				frag.add("vec3 sN = normal;");
				frag.add("vec3 sL = normalize(specularDirection);");
				frag.add("vec3 sH = reflect(-specEye,sN);");
				frag.add("shininess = pow(max(0.0, dot(sL, sH)), 2);");
				frag.add("frag += specularMaterial * specularLight * shininess;");
				 */
			}
		}
		else if(channels[1] != null) {
			frag.add("vec4 specularMaterial = vec4(0,0,0,0);");
			frag.add("vec4 specularLight = light;");
			frag.add("float shininess;");
		}
		
		//Normal calculation
		if(channels[1] != null) {
			i = 0;
			while(i < glslNormalBlock.length) {
				frag.add(glslNormalBlock[i]);
				i++;
			}
		}
			
		//Illumination calculations
		if(channels[3] != null && !unlit)
			frag.add("light += illuminationMaterial;");
		
		//Add light
		frag.add("frag *= light;");
		
		//Fog calculations
		if(allowFog) {
			frag.add("float fdist = length(vec3(0,0,0) - vec3(gl_ModelViewProjectionMatrix * position));");
			frag.add("float d = fogEnd - fogStart;");
			frag.add("float a = max(fdist - fogStart, 0);");
			frag.add("float b = min(a, d);");
			frag.add("float c = (b / d) * fogDensity;");
			frag.add("frag = (fogColor * c) + ((1 - c) * frag);");
		}
		
		//Transparency calculations
		if(channels[4] != null)
			frag.add("frag.a = transparencyMaterial.a;");
		else
			frag.add("frag.a = 1;");
		
		//Finish up
		frag.add("gl_FragColor = frag;");
		frag.add("}");
		
		/** Compile the generated GLSL shader and register it with a number **/
		String f = "";
		String v = "";
		i = 0;
		while(i < vert.size()) {
			v += vert.get(i) + "\n";
			i++;
		}
		i = 0;
		while(i < frag.size()) {
			f += frag.get(i) + "\n";
			i++;
		}
		//System.out.println("shader built: " + file + "\n" + f);
		//if(name.equals("default material"))
				//System.out.println(f);
		glslProgram = game.engine.graphics.compileShaderProgram(v, f);
	}
	
	public String[] parseChannelDEPRECATED(String t, String s) {
		String a = s;
		String b[] = a.split("texture<");
		String c = "vec4 " + t + "=";
		String f[] = new String[b.length];
		
		//Look for special chars before the first texture call
			char dc[] = a.split("texture<")[0].toCharArray();
			int j = 0;
			while(j < dc.length) {
				if(dc[j] == '+')
					c += "+";
				if(dc[j] == '-')
					c += "-";
				if(dc[j] == '*')
					c += "*";
				if(dc[j] == '/')
					c += "/";
				if(dc[j] == '(')
					c += "(";
				if(dc[j] == ')')
					c += ")";
				if(dc[j] == ',')
					c += ",";
				if(dc[j] == '.')
					c += ".";
				if(Character.isDigit(dc[j]))
					c += dc[j];
				if(dc[j] == 'l') {
					if(dc.length - j+1 > 5) {
						if(dc[j+1] == 'e' && dc[j+2] == 'r' && dc[j+3] == 'p' && dc[j+4] == '(')
							c+= "lerp";
					}
				}
				if(dc[j] == 'd') {
					if(dc.length - j+1 > 5) {
						if(dc[j+1] == 'i' && dc[j+2] == 'f' && dc[j+3] == 'f' && dc[j+4] == '(')
							c+= "diff";
					}
				}
				if(dc[j] == 'p') {
					if(dc.length - j+1 > 4) {
						if(dc[j+1] == 'o' && dc[j+2] == 'w' && dc[j+3] == '(')
							c+= "pow";
					}
				}
				if(dc[j] == 's') {
					if(dc.length - j+1 > 7) {
						if(dc[j+1] == 'c' && dc[j+2] == 'a' && dc[j+3] == 'l' && dc[j+4] == 'a' && dc[j+5] == 'r')
							c+= "scalar" + dc[j+6];
					}
				}
				if(dc[j] == 'c') {
					if(dc.length - j+1 > 6) {
						if(dc[j+1] == 'o' && dc[j+2] == 'l' && dc[j+3] == 'o' && dc[j+4] == 'r')
							c+= "color";
					}
				}
				j++;
			}
		//Start reading after the first texture call
		//TODO: Optimize out UV animation and what not if uneeded
		int i = 1;
		while(i < b.length) {
			String e[] = b[i].split(">")[0].split(",");
			if(!e[5].equals("0") || !e[6].equals("0")) {
				f[i-1] = "vec4 tex" + tex + " = " + "texture2D(texture" + e[0] + ", (gl_TexCoord[0].st + vec2(" + e[3] + ", " + e[4] + ") + " + "(uvAnim * vec2(" + e[5] + "," + e[6] + ")))" + " * vec2(" + e[1] + ", " + e[2] + "));";
				uvAnimation = new float[2];
			}
			else
				f[i-1] = "vec4 tex" + tex + " = " + "texture2D(texture" + e[0] + ", (gl_TexCoord[0].st + vec2(" + e[3] + ", " + e[4] + "))" + " * vec2(" + e[1] + ", " + e[2] + "));";
			if(e[7].equals("rgb") || e[7].equals("color"))
				c += "tex" + tex;
			else if(e[7].equals("r") || e[7].equals("red"))
				c += "vec4(" + "tex" + tex + ".r," + "tex" + tex + ".r," + "tex" + tex + ".r," + "tex" + tex + ".r)";
			else if(e[7].equals("g") || e[7].equals("green"))
				c += "vec4(" + "tex" + tex + ".g," + "tex" + tex + ".g," + "tex" + tex + ".g," + "tex" + tex + ".g)";
			else if(e[7].equals("b") || e[7].equals("blue"))
				c += "vec4(" + "tex" + tex + ".b," + "tex" + tex + ".b," + "tex" + tex + ".b," + "tex" + tex + ".b)";
			else if(e[7].equals("a") || e[7].equals("alpha"))
				c += "vec4(" + "tex" + tex + ".a," + "tex" + tex + ".a," + "tex" + tex + ".a," + "tex" + tex + ".a)";
			tex++;
			
			
			String d[] = b[i].split(">");
			if(d.length > 1) {
				dc = d[1].toCharArray();
				j = 0;
				while(j < dc.length) {
					if(dc[j] == '+')
						c += "+";
					if(dc[j] == '-')
						c += "-";
					if(dc[j] == '*')
						c += "*";
					if(dc[j] == '/')
						c += "/";
					if(dc[j] == '(')
						c += "(";
					if(dc[j] == ')')
						c += ")";
					if(dc[j] == ',')
						c += ",";
					if(dc[j] == '.')
						c += ".";
					if(Character.isDigit(dc[j]))
						c += dc[j];
					if(dc[j] == 'l') {
						if(dc.length - j+1 > 5) {
							if(dc[j+1] == 'e' && dc[j+2] == 'r' && dc[j+3] == 'p' && dc[j+4] == '(')
								c+= "lerp";
						}
					}
					if(dc[j] == 'd') {
						if(dc.length - j+1 > 5) {
							if(dc[j+1] == 'i' && dc[j+2] == 'f' && dc[j+3] == 'f' && dc[j+4] == '(')
								c+= "diff";
						}
					}
					if(dc[j] == 'p') {
						if(dc.length - j+1 > 4) {
							if(dc[j+1] == 'o' && dc[j+2] == 'w' && dc[j+3] == '(')
								c+= "pow";
						}
					}
					if(dc[j] == 's') {
						if(dc.length - j+1 > 7) {
							if(dc[j+1] == 'c' && dc[j+2] == 'a' && dc[j+3] == 'l' && dc[j+4] == 'a' && dc[j+5] == 'r')
								c+= "scalar" + dc[j+6];
						}
					}
					if(dc[j] == 'c') {
						if(dc.length - j+1 > 6) {
							if(dc[j+1] == 'o' && dc[j+2] == 'l' && dc[j+3] == 'o' && dc[j+4] == 'r')
								c+= "color";
						}
					}
					j++;
				}
			}
			i++;
		}
		c += ";";
		f[i-1] = c;
		return f;
	}
	
	//Converts the channel input from the engines format to GLSL.
	//TODO: Need to optimize out uneeded UV operations and variables
	int tex = 0;
	public String[] parseChannel(String t, String s) {
		//Parse texture<> calls
		ArrayList<String> prsd = new ArrayList<String>();
		String c[] = s.split("texture<");
		int i = 1;
		while(i < c.length) {
			String e[] = c[i].split(">", 2)[0].split(",");
			int ind;
			String um, vm, uo, vo, ua, va, rot, ran;
			char chan;
			
			ind = Integer.parseInt(e[0]);
			
			um = e[1];
			vm = e[2];
			
			uo = e[3];
			vo = e[4];
			
			ua = e[5];
			va = e[6];
			
			rot = e[7];
			ran = e[8];
			
			if(e[9].equals("rgb") || e[9].equals("color"))
				chan = 'c';
			else
				chan = e[9].charAt(0);
			
			//Build optimized Texcoords... 
			String coord = "gl_TexCoord[0].st";
			if(!uo.equals("0") || !vo.equals("0")) {
				coord = coord + " + vec2(" + uo + ", " + vo + ")";
			}
			if(!ua.equals("0") || !va.equals("0")) {
				uvAnimation = new float[2];
				coord = coord + " + (uvAnim * vec2(" + ua + "," + va + "))";
			}
			if(!rot.equals("0") || !ran.equals("0")) {
				if(ran.equals("0"))
					coord = "coordRotate((" + coord + "), " + rot + ")";
				else
					coord = "coordRotate((" + coord + "), (" + rot + " + (" + ran + " * rotAnim)))";
			}
			if(!um.equals("1") || !vm.equals("1")) {
				coord = "vec2(" + um + "," + vm + ") * " + "(" + coord + ")";
			}
			
			String col;
			String samp;
			if(chan == 'c') {
				samp = "vec4 samp" + tex + " = texture2D(texture" + ind + ", " + coord + ");";
				prsd.add(samp);
			}
			else {
				col = "float col" + tex + " = texture2D(texture" + ind + ", " + coord + ")." + chan + ";";
				samp = "vec4 samp" + tex + " = vec4(col" + tex + ",col" + tex + ",col" + tex + ",col" + tex + ");";
				prsd.add(col);
				prsd.add(samp);
			}
			
			c[i] = "samp" + tex + "" + c[i].split(">", 2)[1];
			
			i++;
			tex++;
		}
		//Rebuild pixel line...
		String pix = "";
		i = 0;
		while(i < c.length) {
			pix = pix + c[i];
			i++;
		}
		//Parse cubemap<> calls
		c = pix.split("cubemap<");
		i = 1;
		while(i < c.length) {
			String e[] = c[i].split(">", 2)[0].split(",");
			int ind = Integer.parseInt(e[0]);
			char chan;
			
			if(e[1].equals("rgb") || e[1].equals("color"))
				chan = 'c';
			else
				chan = e[1].charAt(0);
			
			String col;
			String samp;
			
			if(chan == 'c') {
				samp = "vec4 samp" + tex + " = vec4(textureCube(cubemap"+ind+",-reflect(normalize(vec3(position-specPosition)), normal)));";
				prsd.add(samp);
			}
			else {
				col = "vec4 col" + tex + " = vec4(textureCube(cubemap"+ind+",normal))." + chan + ";";
				samp = "vec4 samp" + tex + " = vec4(col" + tex + ",col" + tex + ",col" + tex + ",col" + tex + ");";
				prsd.add(col);
				prsd.add(samp);
			}
			
			c[i] = "samp" + tex + "" + c[i].split(">", 2)[1];
			
			i++;
			tex++;
		}
		
		//Parse all non glsl operations
		
		//Build array and return
		i = 0;
		pix = "vec4 " + t + " = ";
		while(i < c.length) {
			pix = pix + c[i];
			i++;
		}
		pix = pix + ";";
		prsd.add(pix);
	
		return prsd.toArray(new String[prsd.size()]);
	}
	
	public void step(float f) {
		if(uvAnimation != null) {
			uvAnimation[0] += f;
			uvAnimation[1] += f;
		}
		rotAnimation += f;
			
	}
	
	//TODO: STOP! USING! GLGETUNIFORMLOCATION!
	
	public void glMaterialSet(int shaderProgram) {	
		int i = 0;
		while(i < texture.length) {
			if(texture[i] != null) {
				org.lwjgl.opengl.ARBMultitexture.glActiveTextureARB(org.lwjgl.opengl.ARBMultitexture.GL_TEXTURE0_ARB + i);
				glBindTexture(GL_TEXTURE_2D, texture[i].getIndex());
				org.lwjgl.opengl.ARBShaderObjects.glUniform1iARB(glGetUniformLocation(shaderProgram, "texture" + i), i);
			}
			i++;
		}
		i = 0;
		while(i < cube.length) {
			if(cube[i] != null) {
				glActiveTexture(GL_TEXTURE10 + i);
				glBindTexture(GL_TEXTURE_CUBE_MAP, cube[i].getIndex());
				glBindSampler(cube[i].getIndex(), 10 + i);
				org.lwjgl.opengl.ARBShaderObjects.glUniform1iARB(glGetUniformLocation(shaderProgram, "cubemap" + i), 10 + i);
			}
			i++;
		}
		if(uvAnimation != null)
			glUniform2f(glGetUniformLocation(shaderProgram, "uvAnim"), uvAnimation[0], uvAnimation[1]);
		if(rotAnimation != 0)
			glUniform1f(glGetUniformLocation(shaderProgram, "rotAnim"), rotAnimation);
	}

	//This glMaterialSet is used for shaders with special values, such as the headlights on a car.
	//If you use scalarA in your shader as a variable then the value of scalarA given to this function will be used. 
	//EXAMPLE: illumination=texture<1,1,1,0,0,0,0,rgb> * scalarA
	//So while the headlights are on we give 1 and when they are off we give 0.
	public void glMaterialSet(int shaderProgram, float scalar[]) {
		int i = 0;
		while(i < texture.length) {
			if(texture[i] != null) {
				org.lwjgl.opengl.ARBMultitexture.glActiveTextureARB(org.lwjgl.opengl.ARBMultitexture.GL_TEXTURE0_ARB + i);
				glBindTexture(GL_TEXTURE_2D, texture[i].getIndex());
				org.lwjgl.opengl.ARBShaderObjects.glUniform1iARB(glGetUniformLocation(shaderProgram, "texture" + i), i);
			}
			i++;
		}
		i = 0;
		while(i < cube.length) {
			if(cube[i] != null) {
				org.lwjgl.opengl.ARBMultitexture.glActiveTextureARB(org.lwjgl.opengl.ARBMultitexture.GL_TEXTURE10_ARB + i);
				glBindTexture(GL_TEXTURE_CUBE_MAP, cube[i].getIndex());
				org.lwjgl.opengl.ARBShaderObjects.glUniform1iARB(glGetUniformLocation(shaderProgram, "cubemap" + i), 10 + i);
			}
			i++;
		}
		i = 0;
		while(i < scalar.length) {
			glUniform1f(glGetUniformLocation(shaderProgram, "scalar" + Character.toUpperCase(int2Alpha(i))), scalar[i]);
			i++;
		}
		if(uvAnimation != null)
			glUniform2f(glGetUniformLocation(shaderProgram, "uvAnim"), uvAnimation[0], uvAnimation[1]);
		if(rotAnimation != 0)
			glUniform1f(glGetUniformLocation(shaderProgram, "rotAnim"), rotAnimation);
	}
	
	//Converts characters to ints and back. Used to convert the scalar array to the usable variable names.
	static final char[] alpha = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	public char int2Alpha(int i) {
		return alpha[i];
	}
	
	public int alpha2Int(char c) {
		int i = 0;
		while(i < alpha.length) {
			if(c == alpha[i])
				return i+1;
			i++;
		}
		return 0;
	}
}
