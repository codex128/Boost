/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost;

import com.jme3.material.Material;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class EntityMaterial {
    
    private final EntityId entityId;
    private final Material material;

    public EntityMaterial(EntityId entityId, Material material) {
        this.entityId = entityId;
        this.material = material;
    }

    public EntityId getEntityId() {
        return entityId;
    }
    public Material getMaterial() {
        return material;
    }
    
}
