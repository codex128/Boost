/*
 * Copyright (c) 2024, codex
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package codex.boost.material;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.TechniqueDef;
import com.jme3.material.logic.DefaultTechniqueDefLogic;
import com.jme3.material.logic.MultiPassLightingLogic;
import com.jme3.material.logic.SinglePassAndImageBasedLightingLogic;
import com.jme3.material.logic.SinglePassLightingLogic;
import com.jme3.material.logic.StaticPassLightingLogic;
import com.jme3.material.logic.TechniqueDefLogic;
import com.jme3.shader.DefineList;
import com.jme3.shader.Shader;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import com.jme3.texture.image.ColorSpace;
import com.jme3.util.clone.Cloner;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Facilitates matdef creation with code.
 * 
 * @author codex
 */
public class ImmediateMatDef extends MaterialDef {
    
    public ImmediateMatDef(AssetManager assetManager, String name) {
        super(assetManager, name);
    }
    
    public ImmediateMatDef addParam(VarType type, String name) {
        return addParam(type, name, null);
    }
    public ImmediateMatDef addParam(VarType type, String name, Object defaultValue) {
        if (type.isTextureType()) {
            addMaterialParamTexture(type, name, ColorSpace.Linear, (Texture)defaultValue);
        } else {
            addMaterialParam(type, name, defaultValue);
        }
        return this;
    }
    public ImmediateMatDef addParam(VarType type, String name, ColorSpace space) {
        return addParam(type, name, space, null);
    }
    public ImmediateMatDef addParam(VarType type, String name, ColorSpace space, Texture defaultTexture) {
        if (!type.isTextureType()) {
            throw new IllegalArgumentException("Specified type must be a texture.");
        }
        addMaterialParamTexture(type, name, space, defaultTexture);
        return this;
    }
    
    public TechniqueAdapter createTechnique() {
        return createTechnique(TechniqueDef.DEFAULT_TECHNIQUE_NAME);
    }
    public TechniqueAdapter createTechnique(String name) {
        return createTechnique(this, name);
    }
    public Material createMaterial() {
        return new Material(this);
    }
    
    public static TechniqueAdapter createTechnique(MaterialDef matdef, String name) {
        return new TechniqueAdapter(matdef, name);
    }
    
    public static class TechniqueAdapter {
        
        private final MaterialDef matdef;
        private final TechniqueDef techdef;
        private final EnumMap<Shader.ShaderType, String> shaderNames = new EnumMap<>(Shader.ShaderType.class);
        private final ArrayList<EnumMap<Shader.ShaderType, String>> shaderVersions = new ArrayList<>(4);
        private final LinkedList<String> presetDefines = new LinkedList<>();
        private int[] defShaderVersions;
        
        private TechniqueAdapter(MaterialDef matdef, String name) {
            this.matdef = matdef;
            this.techdef = new TechniqueDef(name, createSortId(this.matdef.getName(), name));
        }
        
        public List<TechniqueDef> add() {
            if (shaderNames.isEmpty()) {
                throw new IllegalStateException("No shaders specified.");
            }
            if (techdef.getLogic() == null) {
                setLogic(TechniqueDef.LightMode.Disable);
            }
            techdef.setShaderPrologue(createShaderPrologue(presetDefines));
            Cloner cloner = new Cloner();
            LinkedList<TechniqueDef> result = new LinkedList<>();
            for (int i = 1; i < shaderVersions.size(); i++) {
                cloner.clearIndex();
                TechniqueDef td = cloner.clone(techdef);
                td.setShaderFile(shaderNames, shaderVersions.get(i));
                matdef.addTechniqueDef(td);
                result.add(td);
            }
            techdef.setShaderFile(shaderNames, shaderVersions.get(0));
            matdef.addTechniqueDef(techdef);
            result.add(techdef);
            return result;
        }
        
        public TechniqueAdapter setVersions(int... versions) {
            if (versions.length == 0) {
                throw new IllegalArgumentException("Must specify at least one version.");
            }
            defShaderVersions = versions;
            return this;
        }
        
