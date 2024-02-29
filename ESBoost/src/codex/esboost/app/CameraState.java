/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.EntityUtils;
import codex.esboost.components.CameraInfo;
import codex.esboost.components.FieldOfView;
import codex.esboost.components.Position;
import codex.esboost.components.Rotation;
import codex.esboost.components.ViewPortRect;
import codex.esboost.factories.Factory;
import codex.esboost.factories.Prefab;
import com.jme3.app.Application;
import com.jme3.math.Transform;
import com.jme3.renderer.Camera;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.state.GameSystemsState;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class CameraState extends GameAppState implements Factory<Camera> {
    
    private EntityData ed;
    private EntitySet cameras;
    private Factory<Camera> factory;
    private EntityId mainCam;
    private final HashMap<EntityId, Camera> cameraMap = new HashMap<>();
    private final Transform tempTransform = new Transform();
    
    @Override
    protected void init(Application app) {
        ed = getState(GameSystemsState.class, true).get(EntityData.class, true);
        cameras = ed.getEntities(CameraInfo.class, ViewPortRect.class, FieldOfView.class);
        if (factory == null) {
            factory = this;
        }
        mainCam = ed.createEntity();
        cameraMap.put(mainCam, cam);
        ed.setComponents(mainCam,
            new CameraInfo(new Prefab(0)),
            new ViewPortRect(),
            new FieldOfView(),
            new Position(),
            new Rotation()
        );
    }
    @Override
    protected void cleanup(Application app) {
        cameras.release();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (cameras.applyChanges()) {
            for (Entity e : cameras.getAddedEntities()) {
                Camera c = cameraMap.get(e.getId());
                if (c == null) {
                    c = factory.create(e.get(CameraInfo.class).getPrefab().getName(ed), e.getId());
                    cameraMap.put(e.getId(), c);
                }
                updateCamera(e, c);
            }
            for (Entity e : cameras.getChangedEntities()) {
                updateCamera(e);
            }
            for (Entity e : cameras.getRemovedEntities()) {
                cameraMap.remove(e.getId());
            }
        }
        for (Entity e : cameras) {
            EntityUtils.getWorldTransform(ed, e.getId(), tempTransform);
            Camera c = cameraMap.get(e.getId());
            c.setLocation(tempTransform.getTranslation());
            c.setRotation(tempTransform.getRotation());
        }
    }
    @Override
    public Camera create(String name, EntityId customer) {
        return cam.clone();
    }
    
    private void updateCamera(Entity e) {
        updateCamera(e, cameraMap.get(e.getId()));
    }
    private void updateCamera(Entity e, Camera c) {
        e.get(ViewPortRect.class).apply(c);
        FieldOfView v = e.get(FieldOfView.class).apply(c);
        if (v != null) e.set(v);
    }
    
    public Camera getCamera(EntityId id) {
        return cameraMap.get(id);
    }
    public EntityId getMainCamera() {
        return mainCam;
    }
    
}
