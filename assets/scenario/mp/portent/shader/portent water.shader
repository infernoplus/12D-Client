name=portent water

mipmaps=true
castshadows=false
unlit=false
fog=true

texture<0>=scenario/mp/portent/texture/portent water.png
texture<1>=scenario/mp/portent/texture/crap water0.png
texture<2>=scenario/mp/portent/texture/crap water1.png
texture<3>=scenario/mp/portent/texture/water color.png

diffuse=((diff(texture<1,7,-7,0,0,-0.00006,-0.00008,0,0,r>,texture<2,10,-10,0,0,0.00012,0.00004,0,0,r>)+0.5)*0.33) * texture<3,5,-5,0,0,0.00012,0.00005,0,0,rgb>
specular=(diff(texture<1,7,-7,0,0,-0.00003,-0.00004,0,0,r>,texture<2,10,-10,0,0,0.00006,0.00002,0,0,r>)+0.5)*0.5
transparency=((diff(texture<1,7,-7,0,0,-0.00006,-0.00008,0,0,r>,texture<2,10,-10,0,0,0.00012,0.00004,0,0,r>)+0.5)*0.33)*texture<0,1,-1,0,0,0,0,0,0,a>