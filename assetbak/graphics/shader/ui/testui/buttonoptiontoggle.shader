name=testui option toggle

mipmaps=false
unlit=true
castshadows=false

texture<0>=graphics/texture/ui/testui/labelokay.png
texture<1>=graphics/texture/ui/testui/labelbad.png

diffuse=(scalarC * texture<0,1,1,0,0,0,0,0,0,rgb>) + ((1 - scalarC) * texture<1,1,1,0,0,0,0,0,0,rgb>)
transparency=(scalarC * texture<0,1,1,0,0,0,0,0,0,a>) + ((1 - scalarC) * texture<1,1,1,0,0,0,0,0,0,a>)


%EOF