#version 330 core

layout (location = 0) vec3 aPos;
layout (location = 1) vec4 aColor;
layout (location = 2) vec2 aTexCoords2D;

out vec4 Color;
out vec2 TexCoords2D;

uniform mat4 proj, modelView;

void main() {
    Color = aColor;
    TexCoords2D = aTexCoords2D;
    gl_Position = proj * modelView * vec4(aPos, 1.0);
}