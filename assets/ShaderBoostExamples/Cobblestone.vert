
#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"

attribute vec3 inPosition;
attribute vec3 inNormal;
attribute vec4 inTangent;
attribute vec2 inTexCoord;

varying vec3 wPosition;
varying vec3 wNormal;
varying vec4 wTangent;
varying vec2 texCoord;

void main() {
    
    vec4 modelSpacePos = vec4(inPosition, 1.0);
    vec3 modelSpaceNorm = inNormal;
    
    wPosition = TransformWorld(modelSpacePos).xyz;
    wNormal = TransformWorldNormal(modelSpaceNorm);
    wTangent = vec4(TransformWorldNormal(inTangent.xyz), inTangent.w);
    texCoord = inTexCoord;
    
    gl_Position = TransformWorldViewProjection(modelSpacePos);
    
}