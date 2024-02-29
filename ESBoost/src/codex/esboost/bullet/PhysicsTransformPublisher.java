/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.bullet;

import codex.esboost.EntityUtils;
import codex.esboost.components.Parent;
import codex.esboost.components.Position;
import codex.esboost.components.Rotation;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
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
    private final Transform tempTransform = new Transform();
    private final Vector3f tempVec = new Vector3f();
    private final Quaternion tempQuat = new Quaternion();
    
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
        object.getPhysicsLocation(tempVec);
        object.getPhysicsRotation(tempQuat);
        if (parent != null) {
            // Physics object transform must be converted from world coordinates to local coordinates
            EntityUtils.getWorldTransform(ed, parent.getId(), tempTransform);
            ed.setComponents(object.getId(),
                new Position(tempVec.subtractLocal(tempTransform.getTranslation())),
                // Division of two quaternions: (q1) * (inverse of q2)
                new Rotation(tempQuat.multLocal(tempTransform.getRotation().inverseLocal()))
            );
        } else ed.setComponents(object.getId(),
            new Position(tempVec),
            new Rotation(tempQuat)
        );
    }
    @Override
    public void removed(EntityPhysicsObject object) {}
    
}
