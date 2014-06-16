name=portent sky

mipmaps=true
castshadows=false
unlit=true
fog=false

texture<0>=sky/portent sky/texture/portent sky.png
texture<1>=sky/portent sky/texture/portent sky mask.png
texture<2>=sky/halo clear sky/texture/stars.png
texture<3>=sky/halo clear sky/texture/detail star twinkle.png
texture<4>=multipurpose/texture/black.png

diffuse=texture<0,1,-1,0,0,0,0,0,0,rgb> + (texture<1,1,1,0,0,0,0,0,0,b> * lerp(texture<2,5,-5,0,0,0,0,0,0,b>, texture<3,3,3,0,0,0.0002,0.00028,0,0,rgb>, texture<4,1,1,0,0,0,0,0,0,rgb>) * 2)
