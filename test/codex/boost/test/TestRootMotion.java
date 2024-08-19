/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.test;

import codex.boost.anim.RootMotionControl;
import codex.boost.mesh.Quad;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 *
 * @author codex
 */
public class TestRootMotion extends SimpleApplication implements ActionListener {

    private BetterCharacterControl bcc;
    private RootMotionControl rootMotion;
    private Vector3f inputDirection = new Vector3f();
    private Vector3f move = new Vector3f();
    
    public static void main(String[] args) {
        new TestRootMotion().start();
    }
    
    @Override
    public void simpleInitApp() {
    
        BulletAppState bullet = new BulletAppState();
        stateManager.attach(bullet);
        
        Node model = (Node)assetManager.loadModel("Models/YBot.j3o");
        model.setLocalScale(0.01f);
        model.setLocalTranslation(0, 5, 0);
        AnimComposer anim = model.getChild("Armature").getControl(AnimComposer.class);
        SkinningControl skin = anim.getSpatial().getControl(SkinningControl.class);
        bcc = new BetterCharacterControl(1, 5, 200);
        model.addControl(bcc);
        bullet.getPhysicsSpace().add(bcc);
        rootMotion = new RootMotionControl("root-motion-control", skin);
        model.addControl(rootMotion);
        rootNode.attachChild(model);
        
        Action run = anim.actionSequence("run", anim.action("sprint"), Tweens.callMethod(rootMotion, "freezeMotionDelta", 3));
        anim.setCurrentAction("run");
        
        ChaseCamera chaseCam = new ChaseCamera(cam, model, inputManager);
        chaseCam.setDragToRotate(true);
        chaseCam.setMinVerticalRotation(-FastMath.HALF_PI);
        chaseCam.setMaxDistance(1000);
        chaseCam.setSmoothMotion(true);
        chaseCam.setRotationSensitivity(10);
        chaseCam.setZoomSensitivity(5);
        flyCam.setEnabled(false);
        
        Quad quad = new Quad(Vector3f.UNIT_Y, Vector3f.UNIT_Z, 100, 100, .5f, .5f);
        //quad.scaleTextureCoordinates(new Vector2f(10f, 10f));
        Geometry floor = new Geometry("floor", quad);
        Material floorMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        floorMat.setTexture("DiffuseMap", assetManager.loadTexture("Models/BlueColorScheme.png"));
        floor.setMaterial(floorMat);
        RigidBodyControl floorPhys = new RigidBodyControl(0);
        floor.addControl(floorPhys);
        bullet.getPhysicsSpace().add(floorPhys);
        rootNode.attachChild(floor);
        
        rootNode.addLight(new DirectionalLight(new Vector3f(1, -1, 1)));
        rootNode.addLight(new AmbientLight(ColorRGBA.DarkGray));
        
        inputManager.addMapping("forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("strafe", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addListener(this, "forward", "strafe");
        
    }
    @Override
    public void simpleUpdate(float tpf) {
        move.set(cam.getDirection()).setY(0).normalizeLocal().multLocal(inputDirection.z);
        move.addLocal(cam.getLeft().multLocal(inputDirection.x)).normalizeLocal();
        move.multLocal(rootMotion.getMotionDelta().length());
        bcc.setWalkDirection(move);
        bcc.setViewDirection(move);
    }
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("forward")) {
            inputDirection.z = isPressed ? 1 : 0;
        } else if (name.equals("strafe")) {
            inputDirection.x = isPressed ? 1 : 0;
        }
    }
    
}
