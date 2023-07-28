/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.camera;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.Camera;
import java.util.LinkedHashMap;

/**
 *
 * @author codex
 */
public class CameraAppState extends BaseAppState {
    
    protected LinkedHashMap<Camera, CameraAgent> cameras = new LinkedHashMap<>();
    
    @Override
    protected void initialize(Application app) {}
    @Override
    protected void cleanup(Application app) {
        cameras.clear();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        cameras.forEach((cam, control) -> control.updateCamera(cam, tpf));
    }
    
    /**
     * Assign this agent to this camera.
     * @param camera camera to assign to
     * @param agent agent to assign, or null to remove the current agent
     */
    public void assign(Camera camera, CameraAgent agent) {
        put(camera, agent);
    }
    /**
     * Fetches the agent assigned to this camera.
     * @param camera
     * @return controller assigned, or null if none
     */
    public CameraAgent fetch(Camera camera) {
        return cameras.get(camera);
    }
    
    private CameraAgent put(Camera camera, CameraAgent agent) {
        if (agent == null) {
            return remove(camera);
        }
        CameraAgent prev = cameras.put(camera, agent);
        if (prev != null) {
            prev.onAgentDisabled();
        }
        agent.onAgentEnabled(camera);
        return prev;
    }
    private CameraAgent remove(Camera camera) {
        return cameras.remove(camera);
    }
    
}
