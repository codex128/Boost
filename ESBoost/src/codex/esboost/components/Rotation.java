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
public class Rotation implements EntityComponent {
    
    private final Quaternion rotation = new Quaternion();
    
    public Rotation() {}
    public Rotation(Quaternion rotation) {
        this.rotation.set(rotation);
    }
    public Rotation(Transform t) {
        t.getRotation(rotation);
    }
    public Rotation(float angle, Vector3f axis) {
        this.rotation.fromAngleAxis(angle, axis);
    }
    public Rotation(Vector3f lookAt, Vector3f up) {
        this.rotation.lookAt(lookAt, up);
    }
    public Rotation(float angleX, float angleY, float angleZ) {
        this.rotation.fromAngles(angleX, angleY, angleZ);
    }

    public Quaternion getRotation() {
        return rotation;
    }
    public Vector3f toDirection() {
        return rotation.mult(Vector3f.UNIT_Z);
    }
    public Rotation rotate(Quaternion rotate) {
        return new Rotation(rotation.mult(rotate));
    }
    @Override
    public String toString() {
        return "Rotation{" + rotation + '}';
    }
    
}
