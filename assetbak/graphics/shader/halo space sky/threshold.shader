name=threshold planet space

mipmaps=true
castshadows=false
unlit=true
fog=false

texture<0>=graphics/texture/sky/halo space sky/threshold space.png

diffuse=texture<0,1,1,0,0,0,0,0,0,rgb>
transparency=texture<0,1,1,0,0,0,0,0,0,a> * scalarA

%EOF