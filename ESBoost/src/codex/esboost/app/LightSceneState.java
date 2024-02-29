/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.EntityUtils;
import codex.esboost.render.SceneLight;
import codex.esboost.components.EntityLight;
import codex.esboost.components.SceneMember;
import com.jme3.app.Application;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 * Binds lights to scenes.
 * 
 * @author codex
 */
public class LightSceneState extends GameAppState {
    
    private EntityData ed;
    private LightingState lightState;
    private SceneMapState sceneState;
    private EntitySet lights;
    
    @Override
    protected void init(Application app) {
        ed = EntityUtils.getEntityData(app);
        lightState = getState(LightingState.class, true);
        sceneState = getState(SceneMapState.class, true);
        lights = ed.getEntities(EntityLight.class, SceneMember.class);
    }
    @Override
    protected void cleanup(Application app) {
        lights.release();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (lights.applyChanges()) {
            lights.getAddedEntities().forEach(e -> assignLight(e, true));
            lights.getChangedEntities().forEach(e -> assignLight(e, true));
            lights.getRemovedEntities().forEach(e -> assignLight(e, false));
        }
    }
    
    private void assignLight(Entity e, boolean attach) {
        SceneLight light = lightState.getLight(e.getId());
        if (light != null) {
            if (attach) {
                Node scene = sceneState.getScene(e.get(SceneMember.class).getScene());
                light.assign(scene);
            } else {
                light.assign(null);
            }
        }
    }
    
}
