package twelveengine.graphics;

import twelveengine.data.Vertex;
import twelveengine.graphics.Shader;
import twelveutil.MathUtil;

public class Triangle {
	//Triangle coordinates
	public Vertex a, b, c;
	//Triangle normals
	public Vertex i, j, k;
	//Triangle tangents
	public Vertex m, n, o;
	//Triangle texture coordiantes
	public Vertex u, v, w;
	//The normal of this triangle
	public Vertex normal;
	//Triangle Material
	public Shader shdr;
	//Model that this triangle belongs too.
	
	public Triangle(Vertex aa, Vertex bb, Vertex cc, Vertex ii, Vertex jj, Vertex kk, Vertex uu, Vertex vv, Vertex ww, Shader t) {
		a = aa;
		b = bb;
		c = cc;
		i = MathUtil.normalize(ii);
		j = MathUtil.normalize(jj);
		k = MathUtil.normalize(kk);
		u = uu;
		v = vv;
		w = ww;
		
		normal = MathUtil.normalize(MathUtil.crossProduct(MathUtil.subtract(a,c), MathUtil.subtract(b,c)));
		
		m = generateTangent(i);
		n = generateTangent(j);
		o = generateTangent(k);
		
		shdr = t;
	}
	
	public Vertex generateTangent(Vertex norm) {
		Vertex tangent; 
		Vertex binormal; 
		
		Vertex c1 = MathUtil.crossProduct(norm, new Vertex(0.0f, 0.0f, 1.0f)); 
		Vertex c2 = MathUtil.crossProduct(norm, new Vertex(0.0f, 1.0f, 0.0f)); 
		
		if(MathUtil.magnitude(c1) > MathUtil.magnitude(c2))
		{
			tangent = c1;	
		}
		else
		{
			tangent = c2;	
		}
		
		tangent = MathUtil.normalize(tangent);
		
		binormal = MathUtil.crossProduct(norm, tangent); 
		binormal = MathUtil.normalize(binormal);
		
		return tangent;
	}
	
	/*public Vertex generateTangent(Vertex v1, Vertex v2, Vertex st1, Vertex st2, Vertex nor)
	{
		Vertex normal = MathUtil.crossProduct(v1, v2);
		float coef = 1f/(float)((st1.x * st2.y) - (st2.x * st1.y));
		Vertex tangent = new Vertex();

		tangent.x = coef * ((v1.x * st2.y)  + (v2.x * -st1.y));
		tangent.y = coef * ((v1.y * st2.y)  + (v2.y * -st1.y));
		tangent.z = coef * ((v1.z * st2.y)  + (v2.z * -st1.y));
		
		Vertex binormal = MathUtil.crossProduct(normal, tangent);
		
		tangent = MathUtil.normalize(tangent);
		
		normal = MathUtil.normalize(normal);
		
		//System.out.println(MathUtil.toString(normal) + " " + MathUtil.toString(normal));
		
		return tangent;
	}*/
}
