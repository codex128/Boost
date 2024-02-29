/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.components.ModelInfo;
import codex.esboost.components.Scene;
import codex.esboost.components.SceneMember;
import codex.esboost.factories.Factory;
import com.jme3.app.Application;
import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
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
public class SceneMapState extends GameAppState implements Factory<Node> {
    
    //public static final String ROOT_NODE = "SceneMapState[rootnode]";
    //public static final String GUI_NODE = "SceneMapState[guinode]";
    
    private EntityData ed;
    private EntitySet scenes;
    private Factory<Node> factory;
    private EntityId rootNodeId, guiNodeId;
    private final HashMap<EntityId, Node> sceneMap = new HashMap<>();
    
    public SceneMapState() {}
    public SceneMapState(Factory<Node> factory) {
        this.factory = factory;
    }
    
    @Override
    protected void init(Application app) {
        ed = getState(GameSystemsState.class, true).get(EntityData.class, true);
        scenes = ed.getEntities(Scene.class);
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
                sceneMap.put(e.getId(), factory.create(e.get(Scene.class).getPrefab().getName(ed), e.getId(), true));
            }
            for (Entity e : scenes.getRemovedEntities()) {
                Node n = sceneMap.remove(e.getId());
                n.detachAllChildren();
            }
        }
    }
    @Override
    public Node create(String name, EntityId customer) {
        return new Node(name);
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
    
}
