name=portent beam

mipmaps=true
unlit=true
castshadows=false

texture<0>=scenario/mp/portent/texture/portent beam.png
texture<1>=multipurpose/texture/multi_difference.png

diffuse=texture<0,1,-1,0,0,0,0,0,0,rgb> * (diff(texture<1,0.33,-0.33,0,0,-0.0006,-0.002,0,0,r>, texture<1,0.33,-0.33,0,0,0.003,0.001,0,0,g>) * diff(texture<1,0.33,-0.33,0,0,-0.0006,-0.002,0,0,r>, texture<1,0.33,-0.33,0,0,0.003,0.001,0,0,g>))
illumination=texture<0,0.33,-0.33,0,0,0,0,0,0,g>
transparency=texture<0,0.33,-0.33,0,0,0,0,0,0,a> * (diff(texture<1,0.33,-0.33,0,0,-0.0006,-0.002,0,0,r>, texture<1,0.33,-0.33,0,0,0.003,0.001,0,0,g>) * diff(texture<1,0.33,-0.33,0,0,-0.0006,-0.002,0,0,r>, texture<1,0.33,-0.33,0,0,0.003,0.001,0,0,g>))

%EOF