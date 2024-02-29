/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class WorldTransform implements EntityComponent {
    
    private final Vector3f translation = new Vector3f();
    private final Quaternion rotation = new Quaternion();
    private final Vector3f scale = new Vector3f();

    public WorldTransform(Transform transform) {
        this(transform.getTranslation(), transform.getRotation(), transform.getScale());
    }
    public WorldTransform(Vector3f translation, Quaternion rotation, Vector3f scale) {
        this.translation.set(translation);
        this.rotation.set(rotation);
        this.scale.set(scale);
    }

    public Vector3f getTranslation() {
        return translation;
    }
    public Quaternion getRotation() {
        return rotation;
    }
    public Vector3f getScale() {
        return scale;
    }
    
    public Vector3f getTranslation(Vector3f store) {
        return store.set(translation);
    }
    public Quaternion getRotation(Quaternion store) {
        return store.set(rotation);
    }
    public Vector3f getScale(Vector3f store) {
        return store.set(scale);
    }
    public Transform toTransform(Transform store) {
        if (store == null) {
            store = new Transform();
        }
        store.setTranslation(translation);
        store.setRotation(rotation);
        store.setScale(scale);
        return store;
    }
    
}
