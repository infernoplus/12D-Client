name=deathisland beach

mipmaps=true
unlit=false

texture<0>=graphics/texture/bsp/halo/deathbeach.png
texture<1>=graphics/texture/bsp/halo/detail sand.png
texture<2>=graphics/texture/bsp/halo/detail grass.png
texture<3>=graphics/texture/bsp/halo/mud bump.png

diffuse=texture<0,1,-1,0,0,0,0,rgb>*lerp(texture<0,1,-1,0,0,0,0,a>,(texture<1,60,60,0,0,0,0,rgb>*1.25),texture<2,55,55,0,0,0,0,rgb>)
normal=texture<3,50,50,0,0,0,0,rgb>

%EOF