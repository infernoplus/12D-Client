name=testui shield bar fill

mipmaps=false
unlit=true
castshadows=false

texture<0>=graphics/texture/ui/testui/shieldbarfill.png
texture<1>=graphics/texture/multipurpose/multi_bar.png

diffuse=texture<0,1,1,0,0,0,0,0,0,rgb>
transparency=texture<0,1,1,0,0,0,0,0,0,a> * texture<1,0.5,1,-scalarA,0,0,0,0,0,r>


%EOF