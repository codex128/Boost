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
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class FreeMotionCameraState extends GameAppState implements AnalogListener {

    private static final String
            FORWARD = "freemotioncam[forward]",
            BACKWARD = "freemotioncam[backward]",
            RIGHT = "freemotioncam[right]",
            LEFT = "freemotioncam[left]",
            UP = "freemotioncam[up]",
            DOWN = "freemotioncam[down]",
            ROTATE_XP = "freemotioncam[rotate_x+]",
            ROTATE_XN = "freemotioncam[rotate_x-]",
            ROTATE_YP = "freemotioncam[rotate_y+]",
            ROTATE_YN = "freemotioncam[rotate_y-]";
    
    private EntityData ed;
    private EntityId camera;
    private final Vector3f movementInput = new Vector3f();
    private final Vector2f rotationInput = new Vector2f();
    private final float movementSpeed, rotationSpeed;
    
    public FreeMotionCameraState(float movementSpeed, float rotationSpeed) {
        this.movementSpeed = movementSpeed;
        this.rotationSpeed = rotationSpeed;
    }
    
    @Override
    protected void init(Application app) {
        
        ed = EntityUtils.getEntityData(app);
        
        inputManager.addMapping(FORWARD, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(BACKWARD, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(UP, new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping(DOWN, new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping(ROTATE_XP, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(ROTATE_XN, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(ROTATE_YP, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(ROTATE_YN, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        
        camera = getState(CameraState.class, true).getMainCamera();
        
    }
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {
        inputManager.addListener(this, FORWARD, BACKWARD, RIGHT,
                LEFT, UP, DOWN, ROTATE_XP, ROTATE_XN, ROTATE_YP, ROTATE_YN);
    }
    @Override
    protected void onDisable() {
        inputManager.removeListener(this);
    }
    @Override
    public void update(float tpf) {
        Vector3f pos = EntityUtils.getComponent(ed, camera, Position.ZERO).getPosition(null);
        Quaternion rot = EntityUtils.getComponent(ed, camera, Rotation.ZERO).getRotation(null);
        Vector3f x = rot.getRotationColumn(0);
        Vector3f y = rot.getRotationColumn(1);
        Vector3f z = rot.getRotationColumn(2);
        pos.addLocal(x.mult(movementInput.x * movementSpeed));
        pos.addLocal(y.mult(movementInput.y * movementSpeed));
        pos.addLocal(z.mult(movementInput.z * movementSpeed));
        rot.multLocal(new Quaternion().fromAngleAxis(rotationInput.x * rotationSpeed, Vector3f.UNIT_Y));
        rot.multLocal(new Quaternion().fromAngleAxis(rotationInput.y * rotationSpeed, Vector3f.UNIT_X));
        rot.lookAt(rot.getRotationColumn(2), Vector3f.UNIT_Y);
        ed.setComponent(camera, new Position(pos));
        ed.setComponent(camera, new Rotation(rot));
        movementInput.set(0, 0, 0);
        rotationInput.set(0, 0);
    }
    @Override
    public void onAnalog(String name, float value, float tpf) {
        switch (name) {
            case FORWARD:   movementInput.z =  1; break;
            case BACKWARD:  movementInput.z = -1; break;
            case RIGHT:     movementInput.x = -1; break;
            case LEFT:      movementInput.x =  1; break;
            case UP:        movementInput.y =  1; break;
            case DOWN:      movementInput.y = -1; break;
            case ROTATE_XP: rotationInput.x = -value; break;
            case ROTATE_XN: rotationInput.x =  value; break;
            case ROTATE_YP: rotationInput.y = -value; break;
            case ROTATE_YN: rotationInput.y =  value; break;
        }
    }
    
}
