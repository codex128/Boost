/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost;

import com.jme3.material.Material;
import com.jme3.math.Transform;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class EntityModel {
    
    private final EntityId entity;
    private final Spatial model;
    private EntityId necro;
    private final Transform tempTransform = new Transform();

    public EntityModel(EntityId entity, Spatial model) {
        this.entity = entity;
        this.model = model;
    }
    
    public void updateTransform(EntityData ed) {
        if (necro == null) {
            EntityUtils.getWorldTransform(ed, entity, tempTransform);
            model.setLocalTransform(tempTransform);
        }
    }
    
    public void setNecro(EntityId necro) {
        this.necro = necro;
    }
    
    public EntityId getEntity() {
        return entity;
    }
    public Spatial getModel() {
        return model;
    }
    public EntityId getNecro() {
        return necro;
    }
    public Transform getTempTransform() {
        return tempTransform;
    }
    
}
