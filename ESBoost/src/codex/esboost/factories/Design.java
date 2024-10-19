/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.factories;

import codex.esboost.components.Parent;
import codex.esboost.components.Position;
import codex.esboost.components.Rotation;
import codex.esboost.components.Scale;
import codex.esboost.components.SceneMember;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public interface Design {
    
    public void create();    
    public void create(EntityId customer);
    public void create(Spatial customer);
    
    public void setDesignTools(DesignTools tools);
    
    public DesignTools getDesignTools();
    public EntityId getMainEntity();
    
    public default void setPosition(Position position) {
        getDesignTools().ed.setComponent(getMainEntity(), position);
    }
    public default void setPosition(Vector3f position) {
        setPosition(new Position(position));
    }
    public default void setPosition(float x, float y, float z) {
        setPosition(new Position(x, y, z));
    }
    public default void setRotation(Rotation rotation) {
        getDesignTools().ed.setComponent(getMainEntity(), rotation);
    }
    public default void setScale(Scale scale) {
        getDesignTools().ed.setComponent(getMainEntity(), scale);
    }
    public default void setParent(Parent parent) {
        getDesignTools().ed.setComponent(getMainEntity(), parent);
    }
    public default void setParent(EntityId parent) {
        setParent(new Parent(parent));
    }
    public default void setScene(SceneMember scene) {
        getDesignTools().ed.setComponent(getMainEntity(), scene);
    }
    public default void setScene(EntityId scene) {
        setScene(new SceneMember(scene));
    }
    
}
