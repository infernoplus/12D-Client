name=teleporter shield

mipmaps=true
unlit=true
castshadows=false

texture<0>=graphics/texture/bsp/halo/teleporter_mask.png
texture<1>=graphics/texture/multipurpose/multi_difference.png

diffuse=(texture<0,1,-1,0,0,0,0,a> * texture<0,1,-1,0,0,0,0,rgb>) * (diff(texture<1,1,-1,0,0,-0.0006,-0.002,r>, texture<1,1,-1,0,0,0.003,0.001,g>) * diff(texture<1,1,-1,0,0,-0.0006,-0.002,r>, texture<1,1,-1,0,0,0.003,0.001,g>))
illumination=texture<0,1,-1,0,0,0,0,g>
transparency=texture<0,1,-1,0,0,0,0,a> * (diff(texture<1,1,-1,0,0,-0.0006,-0.002,r>, texture<1,1,-1,0,0,0.003,0.001,g>) * diff(texture<1,1,-1,0,0,-0.0006,-0.002,r>, texture<1,1,-1,0,0,0.003,0.001,g>))

%EOF