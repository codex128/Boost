/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.boost.scene.SceneGraphIterator;
import codex.esboost.BoostEntityContainer;
import codex.esboost.EntityUtils;
import codex.esboost.bullet.CollisionShapeCache;
import codex.esboost.bullet.GeometricShape;
import codex.esboost.components.*;
import codex.esboost.factories.*;
import com.jme3.app.Application;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import com.jme3.material.Material;
import com.jme3.math.Transform;
import com.simsilica.bullet.CollisionShapes;
import com.simsilica.bullet.ShapeInfo;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.state.GameSystemsState;
import java.util.HashMap;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author codex
 */
public class ModelViewState extends GameAppState {
    
    private static final Logger Log = LoggerFactory.getLogger(ModelViewState.class);
    
    /**
     * Userdata key for marking a model as an entity in a scene.
     */
    public static final String ENTITY = "Entity";
    
    /**
     * Userdata key for marking a scene is belonging to an entity.
     */
    public static final String SCENE_ID_USERDATA = "ModelViewState[sceneid]";
    
    /**
     * {@link Prefab} name for entities that want to retrieve a model from the cache.
     */
    public static final String CACHE = "ModelViewState[cachedmodel]";
    
    /**
     * Indicates that the entity wants to steal another entity's model.
     */
    public static final String NECRO = "ModelViewState[necro]";
    
    private EntityData ed;
    private ModelContainer models;
    private EntitySet geometryShapes;
    private Factory<Spatial> modelFactory;
    private Factory<Design> designFactory;
    private CollisionShapeCache shapeCache;
    private final HashMap<EntityId, Spatial> modelCache = new HashMap<>();
    
    public ModelViewState(Factory<Spatial> modelFactory) {
        this.modelFactory = modelFactory;
    }
    
    @Override
    protected void init(Application app) {
        ed = getState(GameSystemsState.class, true).get(EntityData.class, true);
        models = new ModelContainer();
        geometryShapes = ed.getEntities(ModelInfo.class, GeometricShapeInfo.class);
        CollisionShapes shapes = getState(GameSystemsState.class, true).get(CollisionShapes.class);
        if (shapes != null && shapes instanceof CollisionShapeCache) {
            shapeCache = (CollisionShapeCache)shapes;
        }
    }
    @Override
    protected void cleanup(Application app) {
        geometryShapes.release();
        modelCache.clear();
    }
    @Override
    protected void onEnable() {
        models.start();
    }
    @Override
    protected void onDisable() {
        models.stop();
    }
    @Override
    public void update(float tpf) {
        // check for new geometry physics shape entities
        boolean shapesUpdated = geometryShapes.applyChanges();
        models.update();
        if (shapesUpdated) {
            geometryShapes.getAddedEntities().forEach(e -> createGeometicCollisionShape(e));
        }
    }
    
    public void setDesignFactory(Factory<Design> factory) {
        this.designFactory = factory;
    }
    public Spatial getSpatial(EntityId id) {
        ModelView view = models.getObject(id);
        if (view == null) {
            Spatial spatial = modelCache.get(id);
            if (spatial != null) {
                return spatial;
            }
            return null;
        }
        return view.spatial;
    }
    
    private Spatial createModel(EntityId customer, ModelInfo info) {
        // check if a model is already cached for this entity
        Spatial spatial = modelCache.remove(customer);
        if (spatial == null) {
            // manufacture a new model for the entity
            if (info.getName(ed).equals(NECRO)) {
                NecroTarget id = ed.getComponent(customer, NecroTarget.class);
                if (id == null) {
                    throw new NullPointerException("Necro model must have a target.");
                }
                ModelView target = models.getObject(id.getTarget());
                if (target.necro != null) {
                    throw new IllegalStateException("Cannot necro a model that has already been necroed.");
                }
                target.necro = customer;
                spatial = target.spatial;
            } else {
                spatial = modelFactory.create(info.getPrefab().getName(ed), customer);
                if (spatial == null) {
                    throw new NullPointerException("Prefab \""+info.getPrefab().getName(ed)+"\" failed to manufacture model.");
                }
            }
            EntityUtils.appendId(customer, spatial);
        }
        return spatial;
    }
    private void cacheModel(EntityId id, Spatial spatial) {
        modelCache.put(id, spatial);
    }
    
