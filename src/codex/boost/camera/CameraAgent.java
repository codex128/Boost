/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.boost.camera;

import com.jme3.renderer.Camera;

/**
 * Interface for controlling a camera.
 * 
 * @author codex
 */
public interface CameraAgent {
    
    public void updateCamera(Camera camera, float tpf);
    public void onAgentEnabled(Camera camera);
    public void onAgentDisabled();
    
}
