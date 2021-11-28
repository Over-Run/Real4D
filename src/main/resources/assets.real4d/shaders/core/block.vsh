#version 330 core

layout(location = 0) in vec3 in_vert;
layout(location = 1) in vec4 in_color;
out vec4 out_color;

uniform mat4 proj;
uniform mat4 modelView;

void main() {
    gl_Position = proj * modelView * vec4(in_vert, 1.0);
    out_color = in_color;
}
