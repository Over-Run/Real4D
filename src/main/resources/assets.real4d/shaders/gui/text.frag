#version 330

in vec4 Color;
in vec2 TexCoords;
out vec4 FragColor;

uniform sampler2D texture2D_sampler;

void main() {
    FragColor = Color * texture(texture2D_sampler, TexCoords);
}