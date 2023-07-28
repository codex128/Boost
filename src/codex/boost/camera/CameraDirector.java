/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.boost.camera;

import com.jme3.math.Transform;
import com.jme3.renderer.Camera;

/**
 * Interface for the transform of a camera.
 * 
 * @author codex
 */
public interface CameraDirector {
    
    public Transform getNextTransform(Camera camera);
    public Transform getCurrentTransform(Camera camera);
    public void onAgentEnabled(Camera camera);
    public void onAgentDisabled();
    
}
