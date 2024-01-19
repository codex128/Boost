/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class Scale implements EntityComponent {
    
    private final Vector3f scale = new Vector3f();
    
    public Scale(Vector3f scale) {
        this.scale.set(scale);
    }
    public Scale(float scalar) {
        this.scale.set(scalar, scalar, scalar);
    }
    public Scale(float x, float y, float z) {
        this.scale.set(x, y, z);
    }

    public Vector3f getScale() {
        return scale;
    }

    @Override
    public String toString() {
        return "Scale{" + "scale=" + scale + '}';
    }
    
}
