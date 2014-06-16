uniform sampler2D world;
uniform sampler2D fp;
uniform sampler2D ui;
uniform float pixelX;
uniform float pixelY;
varying vec2 texCoord;

void main() {
  	vec4 worldPixel = texture2D(world, texCoord);
  	vec4 fpPixel = texture2D(fp, texCoord);
  	vec4 uiPixel = texture2D(ui, texCoord);
  	
  	vec4 uiWorldPixel = ((1 - fpPixel.a) * worldPixel) + (fpPixel.a * fpPixel);
    gl_FragColor = ((1 - uiPixel.a) * uiWorldPixel) + (uiPixel.a * uiPixel);
}

	/*vec4 pixel;
	int x = -3;
	int y = -3;
	while(y <= 3) {
		while(x <= 3) {
			sample = texture2D(render, texCoord + vec2(x*pixelX, y*pixelY));
			pixel += sample*sample.a;
			x++;
		}
		y++;
		x=-3;
	}
	pixel = max((pixel / 49) - 0.75, 0)*1.25;
  	gl_FragColor = texture2D(render, texCoord) + pixel;
  	
   vec4 sum = vec4(0);
   int j;
   int i;

   for( i= -4 ;i < 4; i++)
   {
        for (j = -3; j < 3; j++)
        {
            sum += texture2D(render, texCoord + vec2(j, i)*0.004) * 0.25;
        }
   }
       if (texture2D(render, texCoord).r < 0.3)
    {
       gl_FragColor = sum*sum*0.012 + texture2D(render, texCoord);
    }
    else
    {
        if (texture2D(render, texCoord).r < 0.5)
        {
            gl_FragColor = sum*sum*0.009 + texture2D(render, texCoord);
        }
        else
        {
            gl_FragColor = sum*sum*0.0075 + texture2D(render, texCoord);
        }
    }*/