name=m6d pistol fp

mipmaps=true
unlit=false

texture<0>=graphics/texture/item/weapon/m6dpistol/pistol.png
texture<1>=graphics/texture/item/weapon/m6dpistol/pistol multipurpose.png
cubemap<0>=graphics/texture/cubemap/halopistol.cubemap

diffuse=texture<0,1,-1,0,0,0,0,0,0,rgb> + (texture<1,1,-1,0,0,0,0,0,0,r> * cubemap<0,rgb>)
specular=texture<1,1,-1,0,0,0,0,0,0,r>

%EOF