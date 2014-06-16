name=metal strips narrow ext

mipmaps=true
castshadows=true
unlit=false
fog=true

texture<0>=scenario/mp/hangemhigh/texture/metal strips narrow ext.png
texture<1>=scenario/mp/hangemhigh/texture/metal strips narrow ext bump.png
texture<2>=scenario/mp/hangemhigh/texture/dirt splotch detail.png

diffuse=texture<0,1,-1,0,0,0,0,0,0,rgb> * texture<2,8,-2,0,0,0,0,0,0,rgb>
normal=texture<1,1,-1,0,0,0,0,0,0,rgb>
specular=texture<0,1,-1,0,0,0,0,0,0,a>