        public TechniqueAdapter setShader(ImmediateShader shader, int... versions) {
            if (shader.isExtension()) {
                throw new IllegalArgumentException("Shader cannot be an extension type.");
            }
            return setShader(shader.getType(), shader.getCode(), versions);
        }
        public TechniqueAdapter setShader(Shader.ShaderType type, String name, int... versions) {
            shaderNames.put(type, name);
            if (versions.length == 0) {
                versions = Objects.requireNonNull(defShaderVersions, "Shader versions not defined.");
            }
            for (int i = 0; i < versions.length; i++) {
                if (i >= shaderVersions.size()) {
                    shaderVersions.add(new EnumMap<>(Shader.ShaderType.class));
                }
                shaderVersions.get(i).put(type, formatVersion(versions[i]));
            }
            return this;
        }
        public TechniqueAdapter setVertexShader(String name, int... versions) {
            return setShader(Shader.ShaderType.Vertex, name, versions);
        }
        public TechniqueAdapter setFragmentShader(String name, int... versions) {
            return setShader(Shader.ShaderType.Fragment, name, versions);
        }
        public TechniqueAdapter setGeometryShader(String name, int... versions) {
            return setShader(Shader.ShaderType.Geometry, name, versions);
        }
        public TechniqueAdapter setTessellationControlShader(String name, int... versions) {
            return setShader(Shader.ShaderType.TessellationControl, name, versions);
        }
        public TechniqueAdapter setTessallationEvaluationShader(String name, int... versions) {
            return setShader(Shader.ShaderType.TessellationEvaluation, name, versions);
        }
        
        public TechniqueAdapter addWorldParameters(String... parameters) {
            for (String p : parameters) {
                techdef.addWorldParam(p);
            }
            return this;
        }
        public TechniqueAdapter addDefine(String define, String parameter) {
            techdef.addShaderParamDefine(parameter, matdef.getMaterialParam(parameter).getVarType(), define);
            return this;
        }
        public TechniqueAdapter addDefine(String define) {
            presetDefines.add(define);
            return this;
        }
        
        public TechniqueAdapter setLightMode(TechniqueDef.LightMode mode) {
            return setLightMode(mode, true);
        }
        public TechniqueAdapter setLightMode(TechniqueDef.LightMode mode, boolean createLogic) {
            techdef.setLightMode(mode);
            if (createLogic) {
                setLogic(mode);
            }
            return this;
        }
        public TechniqueAdapter setLogic(Function<TechniqueDef, TechniqueDefLogic> function) {
            return setLogic(function.apply(techdef));
        }
        public TechniqueAdapter setLogic(TechniqueDefLogic logic) {
            techdef.setLogic(logic);
            return this;
        }
        public TechniqueAdapter setLogic(TechniqueDef.LightMode mode) {
            switch (techdef.getLightMode()) {
                case MultiPass: setLogic(new MultiPassLightingLogic(techdef)); break;
                case SinglePass: setLogic(new SinglePassLightingLogic(techdef)); break;
                case StaticPass: setLogic(new StaticPassLightingLogic(techdef)); break;
                case SinglePassAndImageBased: setLogic(new SinglePassAndImageBasedLightingLogic(techdef)); break;
                default: setLogic(new DefaultTechniqueDefLogic(techdef)); break;
            }
            return this;
        }
        
        public TechniqueAdapter setShadowMode(TechniqueDef.ShadowMode mode) {
            techdef.setShadowMode(mode);
            return this;
        }
        public TechniqueAdapter setLightSpace(TechniqueDef.LightSpace space) {
            techdef.setLightSpace(space);
            return this;
        }
        
        private String formatVersion(int version) {
            return "GLSL" + version;
        }
        private int createSortId(String matdefName, String techniqueName) {
            return (matdefName + "@" + techniqueName).hashCode();
        }
        private String createShaderPrologue(List<String> presetDefines) {
            DefineList dl = new DefineList(presetDefines.size());
            for (int i = 0; i < presetDefines.size(); i++) {
                dl.set(i, 1);
            }
            StringBuilder sb = new StringBuilder();
            dl.generateSource(sb, presetDefines, null);
            return sb.toString();
        }
        
    }
    
}
