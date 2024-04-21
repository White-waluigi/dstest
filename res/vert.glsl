#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;

// worldivew proj matrix

layout (std140) uniform matrices
{
    mat4 worldViewProj;

};
layout (std140) uniform test
{
    vec4 bla;

};
out vec3 pos;
out vec4 spos;
out vec2 tex;
out vec4 col;

void main()
{
    gl_Position = worldViewProj*vec4(aPos, 1.0);
    //gl_Position = vec4(aPos, 1.0);
    //gl_Position =gl_Position/gl_Position.w;
    spos=gl_Position;
    pos=aPos;
    tex=aTexCoord;
    col=vec4(1,0,0,0);
}