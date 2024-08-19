/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.bullet;

import codex.esboost.EntityUtils;
import codex.esboost.components.Parent;
import com.jme3.math.Transform;
import com.simsilica.bullet.EntityPhysicsObject;
import com.simsilica.bullet.PhysicsObjectListener;
import com.simsilica.es.EntityData;
import com.simsilica.sim.SimTime;

/**
 *
 * @author codex
 */
public class PhysicsTransformPublisher implements PhysicsObjectListener {
    
    private final EntityData ed;
    private final Transform physicsWorld = new Transform();
    private final Transform parentWorld = new Transform();
    private final Transform result = new Transform();
    
    public PhysicsTransformPublisher(EntityData ed) {
        this.ed = ed;
    }
    
    @Override
    public void startFrame(SimTime time) {}
    @Override
    public void endFrame() {}
    @Override
    public void added(EntityPhysicsObject object) {}
    @Override
    public void updated(EntityPhysicsObject object) {
        Parent parent = ed.getComponent(object.getId(), Parent.class);
        object.getPhysicsLocation(physicsWorld.getTranslation());
        object.getPhysicsRotation(physicsWorld.getRotation());
        if (parent != null) {
            // Physics object transform must be converted from world coordinates to local coordinates
            EntityUtils.getWorldTransform(ed, parent.getId(), parentWorld);
            EntityUtils.worldToLocal(physicsWorld, parentWorld, result);
            EntityUtils.setEntityTransform(ed, object.getId(), result, false);
        } else {
            EntityUtils.setEntityTransform(ed, object.getId(), physicsWorld, false);
        }
    }
    @Override
    public void removed(EntityPhysicsObject object) {}
    
}
