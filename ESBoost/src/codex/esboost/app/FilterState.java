/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.connection.ConnectionManager;
import codex.esboost.EntityUtils;
import codex.esboost.EntityViewPort;
import codex.esboost.components.FilterInfo;
import codex.esboost.connection.Connector;
import codex.esboost.factories.Factory;
import com.jme3.app.Application;
import com.jme3.post.Filter;
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
public class FilterState extends GameAppState implements Connector<Filter, EntityViewPort> {
    
    private EntityData ed;
    private ConnectionManager connector;
    private ViewPortState vpState;
    private EntitySet filters;
    private final Factory<Filter> factory;
    private final HashMap<EntityId, Filter> filterMap = new HashMap<>();

    public FilterState(Factory<Filter> factory) {
        this.factory = factory;
    }
    
    @Override
    protected void init(Application app) {
        ed = EntityUtils.getEntityData(app);
        connector = EntityUtils.getConnectionManager(app);
        vpState = getState(ViewPortState.class, true);
        filters = ed.getEntities(FilterInfo.class);
    }
    @Override
    protected void cleanup(Application app) {
        filters.release();
        filterMap.clear();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (filters.applyChanges()) {
            for (Entity e : filters.getAddedEntities()) {
                createFilter(e);
            }
            for (Entity e : filters.getRemovedEntities()) {
                Filter f = filterMap.remove(e.getId());
                if (f != null) {
                    connector.makeDeletionContainingRequest(vpState, f);
                }
            }
        }
    }
    @Override
    public void connect(Filter filter, EntityViewPort vp) {
        vp.addFilter(filter);
    }
    @Override
    public void disconnect(Filter filter, EntityViewPort vp) {
        vp.removeFilter(filter);
    }
    @Override
    public MultiConnectionHint getMultiConnectionHint() {
        return Connector.MultiConnectionHint.OnlyObjectB;
    }
    
    private void createFilter(Entity e) {
        FilterInfo info = e.get(FilterInfo.class);
        Filter filter = factory.create(info.getPrefab().getName(ed), e.getId(), true);
        filterMap.put(e.getId(), filter);
        connector.makePendingConnection(this, vpState, filter, info.getViewPort());
    }
    
}
