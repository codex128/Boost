/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.test;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import jme3utilities.sky.SkyControl;
import jme3utilities.sky.StarsOption;

/**
 *
 * @author codex
 */
public class TestShaders extends SimpleApplication implements AppState {

    Material mat;
    DirectionalLight light;
    boolean enabled = true;
    boolean initialized = false;
    String id = "TestShader#AppState";
    
    public static void main(String[] args) {
        new TestShaders().start();
    }
    
    @Override
    public void simpleInitApp() {
        
        assetManager.registerLocator(System.getProperty("user.home")+"/java/assets", FileLocator.class);        
        flyCam.setMoveSpeed(5f);
        
        EnvironmentCamera camera = new EnvironmentCamera();
        camera.setPosition(new Vector3f(3f, 3f, 3f));
        stateManager.attach(camera);
        
        stateManager.attach(this);
        
    }
    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
    
        mat = new Material(assetManager, "ShaderBoostExamples/Cobblestone.j3md");
        
        Geometry cube = new Geometry("test-cube", new Box(1f, 1f, 1f));
        TangentBinormalGenerator.generate(cube);
        cube.setMaterial(mat);
        cube.setShadowMode(RenderQueue.ShadowMode.Cast);
        rootNode.attachChild(cube);
        
        Geometry floor = new Geometry("test-floor", new Quad(20f, 20f));
        TangentBinormalGenerator.generate(floor);
        floor.setLocalTranslation(-10f, -3f, 10f);
        floor.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        floor.setMaterial(mat);
        floor.setShadowMode(RenderQueue.ShadowMode.Receive);
        rootNode.attachChild(floor);
        
        light = new DirectionalLight(new Vector3f(0f, 0f, 1f), ColorRGBA.Gray);
        rootNode.addLight(light);
        PointLight pl = new PointLight(new Vector3f(-2f, 2f, -2f), ColorRGBA.White);
        pl.setRadius(100f);
        rootNode.addLight(pl);
        
        PointLightShadowRenderer plsr = new PointLightShadowRenderer(assetManager, 4096);
        plsr.setLight(pl);
        viewPort.addProcessor(plsr);
        
        Spatial sky = SkyFactory.createSky(assetManager, "FullskiesSunset0068.dds", SkyFactory.EnvMapType.CubeMap);
        sky.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sky);        
        SkyControl skyControl = new SkyControl(assetManager, cam, .5f, StarsOption.TopDome, true);
        rootNode.addControl(skyControl);
        skyControl.setCloudiness(0.8f);
        skyControl.setCloudsYOffset(0.4f);
        skyControl.setTopVerticalAngle(1.78f);
        skyControl.getSunAndStars().setHour(10);
        skyControl.setEnabled(true);        
        
        AmbientLight gi = new AmbientLight(ColorRGBA.DarkGray);
        rootNode.addLight(gi);
        
        EnvironmentCamera envCam = stateManager.getState(EnvironmentCamera.class, true);
        envCam.setBackGroundColor(ColorRGBA.White);
        LightProbeFactory.makeProbe(envCam, rootNode, new JobProgressAdapter<LightProbe>() {
            @Override
            public void done(LightProbe result) {
                System.out.println("finished making probe");
                result.getArea().setRadius(100f);
                rootNode.addLight(result);
            }
        });
    
    }
    @Override
    public boolean isInitialized() {
        return initialized;
    }
    @Override
    public String getId() {
        return id;
    }
    @Override
    public void setEnabled(boolean active) {
        enabled = active;
    }
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    @Override
    public void stateAttached(AppStateManager stateManager) {}
    @Override
    public void stateDetached(AppStateManager stateManager) {}
    @Override
    public void update(float tpf) {
        light.setDirection(cam.getDirection());
    }
    @Override
    public void render(RenderManager rm) {}
    @Override
    public void postRender() {}
    @Override
    public void cleanup() {}
    
}
