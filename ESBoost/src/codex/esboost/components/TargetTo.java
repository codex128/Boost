/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 * Sets this entity to target another entity.
 * 
 * @author codex
 */
public class TargetTo implements EntityComponent {
    
    private final EntityId target;

    public TargetTo(EntityId target) {
        this.target = target;
    }
    
    public EntityId getTargetId() {
        return target;
    }
    @Override
    public String toString() {
        return "TargetTo{" + "target=" + target + '}';
    }
    
}
