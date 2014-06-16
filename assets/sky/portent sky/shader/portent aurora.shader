name=portent sky

mipmaps=true
castshadows=false
unlit=true
fog=false

texture<0>=sky/portent sky/texture/portent aurora.png
texture<1>=sky/portent sky/texture/aurora noise.png

diffuse=texture<0,1,-1,0,0,0,0,0,0,rgb> * (texture<0,1,-1,0,0,0,0,0,0,a> * texture<1,0.4,-1,0,0,-0.0035,0,0,0,rgb> * 1.5)
transparency=texture<0,1,-1,0,0,0,0,0,0,a>
