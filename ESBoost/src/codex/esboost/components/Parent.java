/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class Parent implements EntityComponent {
    
    private final EntityId parent;
    private final boolean removeOnMiss;
    
    public Parent(EntityId parent) {
        this(parent, true);
    }
    public Parent(EntityId parent, boolean removeOnMiss) {
        this.parent = parent;
        this.removeOnMiss = removeOnMiss;
    }

    public EntityId getId() {
        return parent;
    }
    public boolean isRemoveOnMiss() {
        return removeOnMiss;
    }
    @Override
    public String toString() {
        return "Parent{" + parent + '}';
    }
    
}
