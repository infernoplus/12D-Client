name=metal panels generic ext clean

mipmaps=true
unlit=false

texture<0>=graphics/texture/bsp/halo/metal panels generic ext clean.png
texture<1>=graphics/texture/bsp/halo/detail metal sratches.png
texture<2>=graphics/texture/bsp/halo/metal flat generic bump.png

diffuse=texture<0,1,-1,0,0,0,0,rgb> * texture<1,4.1,4.1,0,0,0,0,rgb>
normal=texture<2,2,2,0,0,0,0,rgb>
specular=texture<0,1,-1,0,0,0,0,a>

%EOF