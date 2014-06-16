name=metal plate floor ext

mipmaps=true
castshadows=true
unlit=false
fog=true

texture<0>=scenario/mp/deathisland/texture/metal plate floor ext.png
texture<1>=scenario/mp/hangemhigh/texture/metal plate floor bump.png
texture<2>=scenario/mp/hangemhigh/texture/dirt splotch detail.png

diffuse=texture<0,1,-1,0,0,0,0,0,0,rgb> * texture<2,1.5,-1.5,0,0,0,0,0,0,rgb>
normal=texture<1,1,-1,0,0,0,0,0,0,rgb>
specular=texture<0,1,-1,0,0,0,0,0,0,a>