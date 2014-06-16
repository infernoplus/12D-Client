name=deathisland waves

mipmaps=true
castshadows=false
unlit=false

texture<0>=graphics/texture/bsp/halo/waves mask.png
texture<1>=graphics/texture/bsp/halo/waves.png
texture<2>=graphics/texture/bsp/halo/waves water detail.png

diffuse=texture<0,1,-1,0,0,0,00,rgb> * lerp(texture<0,1,-1,0,0,0,00,a>,texture<1,1,-1,0,0,0.003,0,rgb>,texture<2,1,-1,0,0,0.002,0,rgb>)
transparency=texture<0,1,-1,0,0,0,0,a>

%EOF