name=space stars

mipmaps=true
castshadows=false
unlit=true
fog=false

texture<0>=graphics/texture/sky/halo space sky/space.png
texture<1>=graphics/texture/sky/halo space sky/star mask.png
texture<2>=graphics/texture/sky/halo space sky/stars.png

diffuse=texture<0,1,1,0,0,0,0,0,0,rgb> + pow(lerp(texture<2,5,5,0,0,-0.000011,-0.000023,0,0,r>, texture<1,2,2,0,0,0.00013,0.00032,0,0,rgb>, color(0,0,0,0)) * 2, 2)
transparency=scalarA

%EOF