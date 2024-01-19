/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.factories;

import codex.esboost.components.Position;
import codex.esboost.components.Rotation;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.sim.SimTime;

/**
 *
 * @author codex
 */
public interface Design extends Assembler<Design> {
    
    public EntityId create();    
    public EntityId create(EntityId customer);
    public EntityId create(Spatial customer);
    
    public void setEntityData(EntityData ed);
    public void setTime(SimTime time);    
    public EntityData getEntityData();    
    public SimTime getTime();
    public EntityId getMainEntity();
    
    public default void setPosition(Position position) {
        getEntityData().setComponent(getMainEntity(), position);
    }
    public default void setRotation(Rotation rotation) {
        getEntityData().setComponent(getMainEntity(), rotation);
    }
    
}
