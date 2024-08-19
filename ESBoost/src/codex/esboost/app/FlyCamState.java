/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.EntityUtils;
import codex.esboost.components.Position;
import codex.esboost.components.Rotation;
import com.jme3.app.Application;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class FlyCamState extends GameAppState implements AnalogListener {

    private static final String[] mappings = {
        "flyCam[forward]",
        "flyCam[backward]",
        "flyCam[left]",
        "flyCam[right]",
        "flyCam[down]",
        "flyCam[up]",
        "flyCam[rotatex+]",
        "flyCam[rotatey+]",
        "flyCam[rotatex-]",
        "flyCam[rotatey-]",
    };
    
    private EntityData ed;
    private float speed = 20f;
    private float rotSpeed = 50f;
    private EntityId camera;
    
    @Override
    protected void init(Application app) {
        
        ed = EntityUtils.getEntityData(app);
        camera = getState(CameraState.class, true).getMainCamera();
        
        inputManager.addMapping(mappings[0], new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(mappings[1], new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(mappings[2], new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(mappings[3], new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(mappings[4], new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping(mappings[5], new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping(mappings[6], new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(mappings[7], new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(mappings[8], new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(mappings[9], new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addListener(this, mappings);
        
    }
    @Override
    protected void cleanup(Application app) {
        inputManager.removeListener(this);
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals(mappings[0])) {
            move(0, 0, 1, tpf);
        } else if (name.equals(mappings[1])) {
            move(0, 0, -1, tpf);
        } else if (name.equals(mappings[2])) {
            move(1, 0, 0, tpf);
        } else if (name.equals(mappings[3])) {
            move(-1, 0, 0, tpf);
        } else if (name.equals(mappings[4])) {
            move(0, -1, 0, tpf);
        } else if (name.equals(mappings[5])) {
            move(0, 1, 0, tpf);
        } else if (name.equals(mappings[6])) {
            rotate(1, rotSpeed*tpf*value);
        } else if (name.equals(mappings[7])) {
            rotate(0, rotSpeed*tpf*value);
        } else if (name.equals(mappings[8])) {
            rotate(1, -rotSpeed*tpf*value);
        } else if (name.equals(mappings[9])) {
            rotate(0, -rotSpeed*tpf*value);
        }
    }
    
    private void move(float x, float y, float z, float tpf) {
        Quaternion rot = ed.getComponent(camera, Rotation.class).getRotation();
        Vector3f out = new Vector3f();
        if (x != 0) {
            out.addLocal(rot.getRotationColumn(0).multLocal(x));
        }
        if (y != 0) {
            out.addLocal(rot.getRotationColumn(1).multLocal(y));
        }
        if (z != 0) {
            out.addLocal(rot.getRotationColumn(2).multLocal(z));
        }
        if (!out.equals(Vector3f.ZERO)) {
            out.normalizeLocal().multLocal(speed*tpf);
            ed.setComponent(camera, new Position(ed.getComponent(camera, Position.class).getPosition().add(out)));
        }
    }
    private void rotate(int axis, float angle) {
        Quaternion rot = ed.getComponent(camera, Rotation.class).getRotation();
        Vector3f forward = rot.getRotationColumn(2);
        Vector3f a = rot.getRotationColumn(axis);
        new Quaternion().fromAngleAxis(angle, a).mult(forward, forward);
        ed.setComponent(camera, new Rotation(forward, Vector3f.UNIT_Y));
    }
    
}
