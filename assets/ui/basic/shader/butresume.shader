##Name of this material##
name=resume button

##Switches for shader features##
mipmaps=false
castshadows=false
unlit=true
fog=false


##texture<0> through texture<6> are valid textures to load in this material##
##cubemap<0> through cubemap<1> are valid cubemaps to load in this material##
##load texture in png format in the way shown below##
texture<0>=ui/basic/texture/butresume.png

##define channels for this material channels are: diffuse, normal, specular, illumination, and transparency##
##channels left undefined will not be compiled in this material (saves gpu time)##
##
##Calls to texture here are as follows texture<TEXTUREINDEX,U-MULTIPLIER,V-MULTIPLIER,U-OFFSET,V-OFFSET,U-ANIMATION,V-ANIMATION,TEXTURECHANNEL>##
##TEXTUREINDEX is the index of the texture loaded you want to use (texture<0> to texture<6> defined the above section##
##U-MULTIPLIER is horizontal scaling of the texture, this is a multiplier so 1 is default 0.5 is double size and 2 would be half size. ##
##V-MULTIPLIER is vertical scaling of the texture, this is a multiplier so 1 is default 0.5 is double size and 2 would be half size. ##
##U-OFFSET is horizontal offset of the texture, the size of the texure is 1 so 0.5 would center it and 0.75 would move it 3/4 of the way across.##
##V-OFFSET is vertical offset of the texture, the size of the texure is 1 so 0.5 would center it and 0.75 would move it 3/4 of the way up.##
##U-ANIMATION is the amount we should slide the texture horizontally each tick. A tick is defined by Game.stepTime. This is usually 1/30th of a second.##
##V-ANIMATION is the amount we should slide the texture vertically each tick. A tick is defined by Game.stepTime. This is usually 1/30th of a second.##
##TEXTURECHANNEL defines the channel to use. Valid arguements are (rgb, r, g, b, a, color, red, green, blue, alpha)##
##
##Calls to cubemap here are as follows cubemap<CUBEMAPINDEX,TEXTURECHANNEL>##
##
##Basic math can be used to add, subtract, multiply, and divide textures, as well as a few other special commands##
##Parenthesis can be used in standard mathematical form.##
##EXAMPLE: diffuse=texture<0,1,1,0,0,0,0,rgb> * texture<1,5,5,0,0,0,0,rgb>
##EXAMPLE: specular=texture<0,1,1,0,0,0,0,alpha>
##EXAMPLE: illumination=texture<3,1,1,0,0,0,0,color> * (texture<4,2,2,0,0,0.04,0.015,red> + texture<4,2,2,0,0,0.01,0.033,blue>)
##
##Linear interpolation (lerp): lerp(MASKTEXTURE, TEXTUREA, TEXTUREB): linear interpolate TEXTUREA and TEXTUREB, over the  defined r, g, b, or a of the MASKTEXTURE.
##EXAMPLE: diffuse=lerp(texture<0,1,1,0,0,0,0,alpha>, texture<1,4,4,0,0,0,0,rgb>, texture<2,3,3,0,0,0,0,rgb>)##
diffuse=texture<0,1,1,0,0,0,0,0,0,rgb>
transparency=texture<0,1,1,0,0,0,0,0,0,a>

%EOF
