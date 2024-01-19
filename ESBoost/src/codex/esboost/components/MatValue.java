/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.jme3.shader.VarType;
import com.simsilica.es.EntityComponent;

/**
 * Sets a material parameter for all geometries under the entity's
 * root model, if it exists.
 * <p>
 * To clear a material parameter instead of setting it, the value
 * should be set to null.
 * 
 * @author codex
 */
public class MatValue implements EntityComponent {
    
    private final String name;
    private final VarType type;
    private final Object value;
    
    /**
     * Creates a MatValue that clears the named parameter.
     * 
     * @param name name of the parameter (not null)
     */
    public MatValue(String name) {
        this(name, null, null);
    }
    /**
     * Creates a MatValue that sets the named parameter according to the arguments.
     * 
     * @param name name of the parameter (not null)
     * @param type data type of the parameter
     * @param value value to assign to the parameter
     */
    public MatValue(String name, VarType type, Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
        if (this.type == null && this.value != null) {
            throw new NullPointerException("Type cannot be null if value is not null.");
        }
    }

    public String getName() {
        return name;
    }
    public VarType getType() {
        return type;
    }
    public Object getValue() {
        return value;
    }
    
}
