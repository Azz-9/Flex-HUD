#version 330

in vec4 vertexColor;

out vec4 fragColor;

vec3 hsvToRgb(float hue) {
    float h = fract(hue) * 6.0;

    float x = 1.0 - abs(mod(h, 2.0) - 1.0);

    if (h < 1.0) {
        return vec3(1.0, x, 0.0);
    } else if (h < 2.0) {
        return vec3(x, 1.0, 0.0);
    } else if (h < 3.0) {
        return vec3(0.0, 1.0, x);
    } else if (h < 4.0) {
        return vec3(0.0, x, 1.0);
    } else if (h < 5.0) {
        return vec3(x, 0.0, 1.0);
    } else {
        return vec3(1.0, 0.0, x);
    }
}

void main() {
    fragColor = vec4(hsvToRgb(vertexColor.r), 1.0);
}