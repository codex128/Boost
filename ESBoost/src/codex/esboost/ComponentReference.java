/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;

/**
 * 
 * @author codex
 * @param <T>
 */
public class ComponentReference <T extends EntityComponent> {
    
    private final Entity entity;
    private final Class<T> type;
    private T component;

    public ComponentReference(Entity entity, Class<T> type) {
        this.entity = entity;
        this.type = type;
    }

    public boolean update() {
        boolean updated = needsUpdate();
        component = entity.get(type);
        return updated;
    }
    public boolean needsUpdate() {
        return component == null || !component.equals(entity.get(type));
    }
    
    public Entity getEntity() {
        return entity;
    }
    public Class<T> getType() {
        return type;
    }
    public T getComponent() {
        return component;
    }
    
}
