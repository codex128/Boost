/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost;

import codex.boost.GameAppState;
import codex.boost.scene.SceneGraphIterator;
import codex.esboost.bullet.GeometricShape;
import codex.esboost.components.*;
import codex.esboost.factories.Design;
import codex.esboost.factories.Factory;
import codex.esboost.factories.Prefab;
import com.jme3.anim.SkinningControl;
import com.jme3.app.Application;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import com.jme3.anim.Joint;
import com.jme3.material.Material;
import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.simsilica.bullet.CollisionShapes;
import com.simsilica.bullet.ShapeInfo;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.state.GameSystemsState;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public class ModelViewState extends GameAppState {
    
    /**
     * Userdata key for marking a model as an entity in a scene.
     */
    public static final String ENTITY = "Entity";
    
    /**
     * {@link Prefab} name for entities that want to retrieve a model from the cache.
     */
    public static final String CACHE = "[cached_model]";
    
    private EntityData ed;
    private ModelContainer models;
    private EntitySet bonePosition, boneRotation, materials, geometryShapes;
    private Factory<Spatial> modelFactory;
    private Factory<Design> designFactory;
    private CollisionShapes shapes;
    private final HashMap<EntityId, Spatial> modelCache = new HashMap<>();
    private final LinkedList<CachedMatParam> matCache = new LinkedList<>();
    
    public ModelViewState() {}
    public ModelViewState(Factory<Spatial> modelFactory) {
        this.modelFactory = modelFactory;
    }
    
    @Override
    protected void init(Application app) {
        ed = getState(GameSystemsState.class).get(EntityData.class);
        models = new ModelContainer();
        System.out.println(1);
        bonePosition = ed.getEntities(BoneInfo.class, ApplyBonePosition.class, Position.class);
        boneRotation = ed.getEntities(BoneInfo.class, ApplyBoneRotation.class, Rotation.class);
        materials = ed.getEntities(MatValue.class, TargetTo.class);
        geometryShapes = ed.getEntities(ModelInfo.class, GeometricShapeInfo.class);
        shapes = getState(GameSystemsState.class).get(CollisionShapes.class);
        if (shapes == null) {
            throw new NullPointerException("Could not locate collision shapes!");
        }
    }
    @Override
    protected void cleanup(Application app) {
        bonePosition.release();
        boneRotation.release();
        materials.release();
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
        boolean gsu = geometryShapes.applyChanges();
        models.update();
        if (bonePosition.applyChanges()) {
            bonePosition.getAddedEntities().forEach(e -> updateBonePositionFromEntity(e));
            bonePosition.getChangedEntities().forEach(e -> updateBonePositionFromEntity(e));
        }
        bonePosition.forEach(e -> updateEntityPositionFromBone(e));
        if (boneRotation.applyChanges()) {
            boneRotation.getAddedEntities().forEach(e -> updateBoneRotationFromEntity(e));
            boneRotation.getChangedEntities().forEach(e -> updateBoneRotationFromEntity(e));
        }
        boneRotation.forEach(e -> updateEntityRotationFromBone(e));
        applyCachedMatValues();
        if (materials.applyChanges()) {
            materials.getAddedEntities().forEach(e -> updateMaterial(e));
            materials.getChangedEntities().forEach(e -> updateMaterial(e));
        }
        if (gsu) {
            geometryShapes.getAddedEntities().forEach(e -> createGeometryShape(e));
        }
    }
    
    private void updateBonePositionFromEntity(Entity e) {        
        if (e.get(ApplyBonePosition.class).isDirection() != ApplyBonePosition.ENTITY_TO_BONE) {
            return;
        }
        Joint j = getBone(e);
        if (j == null) return;
        j.setLocalTranslation(e.get(Position.class).getPosition());
    }
    private void updateEntityPositionFromBone(Entity e) {
        if (e.get(ApplyBonePosition.class).isDirection() != ApplyBonePosition.BONE_TO_ENTITY) {
            return;
        }
        Joint j = getBone(e);
        if (j == null) return;
        e.set(new Position(j.getLocalTranslation()));
    }
    private void updateBoneRotationFromEntity(Entity e) {
        if (e.get(ApplyBoneRotation.class).isDirection() != ApplyBoneRotation.ENTITY_TO_BONE) {
            return;
        }
        Joint j = getBone(e);
        if (j == null) return;
        j.setLocalRotation(e.get(Rotation.class).getRotation());
    }
    private void updateEntityRotationFromBone(Entity e) {
        if (e.get(ApplyBoneRotation.class).isDirection() != ApplyBoneRotation.BONE_TO_ENTITY) {
            return;
        }
        Joint j = getBone(e);
        if (j == null) return;
        e.set(new Rotation(j.getLocalRotation()));
    }
    private Joint getBone(Entity e) {
        ModelView view = models.getObject(e.get(BoneInfo.class).getModel());
        if (view == null) return null;
        SkinningControl skin = view.spatial.getControl(SkinningControl.class);
        if (skin == null) return null;
        return skin.getArmature().getJoint(e.get(BoneInfo.class).getBone());
    }
    
    private void updateMaterial(Entity e) {
        ModelView view = models.getObject(e.get(TargetTo.class).getTargetId());
        MatValue value = e.get(MatValue.class);
        if (view == null) {
            // cache the settings to set them later
            matCache.add(new CachedMatParam(value, e.get(TargetTo.class).getTargetId()));
        } else {
            applyToMaterial(view.spatial, value);
        }
    }
    private void applyToMaterial(Spatial spatial, MatValue value) {
        for (Spatial s : new SceneGraphIterator(spatial)) if (s instanceof Geometry) {
            Material mat = ((Geometry)s).getMaterial();
            // check if the parameter exists
            if (mat.getMaterialDef().getMaterialParam(value.getName()) == null);
            else if (value.getValue() != null) {
                mat.setParam(value.getName(), value.getType(), value.getValue());
            } else {
                // clear the parameter (may force shaders to be reloaded)
                mat.clearParam(value.getName());
            }
        }
    }
    private void applyCachedMatValues() {
        if (matCache.isEmpty()) return;
        for (CachedMatParam p : matCache) {
            ModelView view = models.getObject(p.target);
            if (view == null) {
                continue;
            }
            applyToMaterial(view.spatial, p.value);
        }
        matCache.clear();
    }
    private void createGeometryShape(Entity e) {
        Spatial spatial = getSpatial(e.getId());
        if (spatial == null) return;
        GeometricShapeInfo g = e.get(GeometricShapeInfo.class);
        ShapeInfo info = new ShapeInfo(g.getPrefab().getId());
        if (shapes.getShape(info) == null) {
            shapes.register(info, GameUtils.createGeometricCollisionShape(Enum.valueOf(GeometricShape.class, g.getType()), spatial));
            if (ed.getComponent(e.getId(), ShapeInfo.class) == null) {
                ed.setComponent(e.getId(), info);
            }
        }
        // consume the activator component
        ed.removeComponent(e.getId(), GeometricShapeInfo.class);
    }
    private void prepareScene(Spatial scene) {
        LinkedList<Spatial> list = new LinkedList<>();
        for (Spatial spatial : new SceneGraphIterator(scene)) {
            if (spatial == scene) {
                continue;
            }
            String name = spatial.getUserData(ENTITY);
            if (name == null) {
                continue;
            }
            Design d = designFactory.create(name, null);
            if (d == null) {
                continue;
            }
            EntityId id = d.create(spatial);
            if (id != null) {
                EntityId parent = GameUtils.fetchId(spatial, -1);
                if (parent != null) {
                    ed.setComponent(id, new Parent(parent));
                }
                // check if the entity actually wants to control that spatial
                ModelInfo model = ed.getComponent(id, ModelInfo.class);
                if (model != null && model.getName(ed).equals(CACHE)) {
                    GameUtils.appendId(id, spatial);
                    cacheModel(id, spatial);
                    list.add(spatial);
                }
            }
        }
        for (Spatial spatial : list) {
            spatial.removeFromParent();
            // attaching spatials now may have been causing them to not be positioned correctly
            //rootNode.attachChild(spatial);
        }
        list.clear();
    }
    
    private Spatial createModel(EntityId customer, ModelInfo info) {
        // check if a model is already cached for this entity
        Spatial spatial = modelCache.remove(customer);
        if (spatial == null) {
            // manufacture a new model for the entity
            //factoryInfo.setPrefab(info.getPrefab());
            //factoryInfo.setCustomer(customer);
            spatial = modelFactory.create(info.getPrefab().getName(ed), customer);
            if (spatial == null) {
                throw new NullPointerException("Prefab \""+info.getPrefab().getName(ed)+"\" failed to manufacture model!");
            }
        }
        GameUtils.appendId(customer, spatial);
        return spatial;
    }
    private void cacheModel(EntityId id, Spatial spatial) {
        modelCache.put(id, spatial);
    }
    public Spatial getSpatial(EntityId id) {
        ModelView view = models.getObject(id);
        if (view == null) return null;
        return view.spatial;
    }
    
    private class ModelView {
        
        private final Entity entity;
        private final Spatial spatial;
        private final Transform tempTransform = new Transform();
        
        public ModelView(Entity entity) {
            this.entity = entity;
            spatial = createModel(entity.getId(), entity.get(ModelInfo.class));
            lazyUpdate();
            persistentUpdate();
            Scene s = ed.getComponent(entity.getId(), Scene.class);
            if (s != null) {
                prepareScene(spatial);
            }
        }
        
        public final void lazyUpdate() {
            boolean visible = entity.get(ModelInfo.class).isVisible();
            if (visible && spatial.getParent() == null) {
                rootNode.attachChild(spatial);
            } else if (!visible && spatial.getParent() != null) {
                spatial.removeFromParent();
            }
        }
        public final void persistentUpdate() {
            GameUtils.getWorldTransform(ed, entity.getId(), tempTransform);
            spatial.setLocalTransform(tempTransform);
        }
        public void destroy() {
            spatial.removeFromParent();
        }
        
    }
    private class ModelContainer extends GameEntityContainer<ModelView> {

        public ModelContainer() {
            super(ed, ModelInfo.class);
        }
        
        @Override
        protected ModelView addObject(Entity entity) {
            return new ModelView(entity);
        }
        @Override
        protected void lazyObjectUpdate(ModelView t, Entity entity) {
            t.lazyUpdate();
        }
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
