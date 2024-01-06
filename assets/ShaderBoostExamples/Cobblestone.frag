
#import "ShaderBoost/glsl/PBR.glsllib"
#import "ShaderBoost/glsl/mapping.glsllib"
#import "ShaderBoost/glsl/textures.glsllib"
#import "ShaderBoost/glsl/random.glsllib"
#import "ShaderBoost/glsl/utils.glsllib"

varying vec3 wPosition;
varying vec3 wNormal;
varying vec4 wTangent;
varying vec2 texCoord;

void main() {
    
    vec3 viewDir = getViewDirection(wPosition);
    
    vec3 cobble = cobblestoneTexture(texCoord, 5.0, 72.69);
    float factor = step(cobble.x, 0.9);
    vec2 id = cobble.yz;
    
    float noise = noiseTexture(texCoord, 10.0, 94.47);    
    vec2 newTexCoord = classicParallax(factor + noise * 0.1, viewDir, texCoord, wNormal, wTangent, 100.0);
    
    vec3 cobble2 = cobblestoneTexture(newTexCoord, 5.0, 72.69);
    
    if (factor > 0.5) {
        gl_FragColor = physicallyBasedRender(wPosition, vec4(1.0), 0.0, vec4(1.0), 1.0, wNormal);
    }
    else {
        gl_FragColor = physicallyBasedRender(wPosition, vec4(1.0), 0.0, vec4(1.0), 1.0, wNormal);
    }
    
}
