/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.jme3.renderer.Camera;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class FieldOfView implements EntityComponent {
    
    public static final float PARALLEL = -1, CONFORM = 0;
    
    private final float fov;

    public FieldOfView() {
        this(CONFORM);
    }
    public FieldOfView(float fov) {
        this.fov = fov;
    }

    public float getFov() {
        return fov;
    }
    public FieldOfView apply(Camera cam) {
        if (fov == CONFORM) {
            return (cam.isParallelProjection() ? new FieldOfView(PARALLEL) : new FieldOfView(cam.getFov()));
        }
        if (fov > 0) {
            if (cam.isParallelProjection()) {
                cam.setParallelProjection(false);
            }
            cam.setFov(fov);
        } else if (!cam.isParallelProjection()) {
            cam.setParallelProjection(true);
        }
        return null;
    }

    @Override
    public String toString() {
        return "FieldOfView{" + "fov=" + fov + '}';
    }
    
}
