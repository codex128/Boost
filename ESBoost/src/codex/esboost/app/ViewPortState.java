/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.components.BackgroundColor;
import codex.esboost.components.CameraInfo;
import codex.esboost.components.ClearFlags;
import codex.esboost.components.FieldOfView;
import codex.esboost.components.Scene;
import codex.esboost.components.View;
import codex.esboost.components.ViewPortMember;
import codex.esboost.components.ViewPortRect;
import codex.esboost.factories.Prefab;
import com.jme3.app.Application;
import com.jme3.math.Transform;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.state.GameSystemsState;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public class ViewPortState extends GameAppState {
    
    private EntityData ed;
    private SceneMapState sceneState;
    private CameraState camState;
    private EntitySet viewports, scenes;
    private EntityId camId, viewPortId;
    private final HashMap<EntityId, ViewPortWrapper> viewportMap = new HashMap<>();
    
    @Override
    protected void init(Application app) {
        ed = getState(GameSystemsState.class, true).get(EntityData.class, true);
        sceneState = getState(SceneMapState.class, true);
        camState = getState(CameraState.class, true);
        viewports = ed.getEntities(View.class, BackgroundColor.class, ClearFlags.class);
        scenes = ed.getEntities(Scene.class, ViewPortMember.class);
    }
    @Override
    protected void cleanup(Application app) {
        viewports.release();
        scenes.release();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (viewports.applyChanges()) {
            viewports.getAddedEntities().forEach(e -> createViewPort(e));
            viewports.getChangedEntities().forEach(e -> updateViewPort(e));
            viewports.getRemovedEntities().forEach(e -> removeViewPort(e));
        }
        if (scenes.applyChanges()) {
            scenes.getAddedEntities().forEach(e -> assignScene(e));
            scenes.getRemovedEntities().forEach(e -> removeScene(e));
        }
    }
    
    private void createViewPort(Entity e) {
        if (!viewportMap.containsKey(e.getId())) {            
            View view = e.get(View.class);
            Camera c = camState.getCamera(view.getCamera());
            if (c == null) {
                throw new NullPointerException("Camera for viewport does not exist.");
            }
            ViewPort vp;
            switch (view.getOrder()) {
                case View.PRE: vp = renderManager.createPreView(view.getName(), c); break;
                case View.MAIN: vp = renderManager.createMainView(view.getName(), c); break;
                case View.POST: vp = renderManager.createPostView(view.getName(), c); break;
                default: throw new IllegalArgumentException("Unknown order identifier: "+view.getOrder());
            }
            for (EntityId s : view.getScenes()) {
                Node scene = sceneState.getScene(s);
                if (scene != null && !vp.getScenes().contains(scene)) {
                    vp.attachScene(scene);
                }
            }
            viewportMap.put(e.getId(), new ViewPortWrapper(vp, view.getOrder()));
        }
        updateViewPort(e);
    }
    private void updateViewPort(Entity e) {
        ViewPortWrapper wrapper = viewportMap.get(e.getId());
        if (wrapper != null) {
            ClearFlags flags = e.get(ClearFlags.class);
            wrapper.viewPort.setClearFlags(flags.isColor(), flags.isDepth(), flags.isStencil());
            wrapper.viewPort.setBackgroundColor(e.get(BackgroundColor.class).getColor());
        }
    }
    private void removeViewPort(Entity e) {
        ViewPortWrapper wrapper = viewportMap.remove(e.getId());
        if (wrapper != null) switch (wrapper.order) {
            case View.PRE: renderManager.removePreView(wrapper.viewPort); break;
            case View.MAIN: renderManager.removeMainView(wrapper.viewPort); break;
            case View.POST: renderManager.removePostView(wrapper.viewPort); break;
        }
    }
    
    private void assignScene(Entity e) {
        Node scene = sceneState.getScene(e.getId());
        if (scene != null) for (EntityId v : e.get(ViewPortMember.class).getViewPorts()) {
            ViewPort vp = getViewPort(v);
            if (vp != null) {
                vp.attachScene(scene);
            }
        }
    }
    private void removeScene(Entity e) {
        Node scene = sceneState.getScene(e.getId());
        if (scene != null) for (EntityId v : e.get(ViewPortMember.class).getViewPorts()) {
            ViewPort vp = getViewPort(v);
            if (vp != null) {
                vp.detachScene(scene);
            }
        }
    }
    
    public EntityId getCameraId() {
        return camId;
    }
    public EntityId getViewPortId() {
        return viewPortId;
    }
    public ViewPort getViewPort(EntityId id) {
        ViewPortWrapper wrapper = viewportMap.get(id);
        if (wrapper != null) {
            return wrapper.viewPort;
        } else {
            return null;
        }
    }
    public int getViewPortOrder(EntityId id) {
        ViewPortWrapper wrapper = viewportMap.get(id);
        if (wrapper != null) {
            return wrapper.order;
        } else {
            return -1;
        }
    }
    
    private class ViewPortWrapper {
        
        public final ViewPort viewPort;
        public final int order;

        public ViewPortWrapper(ViewPort viewPort, int order) {
            this.viewPort = viewPort;
            this.order = order;
        }
        
    }
    
}
