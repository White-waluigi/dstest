#version 330 core
  
in vec3 pos;
in vec4 spos;
in vec2 tex;
in vec4 col;

out vec4 FragColor;

uniform sampler2D ourTexture;
uniform vec4 idColor;

void main()
{


    FragColor =  texture(ourTexture,vec2(tex.x,-tex.y));
    
    if(FragColor.a<0.4 ){
    	discard;
    	}
    	
    FragColor.w=1;
 
}