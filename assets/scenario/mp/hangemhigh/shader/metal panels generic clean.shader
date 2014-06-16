name=metal panels generic clean

mipmaps=true
castshadows=true
unlit=false
fog=true

texture<0>=scenario/mp/hangemhigh/texture/metal panels generic ext clean.png
texture<1>=scenario/mp/hangemhigh/texture/metal flat generic bump.png
texture<2>=scenario/mp/hangemhigh/texture/detail metal sratches.png

diffuse=texture<0,1,-1,0,0,0,0,0,0,rgb> * texture<2,2,-2,0,0,0,0,0,0,rgb>
normal=texture<1,1,-1,0,0,0,0,0,0,rgb>
specular=texture<0,1,-1,0,0,0,0,0,0,a>