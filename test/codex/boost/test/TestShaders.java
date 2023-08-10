/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.test;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;
import jme3utilities.sky.SkyControl;
import jme3utilities.sky.StarsOption;

/**
 *
 * @author codex
 */
public class TestShaders extends SimpleApplication {

    Material mat;
    DirectionalLight light;
    
    public static void main(String[] args) {
        new TestShaders().start();
    }
    
    @Override
    public void simpleInitApp() {
        
        assetManager.registerLocator(System.getProperty("user.home")+"/java/assets", FileLocator.class);        
        flyCam.setMoveSpeed(5f);
        
        Geometry cube = new Geometry("test-cube", new Box(1f, 1f, 1f));
        mat = new Material(assetManager, "ShaderBoostExamples/PBRTest.j3md");
        mat.setTexture("DiffuseMap", assetManager.loadTexture("BrickWall.jpg"));
        mat.setTexture("NormalMap", assetManager.loadTexture("BrickWall_normal.jpg"));
        cube.setMaterial(mat);
        rootNode.attachChild(cube);
        
        light = new DirectionalLight(new Vector3f(0f, 0f, 1f), ColorRGBA.Gray);
        rootNode.addLight(light);
        PointLight pl = new PointLight(new Vector3f(-2f, -2f, -2f), ColorRGBA.White);
        pl.setRadius(100f);
        rootNode.addLight(pl);
        
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
        
    }
    @Override
    public void simpleUpdate(float tpf) {
        light.setDirection(cam.getDirection());
    }
    
}
