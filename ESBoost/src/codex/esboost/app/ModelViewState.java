/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.BoostEntityContainer;
import codex.esboost.connection.ConnectionManager;
import codex.esboost.EntityModel;
import codex.esboost.EntityUtils;
import codex.esboost.bullet.CollisionShapeCache;
import codex.esboost.bullet.GeometricShape;
import codex.esboost.components.*;
import codex.esboost.connection.Connector;
import codex.esboost.connection.Delete;
import codex.esboost.connection.Provider;
import codex.esboost.factories.*;
import com.jme3.app.Application;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import com.jme3.scene.SceneGraphIterator;
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
public class ModelViewState extends GameAppState implements
        Connector<EntityModel, Node>, Provider<EntityModel, EntityId> {
    
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
    private ConnectionManager connector;
    private SceneState sceneState;
    private ModelContainer models;
    private EntitySet sceneModels, geometryShapes;
    private final Factory<Spatial> modelFactory;
    private Factory<Design> designFactory;
    private CollisionShapeCache shapeCache;
    private final HashMap<EntityId, Spatial> modelCache = new HashMap<>();
    
    public ModelViewState(Factory<Spatial> modelFactory) {
        this.modelFactory = modelFactory;
    }
    
    @Override
    protected void init(Application app) {
        ed = EntityUtils.getEntityData(app);
        connector = EntityUtils.getConnectionManager(app);
        sceneState = getState(SceneState.class, true);
        models = new ModelContainer();
        geometryShapes = ed.getEntities(EntityModel.class, GeometricShapeInfo.class);
        if (sceneState != null) {
            sceneModels = ed.getEntities(EntityModel.class, SceneMember.class);
        }
        CollisionShapes shapes = getState(GameSystemsState.class, true).get(CollisionShapes.class);
        if (shapes != null && shapes instanceof CollisionShapeCache) {
            shapeCache = (CollisionShapeCache)shapes;
        }
    }
    @Override
    protected void cleanup(Application app) {
        geometryShapes.release();
        sceneModels.release();
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
        boolean shapesUpdated = geometryShapes.applyChanges();
        models.update();
        if (shapesUpdated) {
            geometryShapes.getAddedEntities().forEach(e -> createGeometricCollisionShape(e));
        }
        connector.applyChanges(this);
        if (sceneModels != null && sceneModels.applyChanges()) {
            sceneModels.getAddedEntities().forEach(e -> assignModelToScene(e, true));
        }
    }
    @Override
    public void connect(EntityModel model, Node scene) {
        scene.attachChild(model.getModel());
    }
    @Override
    public void disconnect(EntityModel model, Node scene) {
        scene.detachChild(model.getModel());
    }
    @Override
    public MultiConnectionHint getMultiConnectionHint() {
        return Connector.MultiConnectionHint.OnlyObjectB;
    }
    @Override
    public EntityModel fetchConnectingObject(EntityId key) {
        return models.getObject(key);
    }
    
    public void setDesignFactory(Factory<Design> factory) {
        this.designFactory = factory;
    }
    
    public EntityModel getModel(EntityId id) {
        return models.getObject(id);
    }
    public Spatial getSpatial(EntityId id) {
        EntityModel view = models.getObject(id);
        if (view == null) {
            return modelCache.get(id);
        }
        return view.getModel();
    }
    
    private Spatial createModel(EntityId customer, ModelInfo info) {
        // check if a model is already cached for this entity
        Spatial spatial = modelCache.remove(customer);
        if (spatial == null) {
            if (info.getName(ed).equals(NECRO)) {
                // steal another entity's model
                NecroTarget id = ed.getComponent(customer, NecroTarget.class);
                if (id == null) {
                    throw new NullPointerException("Necro model must have a target.");
                }
                EntityModel target = models.getObject(id.getTarget());
                if (target.getNecro() != null) {
                    throw new IllegalStateException("Cannot necro a model that has already been necroed.");
                }
                target.setNecro(customer);
                spatial = target.getModel();
            } else {
                // manufacture a new model for the entity
                spatial = modelFactory.create(info.getPrefab().getName(ed), customer, true);
            }
            EntityUtils.appendId(customer, spatial);
        }
        return spatial;
    }
    private void cacheModel(EntityId id, Spatial spatial) {
        modelCache.put(id, spatial);
    }
    private void createGeometricCollisionShape(Entity e) {
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
                Log.warn("Spatial design name \""+name+"\" failed to create design.");
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
            // attaching now was causing incorrect positioning, for some reason
            //rootNode.attachChild(spatial);
        }
        list.clear();
    }
    private void assignModelToScene(Entity e, boolean attach) {
        EntityModel model = models.getObject(e.getId());
        if (attach) {
            connector.makePendingConnection(this, sceneState, model, e.get(SceneMember.class).getScene());
        } else {
            connector.makeDeletionRequest(sceneState, Delete.containing(model));
        }
    }
    
    private class ModelContainer extends BoostEntityContainer<EntityModel> {

        public ModelContainer() {
            super(ed, ModelInfo.class);
        }
        
        @Override
        protected EntityModel addObject(Entity entity) {
            Spatial spatial = createModel(entity.getId(), entity.get(ModelInfo.class));
            EntityModel model = new EntityModel(entity.getId(), spatial);
            connector.makePendingConnection(ModelViewState.this, sceneState, model, sceneState.getRootNode());
            CompoundModel s = ed.getComponent(entity.getId(), CompoundModel.class);
            if (s != null) {
                prepareCompoundModel(spatial);
            }
            return model;
        }
        @Override
        protected void lazyObjectUpdate(EntityModel t, Entity entity) {}
        @Override
        public void persistentObjectUpdate(EntityModel t, Entity entity) {
            t.updateTransform(ed);
        }
        @Override
        protected void removeObject(EntityModel t, Entity entity) {
            EntityUtils.appendId(null, t.getModel());
            connector.makeDeletionContainingRequest(t);
        }
        
    }
    
}
