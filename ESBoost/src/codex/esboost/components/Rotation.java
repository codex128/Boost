/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class Rotation implements EntityComponent {
    
    public static final Rotation ZERO = new Rotation();
    
    private final Quaternion rotation = new Quaternion();
    
    public Rotation() {}
    public Rotation(Quaternion rotation) {
        this.rotation.set(rotation);
    }
    public Rotation(Matrix3f matrix) {
        this.rotation.apply(matrix);
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
    public Rotation(Vector3f lookAt) {
        this.rotation.lookAt(lookAt, Vector3f.UNIT_Y);
    }
    public Rotation(float angleX, float angleY, float angleZ) {
        this.rotation.fromAngles(angleX, angleY, angleZ);
    }

    public Quaternion getRotation() {
        return rotation;
    }
    public Quaternion getRotation(Quaternion store) {
        if (store == null) {
            return new Quaternion(rotation);
        } else {
            return store.set(rotation);
        }
    }
    
    public Vector3f toDirection() {
        return rotation.getRotationColumn(2);
    }
    public Vector3f toDirection(Vector3f store) {
        return rotation.getRotationColumn(2, store);
    }
    
    public Matrix3f toMatrix() {
        return rotation.toRotationMatrix();
    }
    public Matrix3f toMatrix(Matrix3f store) {
        return rotation.toRotationMatrix(store);
    }
    
    public Vector3f getColumn(int column) {
        return rotation.getRotationColumn(column);
    }
    public Vector3f getColumn(int column, Vector3f store) {
        return rotation.getRotationColumn(column, store);
    }
    
    public Rotation rotate(Quaternion rotate) {
        return new Rotation(rotation.mult(rotate));
    }
    
    @Override
    public String toString() {
        return "Rotation{" + rotation + '}';
    }
    
}
