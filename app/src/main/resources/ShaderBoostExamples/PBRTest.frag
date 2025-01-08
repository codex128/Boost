
#import "ShaderBoost/glsl/PBR.glsllib"
#import "ShaderBoost/glsl/utils.glsllib"
#import "ShaderBoost/glsl/mapping.glsllib"
#import "ShaderBoost/glsl/textures.glsllib"
#import "ShaderBoost/glsl/emission.glsllib"

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
uniform float g_Time;

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
    
    float metallic = m_Metallic;
    float roughness = m_Roughness;
    
    float diff = dot(abs(normal - wNormal), wNormal);    
    float checker = checkerTexture(newTexCoord + vec2(g_Time * 0.1), 5.0 + diff * 0.01);
    if (checker > 0.5) {
        diffuse = mix(diffuse, vec4(1.0, 0.0, 0.0, 1.0), 0.8);
        metallic = .7f;
        roughness = .3f;
    }
    
    // PBR
    vec4 color = physicallyBasedRender(wPosition, diffuse, metallic, vec4(m_Specular), roughness, normal, wNormal);
    
    // fragment output
    gl_FragColor = color;
    
}
