/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.connection.ConnectionManager;
import codex.esboost.EntityUtils;
import codex.esboost.EntityViewPort;
import codex.esboost.components.BackgroundColor;
import codex.esboost.components.ClearFlags;
import codex.esboost.components.View;
import codex.esboost.connection.Provider;
import com.jme3.app.Application;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class ViewPortState extends GameAppState implements Provider<EntityViewPort, EntityId> {
    
    private EntityData ed;
    private ConnectionManager connector;
    private CameraState camState;
    private EntitySet viewports;
    private EntityId mainVp, guiVp;
    private final HashMap<EntityId, EntityViewPort> vpMap = new HashMap<>();
    
    @Override
    protected void init(Application app) {
        ed = EntityUtils.getEntityData(app);
        connector = EntityUtils.getConnectionManager(app);
        camState = getState(CameraState.class, true);
        viewports = ed.getEntities(View.class, BackgroundColor.class, ClearFlags.class);
        mainVp = ed.createEntity();
        guiVp = ed.createEntity();
        vpMap.put(mainVp, new EntityViewPort(mainVp, viewPort, View.MAIN, assetManager));
        vpMap.put(guiVp, new EntityViewPort(guiVp, viewPort, View.MAIN, assetManager));
    }
    @Override
    protected void cleanup(Application app) {
        viewports.release();
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
        connector.applyChanges(this);
    }
    @Override
    public EntityViewPort fetchConnectingObject(EntityId key) {
        return vpMap.get(key);
    }
    
    private void createViewPort(Entity e) {
        if (!vpMap.containsKey(e.getId())) {            
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
                default: throw new IllegalArgumentException("Unknown view order identifier: "+view.getOrder());
            }
            vpMap.put(e.getId(), new EntityViewPort(e.getId(), vp, view.getOrder(), assetManager));
        }
        updateViewPort(e);
    }
    private void updateViewPort(Entity e) {
        EntityViewPort wrapper = vpMap.get(e.getId());
        if (wrapper != null) {
            ClearFlags flags = e.get(ClearFlags.class);
            wrapper.getViewPort().setClearFlags(flags.isColor(), flags.isDepth(), flags.isStencil());
            wrapper.getViewPort().setBackgroundColor(e.get(BackgroundColor.class).getColor());
        }
    }
    private void removeViewPort(Entity e) {
        EntityViewPort vp = vpMap.remove(e.getId());
        if (vp != null) {
            connector.makeDeletionContainingRequest(vp);
            switch (vp.getOrder()) {
                case View.PRE: renderManager.removePreView(vp.getViewPort()); break;
                case View.MAIN: renderManager.removeMainView(vp.getViewPort()); break;
                case View.POST: renderManager.removePostView(vp.getViewPort()); break;
            }
        }
    }
    
    public EntityViewPort getViewPort(EntityId id) {
        return vpMap.get(id);
    }
    
    public EntityId getMainViewPort() {
        return mainVp;
    }
    public EntityId getGuiViewPort() {
        return guiVp;
    }
    
}
