name=testui health bar fill

mipmaps=false
unlit=true
castshadows=false

texture<0>=graphics/texture/ui/testui/healthbarfill.png
texture<1>=graphics/texture/multipurpose/multi_bar.png

diffuse=color(scalarB,scalarC,scalarD,1)
transparency=texture<0,1,1,0,0,0,0,0,0,a> * texture<1,0.5,1,-scalarA,0,0,0,0,0,r>


%EOF