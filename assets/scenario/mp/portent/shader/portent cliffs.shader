name=portent cliffs

mipmaps=true
unlit=false

texture<0>=scenario/mp/portent/texture/icefields_cliff_walls.png
texture<1>=scenario/mp/portent/texture/detail cliff rock.png
texture<2>=scenario/mp/portent/texture/cliff rock bump.png

diffuse=texture<0,1,-1,0,0,0,0,0,0,rgb>*texture<1,16,-8,0,0,0,0,0,0,rgb>
normal=texture<2,16,-8,0,0,0,0,0,0,rgb>
specular=texture<0,1,-1,0,0,0,0,0,0,r>*0.5

%EOF
