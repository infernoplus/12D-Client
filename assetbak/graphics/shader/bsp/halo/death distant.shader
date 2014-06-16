name=deathisland water

mipmaps=true
castshadows=false
unlit=false

texture<0>=graphics/texture/bsp/halo/water color.png
texture<1>=graphics/texture/bsp/halo/crap water0.png
texture<2>=graphics/texture/bsp/halo/crap water1.png
texture<3>=graphics/texture/bsp/halo/waves bump.png

diffuse=((diff(texture<1,7,-7,0,0,-0.00006,-0.00008,r>,texture<2,10,-10,0,0,0.00012,0.00004,r>)+0.5)*0.33) * texture<0,5,-5,0,0,0.00012,0.00005,rgb>
specular=(diff(texture<1,7,-7,0,0,-0.00003,-0.00004,r>,texture<2,10,-10,0,0,0.00006,0.00002,r>)+0.5)*0.5
transparency=((diff(texture<1,7,-7,0,0,-0.00006,-0.00008,r>,texture<2,10,-10,0,0,0.00012,0.00004,r>)+0.5)*0.33)*texture<0,1,-1,0,0,0,0,a>

%EOF