/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.EntityUtils;
import codex.esboost.components.EnvironmentBakeTag;
import codex.esboost.components.ModelInfo;
import com.jme3.app.Application;
import com.jme3.environment.EnvironmentProbeControl;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;

/**
 *
 * @author codex
 */
public class ProbeTagState extends GameAppState {

    private EntityData ed;
    private ModelViewState modelState;
    private EntitySet entities;
    
    @Override
    protected void init(Application app) {
        ed = EntityUtils.getEntityData(app);
        modelState = getState(ModelViewState.class, true);
        entities = ed.getEntities(ModelInfo.class, EnvironmentBakeTag.class);
    }
    @Override
    protected void cleanup(Application app) {
        entities.release();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (entities.applyChanges()) {
            for (Entity e : entities.getAddedEntities()) {
                Spatial spatial = modelState.getSpatial(e.getId());
                if (spatial != null) {
                    EnvironmentProbeControl.tagGlobal(spatial);
                }
            }
            for (Entity e : entities.getAddedEntities()) {
                Spatial spatial = modelState.getSpatial(e.getId());
                if (spatial != null) {
                    EnvironmentProbeControl.untagGlobal(spatial);
                }
            }
        }
    }
    
}
