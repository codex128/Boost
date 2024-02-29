/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.render;

import com.jme3.light.Light;
import com.jme3.scene.Spatial;

/**
 * Wraps light along with the scene it is assigned to.
 * 
 * @author codex
 * @param <T>
 */
public class SceneLight <T extends Light> {
    
    protected final T light;
    protected Spatial scene;
    
    public SceneLight(T light) {
        this.light = light;
    }

    public void assign(Spatial scene) {
        if (this.scene == scene) return;
        if (this.scene != null) {
            detachFromScene(this.scene);
        }
        this.scene = scene;
        if (this.scene != null) {
            attachToScene(this.scene);
        }
    }
    protected void attachToScene(Spatial scene) {
        scene.addLight(light);
    }
    protected void detachFromScene(Spatial scene) {
        scene.removeLight(light);
    }
    
    public T getLight() {
        return light;
    }
    public Spatial getScene() {
        return scene;
    }
    
}
