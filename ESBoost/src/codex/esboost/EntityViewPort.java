/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost;

import com.jme3.asset.AssetManager;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class EntityViewPort {
    
    private final EntityId entity;
    private final ViewPort viewPort;
    private final int order;
    private final AssetManager assetManager;
    private FilterPostProcessor fpp;

    public EntityViewPort(EntityId entity, ViewPort viewport, int order, AssetManager assetManager) {
        this.entity = entity;
        this.viewPort = viewport;
        this.order = order;
        this.assetManager = assetManager;
    }
    
    public void addFilter(Filter filter) {
        if (fpp == null) {
            fpp = new FilterPostProcessor(assetManager);
            viewPort.addProcessor(fpp);
        }
        fpp.addFilter(filter);
    }
    public void removeFilter(Filter filter) {
        if (fpp == null) return;
        fpp.removeFilter(filter);
        if (fpp.getFilterList().isEmpty()) {
            viewPort.removeProcessor(fpp);
            fpp = null;
        }
    }

    public EntityId getEntity() {
        return entity;
    }
    public ViewPort getViewPort() {
        return viewPort;
    }
    public int getOrder() {
        return order;
    }
    
}
