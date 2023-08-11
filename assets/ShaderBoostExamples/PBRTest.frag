
#import "ShaderBoost/PBR.glsllib"
#import "ShaderBoost/utils.glsllib"
#import "ShaderBoost/mapping.glsllib"
#import "ShaderBoost/textures.glsllib"

#ifdef DIFFUSE_MAP
    uniform sampler2D m_DiffuseMap;
#endif
#ifdef NORMAL_MAP
    uniform sampler2D m_NormalMap;
#endif
#ifdef PARALLAX
    uniform float m_ParallaxHeight;
#endif

uniform vec4 m_BaseColor;
uniform float m_Metallic;
uniform float m_Specular;
uniform float m_Roughness;

varying vec2 texCoord;
varying vec3 wPosition;
varying vec3 wNormal;
varying vec4 wTangent;

void main() {
    
    vec3 viewDir = getViewDirection(wPosition);
    
    // parallax
    #if (defined(NORMAL_MAP) && defined(PARALLAX))
        vec2 newTexCoord = steepParallax(m_NormalMap, viewDir, texCoord, wNormal, wTangent, m_ParallaxHeight, true);
    #else
        vec2 newTexCoord = texCoord;
    #endif
    
    // diffuse
    #ifdef DIFFUSE_MAP
        vec4 diffuse = texture2D(m_DiffuseMap, newTexCoord);
    #else
        vec4 diffuse = m_BaseColor;
    #endif
    
    // normal
    #ifdef NORMAL_MAP
        vec3 normal = normalMap(wNormal, wTangent, texture2D(m_NormalMap, newTexCoord));
    #else
        vec3 normal = wNormal;
    #endif    
    
    float checker = checkerTexture(newTexCoord, 5.0);
    //if (checker > 0.5) discard;
    
    // PBR
    vec4 color = physicallyBasedRender(wPosition, texture2D(m_DiffuseMap, texCoord), m_Metallic, vec4(m_Specular), m_Roughness, normal, wNormal);
    
    // fragment output
    gl_FragColor = color;
    
}
