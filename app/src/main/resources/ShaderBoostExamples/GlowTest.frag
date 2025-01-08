
#import "ShaderBoost/glsl/textures.glsllib"

uniform float g_Time;
varying vec2 texCoord;

void main() {
    
    //float diff = dot(abs(normal - wNormal), wNormal);    
    float checker = checkerTexture(texCoord + vec2(g_Time * 0.1), 5.0);
    if (checker > 0.5) {
        gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
    }
    else {
        gl_FragColor = vec4(0.0);
    }
    
}