    private void createGeometicCollisionShape(Entity e) {
        if (shapeCache == null) {
            Log.warn("No CollisionShapeCache defined; cannot create geometric shape. Skipping.");
            return;
        }
        Spatial spatial = getSpatial(e.getId());
        if (spatial == null) return;
        GeometricShapeInfo g = e.get(GeometricShapeInfo.class);
        ShapeInfo info = shapeCache.registerTemporary(GeometricShape.createCollisionShape(
                Enum.valueOf(GeometricShape.class, g.getType()), spatial, g.getVhacd(), g.getVhacd4()));
        ed.setComponent(e.getId(), info);
    }
    private void prepareCompoundModel(Spatial model) {
        if (designFactory == null) {
            Log.warn("No design factory defined; cannot prepare compound model. Skipping.");
            return;
        }
        LinkedList<Spatial> list = new LinkedList<>();
        for (Spatial spatial : new SceneGraphIterator(model)) {
            if (spatial == model) {
                continue;
            }
            String name = spatial.getUserData(ENTITY);
            if (name == null) {
                continue;
            }
            Design design = designFactory.create(name);
            if (design == null) {
                continue;
            }
            design.create(spatial);
            if (design.getMainEntity() == null) {
                continue;
            }
            EntityId parent = EntityUtils.fetchId(spatial, -1);
            if (parent != null) {
                ed.setComponent(design.getMainEntity(), new Parent(parent));
            }
            // check if the entity actually wants to control that spatial
            ModelInfo info = ed.getComponent(design.getMainEntity(), ModelInfo.class);
            if (info != null && info.getName(ed).equals(CACHE)) {
                EntityUtils.appendId(design.getMainEntity(), spatial);
                cacheModel(design.getMainEntity(), spatial);
                list.add(spatial);
            }
        }
        for (Spatial spatial : list) {
            spatial.removeFromParent();
            // attaching now may have been causing incorrect positioning
            //rootNode.attachChild(spatial);
        }
        list.clear();
    }
    
    private class ModelView {
        
        private final Entity entity;
        private Spatial spatial;
        private Material material;
        private String prefabName;
        private EntityId necro;
        private final Transform tempTransform = new Transform();
        
        public ModelView(Entity entity) {
            this.entity = entity;
            ModelInfo info = entity.get(ModelInfo.class);
            spatial = createModel(entity.getId(), info);
            if (spatial.getParent() == null) {
                rootNode.attachChild(spatial);
            }
            prefabName = info.getName(ed);
            if (material != null) {
                spatial.setMaterial(material);
            }
            persistentUpdate();
            CompoundModel s = ed.getComponent(entity.getId(), CompoundModel.class);
            if (s != null) {
                prepareCompoundModel(spatial);
            }
        }
        
        public final void persistentUpdate() {
            if (necro == null) {
                EntityUtils.getWorldTransform(ed, entity.getId(), tempTransform);
                spatial.setLocalTransform(tempTransform);
            }
        }
        
        public void destroy() {
            if (necro == null) {
                EntityUtils.appendId(null, spatial);
                spatial.removeFromParent();
            }
        }
        
    }
    private class ModelContainer extends BoostEntityContainer<ModelView> {

        public ModelContainer() {
            super(ed, ModelInfo.class);
        }
        
        @Override
        protected ModelView addObject(Entity entity) {
            return new ModelView(entity);
        }
        @Override
        protected void lazyObjectUpdate(ModelView t, Entity entity) {}
        @Override
        public void persistentObjectUpdate(ModelView t, Entity entity) {
            t.persistentUpdate();
        }
        @Override
        protected void removeObject(ModelView t, Entity entity) {
            t.destroy();
        }
        
    }
    
    private class CachedMatParam {
        
        public final MatValue value;
        public final EntityId target;

        public CachedMatParam(MatValue value, EntityId target) {
            this.value = value;
            this.target = target;
        }
        
    }
    
}
