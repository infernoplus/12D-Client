name=deathisland cliffs

mipmaps=true
unlit=false

texture<0>=graphics/texture/bsp/halo/cliffs.png
texture<1>=graphics/texture/bsp/halo/detail cliff rock.png
texture<2>=graphics/texture/bsp/halo/cliff rock bump.png

diffuse=texture<0,1,-1,0,0,0,0,rgb>*texture<1,8,-6,0,0,0,0,rgb>
normal=texture<2,8,-6,0,0,0,0,rgb>*0.5
specular=texture<0,1,-1,0,0,0,0,r>

%EOF