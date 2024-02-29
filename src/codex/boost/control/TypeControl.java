/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.control;

import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author codex
 * @param <T>
 */
public abstract class TypeControl <T extends Spatial> extends AbstractControl {
    
    protected final Class<T> type;
    protected T tSpatial;
    
    public TypeControl(Class<T> type) {
        this.type = type;
    }

    @Override
    public void setSpatial(Spatial spat) {
        super.setSpatial(spat);
        if (spatial == null) {
            tSpatial = null;
        } else if (type.isAssignableFrom(spatial.getClass())) {
            tSpatial = (T)spatial;
        } else {
            throw new IllegalArgumentException("This control cannot control given type!");
        }
    }
    @Override
    public T getSpatial() {
        return tSpatial;
    }
    
}
