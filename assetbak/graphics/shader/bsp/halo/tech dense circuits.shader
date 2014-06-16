name=tech dense circuits

mipmaps=true
unlit=false

texture<0>=graphics/texture/bsp/halo/tech dense circuits.png
texture<1>=graphics/texture/bsp/halo/detail metal sratches.png
texture<2>=graphics/texture/bsp/halo/tech dense circuits bump.png
texture<3>=graphics/texture/bsp/halo/tech dense circuits glow.png
texture<4>=graphics/texture/multipurpose/multi_difference.png

diffuse=texture<0,1,-1,0,0,0,0,rgb> * texture<1,4.1,4.1,0,0,0,0,rgb>
normal=texture<2,1,-1,0,0,0,0,rgb>
specular=texture<0,1,-1,0,0,0,0,a>
illumination=texture<3,1,-1,0,0,0,0,b> * pow((texture<4,1,-1,0,0,0.006,0.01,r> * texture<4,1,-1,0,0,-0.02,-0.015,g>),2) * 4

%EOF