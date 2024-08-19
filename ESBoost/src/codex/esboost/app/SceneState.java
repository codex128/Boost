/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.connection.ConnectionManager;
import codex.esboost.EntityUtils;
import codex.esboost.EntityViewPort;
import codex.esboost.components.SceneInfo;
import codex.esboost.components.TargetTo;
import codex.esboost.components.ViewPortMember;
import codex.esboost.connection.Connector;
import codex.esboost.connection.Delete;
import codex.esboost.connection.Provider;
import codex.esboost.factories.Factory;
import com.jme3.app.Application;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class SceneState extends GameAppState implements
        Factory<Node>, Provider<Node, EntityId>, Connector<Node, EntityViewPort> {
    
    //public static final String ROOT_NODE = "SceneMapState[rootnode]";
    //public static final String GUI_NODE = "SceneMapState[guinode]";
    
    private EntityData ed;
    private ConnectionManager connector;
    private ViewPortState vpState;
    private EntitySet scenes, vpLinks;
    private Factory<Node> factory;
    private EntityId rootNodeId, guiNodeId;
    private final HashMap<EntityId, Node> sceneMap = new HashMap<>();
    private final HashMap<EntityId, ViewLink> sceneViewLinks = new HashMap<>();
    
    public SceneState() {}
    public SceneState(Factory<Node> factory) {
        this.factory = factory;
    }
    
    @Override
    protected void init(Application app) {
        ed = EntityUtils.getEntityData(app);
        connector = EntityUtils.getConnectionManager(app);
        vpState = getState(ViewPortState.class, true);
        scenes = ed.getEntities(SceneInfo.class);
        vpLinks = ed.getEntities(ViewPortMember.class, TargetTo.class);
        if (factory == null) {
            factory = this;
        }
        rootNodeId = ed.createEntity();
        guiNodeId = ed.createEntity();
        sceneMap.put(rootNodeId, rootNode);
        sceneMap.put(guiNodeId, guiNode);
    }
    @Override
    protected void cleanup(Application app) {
        scenes.release();
        sceneMap.clear();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        if (scenes.applyChanges()) {
            for (Entity e : scenes.getAddedEntities()) {
                Node scene = factory.create(e.get(SceneInfo.class).getPrefab().getName(ed), e.getId(), true);
                sceneMap.put(e.getId(), scene);
            }
            for (Entity e : scenes.getRemovedEntities()) {
                Node n = sceneMap.remove(e.getId());
                connector.makeDeletionContainingRequest(n);
            }
        }
        if (vpLinks.applyChanges()) {
            for (Entity e : vpLinks.getAddedEntities()) {
                ViewLink l = new ViewLink(e.getId(), e.get(TargetTo.class).getTargetId(),
                        e.get(ViewPortMember.class).getViewPort());
                l.makeLink();
                sceneViewLinks.put(e.getId(), l);
            }
            for (Entity e : vpLinks.getRemovedEntities()) {
                ViewLink l = sceneViewLinks.remove(e.getId());
                if (l != null) {
                    l.breakLink();
                }
            }
        }
        connector.applyChanges(this);
        for (Entity e : scenes) if (e.get(SceneInfo.class).isUpdate()) {
            Node scene = sceneMap.get(e.getId());
            scene.updateLogicalState(tpf);
            scene.updateGeometricState();
        }
    }
    @Override
    public Node create(String name, EntityId customer) {
        return new Node(name);
    }
    @Override
    public Node fetchConnectingObject(EntityId key) {
        return sceneMap.get(key);
    }
    @Override
    public void connect(Node scene, EntityViewPort vp) {
        vp.getViewPort().attachScene(scene);
    }
    @Override
    public void disconnect(Node scene, EntityViewPort vp) {
        vp.getViewPort().detachScene(scene);
    }
    @Override
    public MultiConnectionHint getMultiConnectionHint() {
        return Connector.MultiConnectionHint.All;
    }
    
    public Node getScene(EntityId id) {
        return sceneMap.get(id);
    }
    public EntityId getRootNode() {
        return rootNodeId;
    }
    public EntityId getGuiNode() {
        return guiNodeId;
    }
    
    private class ViewLink {
        
        private EntityId entity, scene, viewport;
        
        public ViewLink(EntityId entity, EntityId scene, EntityId viewport) {
            this.entity = entity;
            this.scene = scene;
            this.viewport = viewport;
        }
        
        public void makeLink() {
            Node node = getScene(scene);
            if (node != null) {
                connector.makePendingConnection(SceneState.this, vpState, node, viewport);
            }
        }
        public void breakLink() {
            Node node = getScene(scene);
            if (node != null) {
                connector.makeDeletionContainingRequest(node);
            }
        }
        private void breakConnections(Node node) {
            //connector.breakPendingConnection(node, vpState, viewport);
            //connector.breakFilteredConnections(c -> {
            //    return c.contains(node) && c.getProvider() == vpState;
            //});
        }
        
    }
    
}
