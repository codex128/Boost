/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.camera;

import com.jme3.renderer.Camera;
import com.simsilica.lemur.input.InputMapper;

/**
 * Implementation of {@link OrbitalCamera}, but as a {@link CameraAgent}.
 * 
 * <p><strong>Warning:</strong> Some methods may throw {@code NullPointerExceptions} if this is not
 * currently controlling a camera.
 * 
 * @author codex
 */
public class OrbitalCameraAgent extends OrbitalCamera implements CameraAgent {
    
    public OrbitalCameraAgent() {
        super(null);
    }
    public OrbitalCameraAgent(InputMapper im) {
        super(null, im);
    }    
    
    @Override
    public void controlUpdate(float tpf) {
        if (enabledAsAgent()) super.controlUpdate(tpf);
    }
    @Override
    public void updateCamera(Camera camera, float tpf) {}
    @Override
    public void onAgentEnabled(Camera camera) {
        setCamera(camera);
    }
    @Override
    public void onAgentDisabled() {
        setCamera(null);
    }
    
    public boolean enabledAsAgent() {
        return cam != null;
    }
    
}
