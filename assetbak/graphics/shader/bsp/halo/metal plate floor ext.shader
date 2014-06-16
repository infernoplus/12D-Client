name=metal plate floor ext

mipmaps=true
unlit=false

texture<0>=graphics/texture/bsp/halo/metal plate floor ext.png
texture<1>=graphics/texture/bsp/halo/dirt splotch detail.png
texture<2>=graphics/texture/bsp/halo/metal plate floor bump.png

diffuse=texture<0,1,1,0,0,0,0,rgb> * texture<1,2.2,2.2,0,0,0,0,rgb>
normal=texture<2,1,1,0,0,0,0,rgb>
specular=texture<0,1,1,0,0,0,0,a>

%EOF