/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.bullet;

import codex.esboost.components.PhysicsVelocity;
import com.simsilica.bullet.EntityPhysicsObject;
import com.simsilica.bullet.EntityRigidBody;
import com.simsilica.bullet.PhysicsObjectListener;
import com.simsilica.es.EntityData;
import com.simsilica.sim.SimTime;

/**
 *
 * @author codex
 */
public class PhysicsVelocityPublisher implements PhysicsObjectListener {
    
    private final EntityData ed;

    public PhysicsVelocityPublisher(EntityData ed) {
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
        if (object instanceof EntityRigidBody) {
            EntityRigidBody body = (EntityRigidBody)object;
            ed.setComponent(object.getId(), new PhysicsVelocity(body.getLinearVelocity(), body.getAngularVelocity()));
        }
    }
    @Override
    public void removed(EntityPhysicsObject object) {
        ed.removeComponent(object.getId(), PhysicsVelocity.class);
    }
    
}
