#version 330 core

in vec4 Color;
in vec2 TexCoords2D;

out vec4 FragColor;

uniform sampler2D texture2D_sampler;
uniform bool texture2D_enabled;

void main() {
    FragColor = Color;
    if (texture2D_enabled) {
        FragColor *= texture(texture2D_sampler, TexCoords2D);
    }
}