/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.connection.ConnectionManager;
import codex.esboost.EntityUtils;
import codex.esboost.EntityViewPort;
import codex.esboost.components.LightInfo;
import codex.esboost.components.ShadowInfo;
import codex.esboost.components.ViewPortMember;
import codex.esboost.connection.Connector;
import com.jme3.app.Application;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.shadow.AbstractShadowFilter;
import com.jme3.shadow.AbstractShadowRenderer;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.PointLightShadowFilter;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.shadow.SpotLightShadowRenderer;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class ShadowState extends GameAppState {
    
    private EntityData ed;
    private ConnectionManager connector;
    private LightingState lightState;
    private ViewPortState vpState;
    private EntitySet shadows;
    private final RendererConnector rConnect = new RendererConnector();
    private final FilterConnector fConnect = new FilterConnector();
    private final HashMap<EntityId, AbstractShadowRenderer> renderers = new HashMap<>();
    private final HashMap<EntityId, AbstractShadowFilter> filters = new HashMap<>();
    
    @Override
    protected void init(Application app) {
        ed = EntityUtils.getEntityData(app);
        connector = EntityUtils.getConnectionManager(app);
        lightState = getState(LightingState.class, true);
        vpState = getState(ViewPortState.class, true);
        shadows = ed.getEntities(LightInfo.filter(LightInfo.DIRECTIONAL, LightInfo.POINT, LightInfo.SPOT),
                LightInfo.class, ShadowInfo.class, ViewPortMember.class);
    }
    @Override
    protected void cleanup(Application app) {
        shadows.release();
        renderers.clear();
        filters.clear();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (shadows.applyChanges()) {
            shadows.getAddedEntities().forEach(e -> addShadows(e));
            shadows.getRemovedEntities().forEach(e -> removeShadows(e));
        }
    }
    
    private void addShadows(Entity e) {
        Light l = lightState.getLight(e.getId());
        if (l == null) return;
        ShadowInfo info = e.get(ShadowInfo.class);
        EntityId vp = e.get(ViewPortMember.class).getViewPort();
        if (info.isRenderer()) {
            AbstractShadowRenderer r = null;
            if (l instanceof DirectionalLight) {
                r = createDirectionalShadowRenderer(e, info, (DirectionalLight)l);
            } else if (l instanceof PointLight) {
                r = createPointShadowRenderer(e, info, (PointLight)l);
            } else if (l instanceof SpotLight) {
                r = createSpotShadowRenderer(e, info, (SpotLight)l);
            }
            if (r != null) {
                renderers.put(e.getId(), r);
                connector.makePendingConnection(rConnect, vpState, r, vp);
            }
        } else {
            AbstractShadowFilter f = null;
            if (l instanceof DirectionalLight) {
                f = createDirectionalShadowFilter(e, info, (DirectionalLight)l);
            } else if (l instanceof PointLight) {
                f = createPointShadowFilter(e, info, (PointLight)l);
            } else if (l instanceof SpotLight) {
                f = createSpotShadowFilter(e, info, (SpotLight)l);
            }
            if (f != null) {
                filters.put(e.getId(), f);
                connector.makePendingConnection(fConnect, vpState, f, vp);
            }
        }
    }
    private void removeShadows(Entity e) {
        AbstractShadowRenderer r = renderers.remove(e.getId());
        if (r != null) {
            connector.makeDeletionContainingRequest(r);
        } else {
            AbstractShadowFilter f = filters.remove(e.getId());
            if (f != null) {
                connector.makeDeletionContainingRequest(f);
            }
        }
    }
    
    private DirectionalLightShadowRenderer createDirectionalShadowRenderer(Entity e, ShadowInfo info, DirectionalLight l) {
        DirectionalLightShadowRenderer r = new DirectionalLightShadowRenderer(
                assetManager, info.getResolution(), info.getSplits());
        r.setLight(l);
        return r;
    }
    private PointLightShadowRenderer createPointShadowRenderer(Entity e, ShadowInfo info, PointLight l) {
        PointLightShadowRenderer r = new PointLightShadowRenderer(assetManager, info.getResolution());
        r.setLight(l);
        return r;
    }
    private SpotLightShadowRenderer createSpotShadowRenderer(Entity e, ShadowInfo info, SpotLight l) {
        SpotLightShadowRenderer r = new SpotLightShadowRenderer(assetManager, info.getResolution());
        r.setLight(l);
        return r;
    }
    
    private DirectionalLightShadowFilter createDirectionalShadowFilter(Entity e, ShadowInfo info, DirectionalLight l) {
        DirectionalLightShadowFilter r = new DirectionalLightShadowFilter(
                assetManager, info.getResolution(), info.getSplits());
        r.setLight(l);
        return r;
    }
    private PointLightShadowFilter createPointShadowFilter(Entity e, ShadowInfo info, PointLight l) {
        PointLightShadowFilter r = new PointLightShadowFilter(assetManager, info.getResolution());
        r.setLight(l);
        return r;
    }
    private SpotLightShadowFilter createSpotShadowFilter(Entity e, ShadowInfo info, SpotLight l) {
        SpotLightShadowFilter r = new SpotLightShadowFilter(assetManager, info.getResolution());
        r.setLight(l);
        return r;
    }
    
    private class RendererConnector implements Connector<AbstractShadowRenderer, EntityViewPort> {

        @Override
        public void connect(AbstractShadowRenderer renderer, EntityViewPort vp) {
            vp.getViewPort().addProcessor(renderer);
        }
        @Override
        public void disconnect(AbstractShadowRenderer renderer, EntityViewPort vp) {
            vp.getViewPort().removeProcessor(renderer);
        }
        @Override
        public MultiConnectionHint getMultiConnectionHint() {
            return Connector.MultiConnectionHint.OnlyObjectB;
        }
        
    }
    private class FilterConnector implements Connector<AbstractShadowFilter, EntityViewPort> {

        @Override
        public void connect(AbstractShadowFilter filter, EntityViewPort vp) {
            vp.addFilter(filter);
        }
        @Override
        public void disconnect(AbstractShadowFilter filter, EntityViewPort vp) {
            vp.removeFilter(filter);
        }
        @Override
        public MultiConnectionHint getMultiConnectionHint() {
            return Connector.MultiConnectionHint.OnlyObjectB;
        }
        
    }
    
}
