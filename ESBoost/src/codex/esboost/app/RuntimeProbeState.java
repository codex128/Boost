/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.EntityUtils;
import codex.esboost.components.EntityLight;
import codex.esboost.components.EnvMapSize;
import codex.esboost.components.SceneMember;
import com.jme3.app.Application;
import com.jme3.environment.EnvironmentProbeControl;
import com.jme3.math.Transform;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public class RuntimeProbeState extends GameAppState {

    private EntityData ed;
    private SceneMapState sceneState;
    private EntitySet probes, members;
    private final HashMap<EntityId, EnvironmentProbeControl> probeMap = new HashMap<>();
    private final LinkedList<EnvironmentProbeControl> assignList = new LinkedList<>();
    private final Transform tempTransform = new Transform();
    
    @Override
    protected void init(Application app) {
        ed = EntityUtils.getEntityData(app);
        sceneState = getState(SceneMapState.class);
        probes = ed.getEntities(EntityLight.filter(EntityLight.RUNTIME_PROBE),
                EntityLight.class, EnvMapSize.class);
        if (sceneState != null) {
            members = ed.getEntities(EntityLight.filter(EntityLight.RUNTIME_PROBE),
                    EntityLight.class, EnvMapSize.class, SceneMember.class);
        }
    }
    @Override
    protected void cleanup(Application app) {
        probes.release();
        if (members != null) {
            members.release();
        }
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (probes.applyChanges()) {
            for (Entity e : probes.getAddedEntities()) {
                System.out.println("create runtime probe");
                EnvironmentProbeControl control = new EnvironmentProbeControl(assetManager, e.get(EnvMapSize.class).getSize());
                probeMap.put(e.getId(), control);
                assignList.add(control);
            }
            for (Entity e : probes.getRemovedEntities()) {
                EnvironmentProbeControl control = probeMap.remove(e.getId());
                if (control.getSpatial() != null) {
                    control.getSpatial().removeControl(control);
                }
            }
        }
        if (members != null && members.applyChanges()) {
            for (Entity e : members.getAddedEntities()) {
                Node scene = sceneState.getScene(e.get(SceneMember.class).getScene());
                if (scene != null) {
                    EnvironmentProbeControl control = probeMap.get(e.getId());
                    scene.addControl(control);
                }
            }
            for (Entity e : members.getRemovedEntities()) {
                EnvironmentProbeControl control = probeMap.get(e.getId());
                if (control != null && control.getSpatial() != null) {
                    control.getSpatial().removeControl(control);
                }
            }
        }
        for (Entity e : probes) {
            EntityUtils.getWorldTransform(ed, e.getId(), tempTransform);
            EnvironmentProbeControl control = probeMap.get(e.getId());
            control.setPosition(tempTransform.getTranslation());
        }
        // controls that did not get assigned to a scene above are assigned to the rootnode
        if (!assignList.isEmpty()) {
            for (EnvironmentProbeControl control : assignList) {
                if (control.getSpatial() == null) {
                    rootNode.addControl(control);
                }
            }
            assignList.clear();
        }
    }
    
}
