/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.anim;

import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author codex
 */
public class RootMotionControl extends AbstractControl {
    
    private final String controlJointName;
    private SkinningControl skin;
    private Joint controlJoint;
    private Vector3f rootPosition = new Vector3f();
    private Vector3f delta = new Vector3f();
    private int freeze = 0;
    
    public RootMotionControl(String controlJoint) {
        this(controlJoint, null);
    }
    public RootMotionControl(String controlJoint, SkinningControl skin) {
        this.controlJointName = controlJoint;
        this.skin = skin;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        Vector3f current = getControlJointPosition(null);
        if (freeze > 0) {
            current.subtract(rootPosition, delta);
        }
        rootPosition.set(current);
        freeze--;
    }
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    @Override
    public void setSpatial(Spatial spat) {
        super.setSpatial(spat);
        if (spatial != null) {
            if (skin == null) {
                skin = spatial.getControl(SkinningControl.class);
            }
            controlJoint = skin.getArmature().getJoint(controlJointName);
            getControlJointPosition(rootPosition);
        }
    }
    
    public Vector3f getMotionDelta() {
        return delta;
    }
    public void freezeMotionDelta(int freeze) {
        this.freeze = freeze;
    }
    
    private Vector3f getControlJointPosition(Vector3f store) {
        if (store == null) {
            store = new Vector3f();
        }
        return store.set(controlJoint.getLocalTranslation());
    }
    
}
