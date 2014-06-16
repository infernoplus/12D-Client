name=portent ground

mipmaps=true
castshadows=true
unlit=false
fog=true

texture<0>=scenario/mp/portent/texture/portent terrain.png
texture<1>=scenario/mp/portent/texture/portent detail.png
texture<2>=scenario/mp/portent/texture/portent dirt.png
texture<3>=scenario/mp/deathisland/texture/mud bump.png

diffuse=texture<0,1,-1,0,0,0,0,0,0,rgb> * lerp(texture<0,1,-1,0,0,0,0,0,0,a>, texture<2,75,-75,0,0,0,0,0,0,rgb>, texture<1,95,-95,0,0,0,0,0,0,rgb>)
normal=texture<3,70,-70,0,0,0,0,0,0,rgb>