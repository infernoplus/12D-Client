name=metal panels generator red

mipmaps=true
unlit=false

texture<0>=graphics/texture/bsp/halo/metal panels generator-red.png
texture<1>=graphics/texture/bsp/halo/detail metal sratches.png
texture<2>=graphics/texture/bsp/halo/metal panels generator bump.png
texture<3>=graphics/texture/bsp/halo/metal panels generator glow.png
texture<4>=graphics/texture/multipurpose/multi_gradient_x.png

diffuse=texture<0,1,-1,0,0,0,0,rgb> * texture<1,4.1,4.1,0,0,0,0,rgb>
normal=texture<2,1,-1,0,0,0,0,rgb>
specular=texture<0,1,-1,0,0,0,0,a>
illumination=texture<3,1,-1,0,0,0,0,b> * texture<4,1,-1,0,0,0,0.01,b> * 1.5

%EOF