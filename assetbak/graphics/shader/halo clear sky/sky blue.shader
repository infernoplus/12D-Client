name=sky blue

mipmaps=true
castshadows=false
unlit=true
fog=false

texture<0>=graphics/texture/sky/halo clear sky/sky clear blue.png
texture<1>=graphics/texture/multipurpose/black.png
texture<2>=graphics/texture/sky/halo clear sky/stars.png
texture<3>=graphics/texture/sky/halo clear sky/detail star twinkle.png

diffuse=texture<0,1,1,0,0,0,0,0,0,rgb> + lerp(texture<0,1,1,0,0,0,0,0,0,b>,texture<1,1,1,0,0,0,0,0,0,rgb>,lerp(texture<2,4,4,0,0,0.00001,0.00002,0,0,r>, texture<3,5,5,0,0,0.00025,0.0001,0,0,rgb>, texture<1,1,1,0,0,0,0,0,0,rgb>))
transparency=scalarA

%EOF