/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.control;

import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * Controls geometry.
 *
 * @author gary
 */
public abstract class GeometryControl extends AbstractControl {

    protected Geometry geometry;

    @Override
    public void setSpatial(Spatial spat) {
        super.setSpatial(spat);
        if (spatial == null) {
            geometry = null;
        }
        else if (spatial instanceof Geometry) {
            geometry = (Geometry)spatial;
        }
        else {
            throw new IllegalArgumentException("Control can only control geometry!");
        }
    }

    public Geometry getGeometry() {
        return geometry;
    }

}
