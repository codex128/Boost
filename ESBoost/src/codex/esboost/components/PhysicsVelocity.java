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
public class PhysicsVelocity implements EntityComponent {
    
    private final Vector3f linear;
    private final Vector3f angular;

    public PhysicsVelocity(Vector3f linear, Vector3f angular) {
        this.linear = linear;
        this.angular = angular;
    }
    
    public Vector3f getLinear() {
        return linear;
    }
    public Vector3f getAngular() {
        return angular;
    }
    
}
