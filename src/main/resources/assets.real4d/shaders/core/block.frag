#version 330 core

in vec2 TexCoords;

out vec4 FragColor;

uniform sampler2D texture_sampler;

void main() {
    FragColor = texture(texture_sampler, TexCoords);
}