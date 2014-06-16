name=portent clouds

mipmaps=true
castshadows=false
unlit=true
fog=false

texture<0>=sky/portent sky/texture/portent cloads.png
texture<1>=sky/halo clear sky/texture/clouds puffy light.png

diffuse=texture<0,1,-1,0,0,0.00015,0,0,0,rgb> + (texture<0,1,-1,0,0,0.00015,0,0,0,rgb> * (texture<0,1,-1,0,0,0.00015,0,0,0,a> * texture<1,16,-2,0,0,0.00035,0.00001,0,0,rgb>) * 1.4)
transparency=texture<0,1,-1,0,0,0.00015,0,0,0,a>
