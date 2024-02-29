/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.EntityUtils;
import codex.esboost.FunctionFilter;
import codex.esboost.components.Scene;
import com.jme3.app.Application;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * Updates scenes marked as needing update.
 * <p>
 * This state should be run last, in order to avoid changing the scene
 * graph after scene update.
 * 
 * @author codex
 */
public class SceneUpdateState extends GameAppState {
    
    private EntityData ed;
    private SceneMapState sceneState;
    private EntitySet scenes;
    
    @Override
    protected void init(Application app) {
        ed = EntityUtils.getEntityData(app);
        sceneState = getState(SceneMapState.class, true);
        scenes = ed.getEntities(filter(true), Scene.class);
    }
    @Override
    protected void cleanup(Application app) {
        scenes.release();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        scenes.applyChanges();
        for (Entity e : scenes) {
            Node n = sceneState.getScene(e.getId());
            if (n != null) {
                n.updateLogicalState(tpf);
                n.updateGeometricState();
            }
        }
    }
    
    private static FunctionFilter<Scene> filter(boolean update) {
        return new FunctionFilter<>(Scene.class, c -> c.isUpdate() == update);
    }
    
}
