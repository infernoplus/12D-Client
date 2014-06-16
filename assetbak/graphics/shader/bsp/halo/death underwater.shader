name=deathisland underwater

mipmaps=true
unlit=false

texture<0>=graphics/texture/bsp/halo/underwater.png
texture<1>=graphics/texture/bsp/halo/detail sand.png
texture<2>=graphics/texture/bsp/halo/waves water detail.png
texture<3>=graphics/texture/bsp/halo/crap water0.png
texture<4>=graphics/texture/bsp/halo/crap water1.png

diffuse=texture<0,1,-1,0,0,0,0,rgb>*lerp(texture<0,1,-1,0,0,0,0,a>,(texture<1,125,125,0,0,0,0,rgb>*1.25),texture<2,100,100,0,0,0,0,rgb>)

%EOF