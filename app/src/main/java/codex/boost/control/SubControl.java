/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.control;

import com.jme3.bullet.control.AbstractPhysicsControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * A control that relies on another control type controlling the same spatial.
 *
 * @author gary
 * @param <T>
 */
public abstract class SubControl <T extends Control> extends AbstractControl {

    protected final Class<T> type;
    protected T dependency;
    private boolean persistant = true;

    public SubControl(Class<T> type) {
        this.type = type;
    }

    public SubControl(Class<T> type, boolean persistant) {
        this.type = type;
        this.persistant = persistant;
    }

    public T getDependency() {
        return dependency;
    }

    public Class<T> getType() {
        return type;
    }

    public boolean hasDependency() {
        return dependency != null;
    }

    public boolean isPersistant() {
        return persistant;
    }

    /**
     * Enables this subcontrol to persistantly check for the dependency.
     *
     * @param persist
     */
    public void setAsPersistant(boolean persist) {
        persistant = persist;
    }

    /**
     * Captures the dependency if it is controlling the spatial.
     */
    public void captureDependency() {
        if (spatial == null || dependency != null) {
            return;
        }
        dependency = spatial.getControl(type);
        if (dependency != null) {
            onDependencyCaptured();
        }
    }

    /**
     * Release the dependency.
     */
    public void releaseDependency() {
        onDependencyReleased();
        dependency = null;
    }
    
    /**
     * Tests if the dependency is controlling the spatial.
     * @return 
     */
    public boolean dependencyActive() {
        if (dependency instanceof AbstractControl) {
            return ((AbstractControl)dependency).getSpatial() == spatial;
        }
        if (dependency instanceof AbstractPhysicsControl) {
            return ((AbstractPhysicsControl)dependency).getSpatial() == spatial;
        }
        if (persistant) {
            for (int i = 0; i < spatial.getNumControls(); i++) 
                if (spatial.getControl(i) == dependency) return true;
            return false;
        }
        return true;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (dependency != null) {
            if (!dependencyActive()) {
                releaseDependency();
                return;
            }
            subControlUpdate(tpf);
        }
        else if (persistant) {
            captureDependency();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        if (dependency != null) {
            subControlRender(rm, vp);
        }
    }

    @Override
    public void setSpatial(Spatial spat) {
        super.setSpatial(spat);
        if (spatial != null) {
            captureDependency();
        }
        else {
            releaseDependency();
        }
    }

    protected abstract void subControlUpdate(float tpf);

    protected abstract void subControlRender(RenderManager rm, ViewPort vp);

    /**
     * Called right after the dependency has been successfully captured.
     */
    protected abstract void onDependencyCaptured();

    /**
     * Called right before the dependency is to be released.
     */
    protected abstract void onDependencyReleased();

}
