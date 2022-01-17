#version 330

in vec4 Color;
in vec2 TexCoords;
out vec4 FragColor;

uniform bool has_color, texture2D_enabled;
uniform sampler2D texture2D_sampler;

void main() {
    FragColor = vec4(1.0);
    if (has_color) {
        FragColor = Color;
    }
    if (texture2D_enabled) {
        FragColor *= texture(texture2D_sampler, TexCoords);
    }
}