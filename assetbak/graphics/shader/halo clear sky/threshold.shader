name=threshold planet

mipmaps=true
castshadows=false
unlit=true
fog=false

texture<0>=graphics/texture/sky/halo clear sky/threshold clear afternoon.png

diffuse=texture<0,1,1,0,0,0,0,0,0,rgb>
transparency=texture<0,1,1,0,0,0,0,0,0,a> * scalarA

%EOF