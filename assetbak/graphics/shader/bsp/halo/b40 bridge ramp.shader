name=bridge ramp

mipmaps=true
unlit=false

texture<0>=graphics/texture/bsp/halo/b40_bridgeramp.png
texture<1>=graphics/texture/bsp/halo/detail metal sratches.png
texture<2>=graphics/texture/bsp/halo/b40_bridgeramp_bump.png
texture<3>=graphics/texture/bsp/halo/b40_bridgeramp_glow.png
texture<4>=graphics/texture/multipurpose/multi_gradient_x.png

diffuse=texture<0,1,-1,0,0,0,0,rgb> * texture<1,4.1,4.1,0,0,0,0,rgb>
normal=texture<2,1,-1,0,0,0,0,rgb>
specular=texture<0,1,-1,0,0,0,0,a>
illumination=texture<3,1,-1,0,0,0,0,b> * texture<4,1,-1,0,0,0,0.01,b>

%EOF