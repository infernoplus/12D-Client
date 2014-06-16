name=clouds puffy

mipmaps=true
castshadows=false
unlit=true
fog=false

texture<0>=graphics/texture/sky/halo clear sky/clouds puffy light.png
texture<1>=graphics/texture/sky/halo clear sky/sky clear blue.png

diffuse=texture<0,1,1,0,0,0.00002,0.0003,0,0,rgb>
transparency=texture<1,1,1,0,0,0,0,0,0,a> * scalarA

%EOF