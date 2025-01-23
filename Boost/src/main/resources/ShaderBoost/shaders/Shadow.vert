
#import "Common/ShaderLib/Instancing.glsllib"
#import "Common/ShaderLib/Skinning.glsllib"
#import "ShaderBoost/glsl/vertexShadows.glsllib"

attribute vec3 inPosition;
attribute vec3 inNormal;

void main(){
    
    vec4 modelSpacePos = vec4(inPosition, 1.0);
    
    #ifdef NUM_MORPH_TARGETS
        Morph_Compute(modelSpacePos);
    #endif
    #ifdef NUM_BONES
        Skinning_Compute(modelSpacePos);
    #endif
    
    gl_Position = TransformWorldViewProjection(modelSpacePos);
    
    computeShadows(gl_Position, modelSpacePos, inNormal);
    
}
