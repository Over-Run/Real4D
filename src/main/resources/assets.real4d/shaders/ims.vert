#version 330

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTexCoords;
out vec4 Color;
out vec2 TexCoords;

uniform mat4 proj, modelView;

void main() {
    Color = aColor;
    TexCoords = aTexCoords;
    gl_Position = proj * modelView * vec4(aPos, 1.0);
}