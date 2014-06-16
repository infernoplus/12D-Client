uniform bool bRotation;
uniform vec4 modelPosition;
uniform vec4 modelRotation;
uniform float modelScale;
uniform bool bSkin;
uniform vec3 bonesOffset[64];
uniform vec3 bonesPos[64];
uniform vec4 bonesRot[64];

attribute vec3 indices;
attribute vec3 weights;
attribute vec3 tangent;

vec3 quatRotation(vec3 v, vec4 r) {
			float q00 = 2.0 * r.x * r.x;
			float q11 = 2.0 * r.y * r.y;
			float q22 = 2.0 * r.z * r.z;

			float q01 = 2.0 * r.x * r.y;
			float q02 = 2.0 * r.x * r.z;
			float q03 = 2.0 * r.x * r.w;
			
			float q12 = 2.0 * r.y * r.z;
			float q13 = 2.0 * r.y * r.w;

			float q23 = 2.0 * r.z * r.w;
			vec3 f = vec3(0,0,0);
			f.x = (1.0 - q11 - q22) * v.x + (q01 - q23) * v.y + (q02 + q13) * v.z;
			f.y = (q01 + q23) * v.x + (1.0 - q22 - q00) * v.y + (q12 - q03) * v.z;
			f.z = (q02 - q13) * v.x + (q12 + q03) * v.y + (1.0 - q11 - q00) * v.z;
			return f;
}
 
void main() {
	gl_TexCoord[0] = gl_MultiTexCoord0;
	vec4 pos = gl_Vertex;
	
	//Skin transformation and quaternion rotation
	if(bSkin) {
		int i = 0;
		
		//offset - transform for this frame
		float wx = 0;
		float wy = 0;
		float wz = 0;
		
		//position - normal position of bone
		float wa = 0;
		float wb = 0;
		float wc = 0;
		
		while(i < 3) {
			if(weights[i] != 0) {
				wx += (bonesOffset[int(indices[i])].x*weights[i]);
				wy += (bonesOffset[int(indices[i])].y*weights[i]);
				wz += (bonesOffset[int(indices[i])].z*weights[i]);
				wa += (bonesPos[int(indices[i])].x*weights[i]);
				wb += (bonesPos[int(indices[i])].y*weights[i]);
				wc += (bonesPos[int(indices[i])].z*weights[i]);
			}
			i++;
		}
		
		//Subtract location so we can rotate around origin 0,0,0
		pos.x -= wa;
		pos.y -= wb;
		pos.z -= wc;
		
		//number of actual weights (for performance, not done due to bug...)
				
		//do rotation
		i = 0;
		vec4 v = pos;
		vec4 rot[3];
		
		while(i < 3) {
			vec4 r = bonesRot[int(indices[i])];
			float q00 = 2.0 * r.x * r.x;
			float q11 = 2.0 * r.y * r.y;
			float q22 = 2.0 * r.z * r.z;
	
			float q01 = 2.0 * r.x * r.y;
			float q02 = 2.0 * r.x * r.z;
			float q03 = 2.0 * r.x * r.w;
	
			float q12 = 2.0 * r.y * r.z;
			float q13 = 2.0 * r.y * r.w;
	
			float q23 = 2.0 * r.z * r.w;
	
			rot[i].x = (1.0 - q11 - q22) * v.x + (q01 - q23) * v.y + (q02 + q13) * v.z;
			rot[i].y = (q01 + q23) * v.x + (1.0 - q22 - q00) * v.y + (q12 - q03) * v.z;
			rot[i].z = (q02 - q13) * v.x + (q12 + q03) * v.y + (1.0 - q11 - q00) * v.z;
			i++;
		}
		
		//Average the rotations by weight and apply them
		i=0;
		vec4 final;
		while(i < 3) {
			final.x += (rot[i].x * weights[i]);
			final.y += (rot[i].y * weights[i]);
			final.z += (rot[i].z * weights[i]);
			i++;
		}
		
		pos = final;
		
		//Finish up skinning by adding the location and offset
		pos.x += wa;
		pos.y += wb;
		pos.z += wc;
		
		pos.x += wx;
		pos.y += wy;
		pos.z += wz;		
	}
	
	//Quat rotation in the model
	if(bRotation) {
		pos = vec4(quatRotation(vec3(pos), modelRotation), 1);
	}
	
	//Scale of this model
	pos = pos * vec4(modelScale,modelScale,modelScale,1);
	//Location offset in model
	pos += modelPosition;
    gl_Position = gl_ModelViewProjectionMatrix * pos;
    gl_FrontColor = gl_Color;
}

