name=halo ladder mp

mipmaps=true
unlit=false
castshadows=false

texture<0>=graphics/texture/bsp/halo/beaver ladder.png
texture<1>=graphics/texture/bsp/halo/detail metal sratches.png
texture<2>=graphics/texture/bsp/halo/beaver ladder bump.png

diffuse=texture<0,1,1,0,0,0,0,rgb> * texture<1,4.1,4.1,0,0,0,0,rgb>
normal=texture<2,1,1,0,0,0,0,rgb>
specular=texture<0,1,1,0,0,0,0,a>
transparency=texture<2,1,1,0,0,0,0,a>


%EOF