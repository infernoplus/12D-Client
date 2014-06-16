name=blue holo lights

mipmaps=true
unlit=true
castshadows=false

texture<0>=graphics/texture/bsp/halo/mp lights small strips.png
texture<1>=graphics/texture/multipurpose/multi_difference.png

diffuse=texture<0,1,-1,0,0,0,0,rgb> * (1.0 + ((texture<1,1,-1,0,0,0.0006,0.001,r> * texture<1,1,-1,0,0,-0.002,-0.0015,g>) * 0.75))
illumination=texture<0,1,-1,0,0,0,0,rgb>
transparency=texture<0,1,-1,0,0,0,0,b>

%EOF