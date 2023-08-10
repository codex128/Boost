
#import "ShaderLibrary/shading.glsllib"
#import "ShaderLibrary/textures.glsllib"
#import "ShaderLibrary/math.glsllib"
#import "ShaderLibrary/mapping.glsllib"

#ifdef DIFFUSE_MAP
    uniform sampler2D m_DiffuseMap;
#endif
#ifdef NORMAL_MAP
    uniform sampler2D m_NormalMap;
    varying vec4 wTangent;
#endif

uniform float m_Metallic;
uniform float m_Specular;
uniform float m_Roughness;

varying vec4 baseColor;
varying vec2 texCoord;
varying vec3 wPosition;
varying vec3 wNormal;

void main() {
    
    #ifdef NORMAL_MAP
        vec2 newTexCoord = parallax(
    #endif
    
    #ifdef DIFFUSE_MAP
        vec4 diffuse = texture2D(m_DiffuseMap, texCoord);
    #else
        vec4 diffuse = baseColor;
    #endif
    
    #ifdef NORMAL_MAP
        vec3 normal = normalMap(wNormal, wTangent, texture2D(m_NormalMap, texCoord));
    #else
        vec3 normal = wNormal;
    #endif
    
    vec4 color = physicallyBasedRender(wPosition, diffuse, m_Metallic, m_Specular, m_Roughness, normal, wNormal);
    
    color += lightProbes(wPosition, normal, wNormal, m_Roughness, 
    
}
