/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.EntityUtils;
import codex.esboost.components.ModelInfo;
import codex.esboost.components.SceneMember;
import com.jme3.app.Application;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 *
 * @author codex
 */
public class ModelSceneState extends GameAppState {

    private EntityData ed;
    private ModelViewState modelState;
    private SceneMapState sceneState;
    private EntitySet models;
    
    @Override
    protected void init(Application app) {
        ed = EntityUtils.getEntityData(app);
        modelState = getState(ModelViewState.class, true);
        sceneState = getState(SceneMapState.class, true);
        models = ed.getEntities(ModelInfo.class, SceneMember.class);
    }
    @Override
    protected void cleanup(Application app) {
        models.release();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (models.applyChanges()) {
            models.getAddedEntities().forEach(e -> assignModel(e, true));
            models.getChangedEntities().forEach(e -> assignModel(e, true));
            models.getRemovedEntities().forEach(e -> assignModel(e, false));
        }
    }
    
    private void assignModel(Entity e, boolean attach) {
        Spatial model = modelState.getSpatial(e.getId());
        if (model != null) {
            if (attach) {
                Node scene = sceneState.getScene(e.get(SceneMember.class).getScene());
                if (scene != null) {
                    scene.attachChild(model);
                }
            } else {
                model.removeFromParent();
            }
        }
    }
    
}
