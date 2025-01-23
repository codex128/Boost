/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.material;

import com.jme3.material.Material;
import com.jme3.shader.VarType;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public class MatParamTransfer {
    
    private final LinkedList<Parameter> params = new LinkedList<>();
    
    public void add(String sourceName, String targetName, VarType type, Object defValue) {
        params.add(new Parameter(sourceName, targetName, type, defValue));
    }
    
    private static class Parameter {
        
        private final String sourceName;
        private final String targetName;
        private final VarType type;
        private final Object defValue;

        public Parameter(String sourceName, String targetName, VarType type, Object defValue) {
            this.sourceName = sourceName;
            this.targetName = targetName;
            this.type = type;
            this.defValue = defValue;
        }
        
        public void apply(Material source, Material target) {
            Object value = source.getParamValue(sourceName);
            if (value != null) {
                target.setParam(targetName, type, value);
            } else if (defValue != null) {
                target.setParam(targetName, type, defValue);
            } else {
                target.clearParam(targetName);
            }
        }
        
    }
    
}
