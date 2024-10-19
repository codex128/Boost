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

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import com.jme3.shader.Shader;
import com.jme3.shader.plugins.ShaderAssetKey;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 *
 * @author codex
 */
public class ImmediateShader implements AssetLocator {
    
    public static final String PREFIX = ImmediateShader.class.getName() + ":";
    
    private final Shader.ShaderType type;
    private final StringBuilder code = new StringBuilder();
    private final boolean prettyCode;
    private String tab = "    ";
    private int indent = 0;
    private int defs = 0;
    
    public ImmediateShader() {
        this(null, false);
    }
    public ImmediateShader(Shader.ShaderType type, boolean prettyCode) {
        this.type = type;
        this.prettyCode = prettyCode;
    }
    
    public ImmediateShader line(Object... code) {
        if (prettyCode) for (int i = 0; i < indent; i++) {
            this.code.append(tab);
        }
        append(code);
        this.code.append('\n');
        return this;
    }
    public ImmediateShader append(Object... code) {
        for (int i = 0; i < code.length; i++) {
            this.code.append(code[i]);
        }
        return this;
    }
    public ImmediateShader blank() {
        this.code.append('\n');
        return this;
    }
    
    public ImmediateShader include(String asset) {
        return line("#import \"", asset, "\"");
    }
    public ImmediateShader includeGlslCompat() {
        return include("Common/ShaderLib/GLSLCompat.glsllib");
    }
    public ImmediateShader includeInstancing() {
        return include("Common/ShaderLib/Instancing.glsllib");
    }
    public ImmediateShader includeSkinning() {
        return include("Common/ShaderLib/Skinning.glsllib");
    }
    public ImmediateShader includeMorphing() {
        return include("Common/ShaderLib/MorphAnim.glsllib");
    }
    public ImmediateShader extension(String name) {
        return extension(name, true);
    }
    public ImmediateShader extension(String name, boolean enable) {
        return line("#extension ", name, " : ", (enable ? "enable" : "disable"));
    }
    
    public ImmediateShader attribute(String type, String name) {
        return line("attribute ", type, ' ', name, ';');
    }
    public ImmediateShader uniform(String type, String rawName) {
        return line("uniform ", type, ' ', rawName, ';');
    }
    public ImmediateShader uniform(String type, String name, boolean global) {
        return uniform(type, (global ? "g_" : "m_") + name);
    }
    public ImmediateShader varying(String type, String name) {
        return line("varying ", type, ' ', name, ';');
    }
    public ImmediateShader constant(String type, String name, String value) {
        return line("const ", type, ' ', name, " = ", value, ';');
    }
    
    public ImmediateShader main() {
        return function("void", "main");
    }
    public ImmediateShader function(String returnType, String name, String... args) {
        line(returnType, ' ', name, '(');
        for (int i = 0; i < args.length; i++) {
            code.append(args[i]);
            if (i < args.length-1) {
                code.append(", ");
            }
        }
        code.append(") {");
        indent++;
        return this;
    }
    public ImmediateShader _return(String code) {
        return line("return ", code);
    }
    public ImmediateShader _for(String declare, String criteria, String iteration) {
        indent++;
        return line("for (", declare, "; ", criteria, "; ", iteration, ") {");
    }
    public ImmediateShader _while(String criteria) {
        indent++;
        return line("while (", criteria, ") {");
    }
    public ImmediateShader _if(String criteria) {
        indent++;
        return line("if (", criteria, ") {");
    }
    public ImmediateShader _elseif(String criteria) {
        indent--;
        line("} else if (", criteria, ") {");
        indent++;
        return this;
    }
    public ImmediateShader _else() {
        indent--;
        line("} else {");
        indent++;
        return this;
    }
    public ImmediateShader end() {
        if (--indent < 0) {
            throw new IllegalStateException("Missing '{'");
        }
        return line("}");
    }
    
    public ImmediateShader call(String funcName, String... args) {
        this.code.append(funcName).append('(');
        for (int i = 0; i < args.length; i++) {
            this.code.append(args[i]);
            if (i < args.length-1) {
                this.code.append(',');
            }
        }
        this.code.append(");");
        return this;
    }
    
    public ImmediateShader define(String name) {
        return line("#define ", name);
    }
    public ImmediateShader define(String name, String value) {
        return line("#define ", name, ' ', value);
    }
    public ImmediateShader ifdef(String name) {
        defs++;
        return line("#ifdef ", name);
    }
    public ImmediateShader elseifdef(String name) {
        return line("#elif ", name);
    }
    public ImmediateShader elsedef() {
        return line("#else");
    }
    public ImmediateShader endif() {
        if (--defs < 0) {
            throw new IllegalStateException("Missing #ifdef");
        }
        return line("#endif");
    }
    
    public ImmediateShader assign(String type, String name, String value) {
        return line(type, ' ', name, " = ", value, ';');
    }
    public ImmediateShader assign(String name, String value) {
        return line(name, " = ", value, ';');
    }
    
    public String version() {
        return "__VERSION__";
    }
    public String vec2(float x, float y) {
        return concat("vec2(", x, ',', y, ')');
    }
    public String vec3(float x, float y, float z) {
        return concat("vec3(", x, ',', y, 'y', z, ')');
    }
    public String vec4(float x, float y, float z, float w) {
        return concat("vec4(", x, ',', y, ',', z, ',', w, ')');
    }
    public String ivec2(int x, int y) {
        return concat("ivec2(", x, ',', y, ')');
    }
    public String ivec3(int x, int y, int z) {
        return concat("ivec3(", x, ',', y, 'y', z, ')');
    }
    public String ivec4(int x, int y, int z, int w) {
        return concat("ivec4(", x, ',', y, ',', z, ',', w, ')');
    }
    
    public ImmediateShader setTab(String tab) {
        this.tab = tab;
        return this;
    }
    
    public String getCode() {
        if (indent > 0) {
            throw new IllegalStateException("Unclosed statement");
        }
        if (defs > 0) {
            throw new IllegalStateException("Unclosed #ifdef");
        }
        return toString();
    }
    public Shader.ShaderType getType() {
        return type;
    }
    public String getTab() {
        return tab;
    }
    public boolean isExtension() {
        return type == null;
    }
    
    private String concat(Object... objects) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            builder.append(objects[i]);
        }
        return builder.toString();
    }
    
    @Override
    public String toString() {
        String extension = "glsllib";
        if (type != null) {
            extension = type.getExtension();
        }
        return prepareShaderCode(code.toString(), extension);
    }
    @Override
    public void setRootPath(String rootPath) {}
    @Override
    public AssetInfo locate(AssetManager manager, AssetKey key) {
        String name = key.getName();
        if (name.startsWith(PREFIX)) {
            name = name.substring(name.indexOf(':') + 1, name.lastIndexOf('.'));
            return new ShaderInfo(manager, key, name);
        } else {
            return null;
        }
    }
    
    public static String prepareShaderCode(String code, String extension) {
        return PREFIX + code + '.' + extension;
    }
    
    private static class ShaderInfo extends AssetInfo {
        
        private final String code;
        
        public ShaderInfo(AssetManager manager, AssetKey key, String code) {
            super(manager, key);
            this.code = code;
        }

        @Override
        public InputStream openStream() {
            return new ByteArrayInputStream(code.getBytes());
        }
        
    }
    
}
