/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.boost.effects.DecalProjector;
import codex.boost.scene.SceneGraphIterator;
import codex.esboost.EntityModel;
import codex.esboost.EntityUtils;
import codex.esboost.components.Decal;
import codex.esboost.components.DecalReciever;
import codex.esboost.components.ModelInfo;
import codex.esboost.factories.Factory;
import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.function.Function;

/**
 *
 * @author codex
 */
public class DecalProjectionState extends GameAppState implements Factory<Material> {

    private EntityData ed;
    private ModelViewState modelState;
    private SceneState sceneState;
    private EntitySet decals, recievers;
    private Factory<Material> factory;
    private final HashMap<EntityId, Geometry> decalMap = new HashMap<>();
    private final Transform tempTransform = new Transform();
    private final Vector2f seperationRange = new Vector2f(0.001f, 0.01f);
    
    public DecalProjectionState() {}
    public DecalProjectionState(Factory<Material> factory) {
        this.factory = factory;
    }
    
    @Override
    protected void init(Application app) {
        ed = EntityUtils.getEntityData(app);
        modelState = getState(ModelViewState.class, true);
        sceneState = getState(SceneState.class);
        decals = ed.getEntities(Decal.class);
        recievers = ed.getEntities(ModelInfo.class, DecalReciever.class);
        if (factory == null) {
            factory = this;
        }
    }
    @Override
    protected void cleanup(Application app) {
        decals.release();
        recievers.release();
        decalMap.clear();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        // Decals will only be projected when the projecting
        // entity is created, in order to save resources.
        if (decals.applyChanges()) {
            if (!decals.getAddedEntities().isEmpty()) {
                recievers.applyChanges();
                // compile a list of recieving geometries
                LinkedList<Geometry> geometries = new LinkedList<>();
                for (Entity e : recievers) {
                    Spatial model = modelState.getSpatial(e.getId());
                    if (model == null) continue;
                    for (Spatial s : new SceneGraphIterator(model)) if (s instanceof Geometry) {
                        geometries.add((Geometry)s);
                    }
                }
                // project each decal
                DecalProjector projector = new DecalProjector();
                projector.setGeometries(geometries);
                GeometryFilter filter = new GeometryFilter();
                projector.setFilter(filter);
                for (Entity e : decals.getAddedEntities()) {
                    Decal decal = e.get(Decal.class);
                    Node parent = rootNode;
                    if (sceneState != null) {
                        if (decal.getScene() != null) {
                            parent = sceneState.getScene(decal.getScene());
                        }
                        filter.setScene(parent);
                    }
                    EntityUtils.getWorldTransform(ed, e.getId(), tempTransform);
                    projector.setTransform(tempTransform);
                    projector.setSize(decal.getSize());
                    // use random seperation to avoid depth fighting to some degree
                    projector.setSeparation(FastMath.nextRandomFloat()*(seperationRange.y-seperationRange.x)+seperationRange.x);
                    Geometry g = projector.project();
                    g.setMaterial(factory.create(decal.getMaterial().getName(ed), true));
                    // temporary, until I figure out a better way
                    g.setQueueBucket(RenderQueue.Bucket.Transparent);
                    decalMap.put(e.getId(), g);
                    parent.attachChild(g);
                }
            }
            for (Entity e : decals.getRemovedEntities()) {
                decalMap.remove(e.getId()).removeFromParent();
            }
        }
    }
    @Override
    public Material create(String name, EntityId customer) {
        if (Factory.isDirectAsset(name)) {
            return assetManager.loadMaterial(Factory.getAssetPath(name));
        }
        return null;
    }
    
    private static class GeometryFilter implements Function<Geometry, Boolean> {

        private Node scene;
        
        public void setScene(Node scene) {
            this.scene = scene;
        }
        
        @Override
        public Boolean apply(Geometry t) {
            return scene == null || t.hasAncestor(scene);
        }
        
    }
    
}
