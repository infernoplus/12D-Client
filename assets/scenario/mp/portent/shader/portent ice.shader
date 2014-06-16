name=portent ice

mipmaps=true
castshadows=true
unlit=true
fog=true

texture<0>=scenario/mp/portent/texture/portent ice.png
texture<1>=scenario/mp/portent/texture/cliff rock bump.png

diffuse=texture<0,2,-2,0,0,0,0,0,0,rgb>
specular=texture<0,1,-1,0,0,0,0,0,0,b>
normal=texture<1,10,-10,0,0,0,0,0,0,rgb>*0.5