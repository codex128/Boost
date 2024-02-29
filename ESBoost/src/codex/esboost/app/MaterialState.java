/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.boost.scene.SceneGraphIterator;
import codex.esboost.components.MatValue;
import codex.esboost.components.MaterialInfo;
import codex.esboost.components.ModelInfo;
import codex.esboost.components.TargetTo;
import codex.esboost.factories.Factory;
import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.state.GameSystemsState;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public class MaterialState extends GameAppState implements Factory<Material> {
    
    private EntityData ed;
    private ModelViewState modelState;
    private EntitySet materials, matParams;
    private Factory<Material> factory;
    private final HashMap<EntityId, Material> materialMap = new HashMap<>();
    private final LinkedList<CachedMaterial> matCache = new LinkedList<>();
    private final LinkedList<CachedMatParam> paramCache = new LinkedList<>();

    public MaterialState() {}
    public MaterialState(Factory<Material> factory) {
        this.factory = factory;
    }
    
    @Override
    protected void init(Application app) {
        ed = getState(GameSystemsState.class, true).get(EntityData.class, true);
        modelState = getState(ModelViewState.class, true);
        materials = ed.getEntities(ModelInfo.class, MaterialInfo.class);
        matParams = ed.getEntities(MatValue.class, TargetTo.class);
        if (factory == null) {
            factory = this;
        }
    }
    @Override
    protected void cleanup(Application app) {
        materials.release();
        matParams.release();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        applyCachedMaterials();
        if (materials.applyChanges()) {
            materials.getAddedEntities().forEach(e -> updateModelMaterial(e));
            materials.getChangedEntities().forEach(e -> updateModelMaterial(e));
        }
        applyCachedParams();
        if (matParams.applyChanges()) {
            matParams.getAddedEntities().forEach(e -> updateMaterialParams(e));
            matParams.getChangedEntities().forEach(e -> updateMaterialParams(e));
        }
    }
    @Override
    public Material create(String name, EntityId customer) {
        if (Factory.isDirectAsset(name)) {
            return assetManager.loadMaterial(Factory.getAssetPath(name));
        }
        return assetManager.loadMaterial(name);
    }
    
    private void updateModelMaterial(Entity e) {
        //ModelViewState.ModelView view = models.getObject(e.getId());
        Spatial spatial = modelState.getSpatial(e.getId());
        String name = e.get(MaterialInfo.class).getPrefab().getName(ed);
        Material mat = factory.create(name, e.getId(), true);
        mat.setName(name);
        materialMap.put(e.getId(), mat);
        if (spatial != null) {
            spatial.setMaterial(mat);
        } else {
            matCache.add(new CachedMaterial(e, mat));
        }
    }
    private void applyCachedMaterials() {
        if (matCache.isEmpty()) return;
        for (Iterator<CachedMaterial> it = matCache.iterator(); it.hasNext();) {
            CachedMaterial m = it.next();
            Spatial spatial = modelState.getSpatial(m.entity.getId());
            if (spatial != null) {
                spatial.setMaterial(m.material);
                it.remove();
            }
        }
    }
    private void updateMaterialParams(Entity e) {
        //ModelViewState.ModelView view = models.getObject(e.get(TargetTo.class).getTargetId());
        Spatial spatial = modelState.getSpatial(e.getId());
        MatValue value = e.get(MatValue.class);
        if (spatial == null) {
            // cache the settings to set them later
            paramCache.add(new CachedMatParam(e, value, e.get(TargetTo.class).getTargetId()));
        } else {
            applyParamToMaterial(e, value, spatial);
        }
    }
    private void applyParamToMaterial(Entity e, MatValue value, Spatial spatial) {
        Material mat = materialMap.get(e.getId());
        if (mat != null) {
            setMatParam(mat, value);
        } else for (Spatial s : new SceneGraphIterator(spatial)) if (s instanceof Geometry) {
            setMatParam(((Geometry)s).getMaterial(), value);
        }
    }
    private void setMatParam(Material mat, MatValue value) {
        if (mat.getMaterialDef().getMaterialParam(value.getName()) == null);
        else if (value.getValue() != null) {
            mat.setParam(value.getName(), value.getType(), value.getValue());
        } else {
            // clear the parameter (may force shaders to be reloaded)
            mat.clearParam(value.getName());
        }
    }
    private void applyCachedParams() {
        if (paramCache.isEmpty()) return;
        for (Iterator<CachedMatParam> it = paramCache.iterator(); it.hasNext();) {
            CachedMatParam p = it.next();
            //ModelViewState.ModelView view = models.getObject(p.target);
            Spatial spatial = modelState.getSpatial(p.target);
            if (spatial == null) {
                continue;
            }
            applyParamToMaterial(p.entity, p.value, spatial);
            it.remove();
        }
    }
    
    public Material getMaterial(EntityId id) {
        return materialMap.get(id);
    }
    
    private class CachedMaterial {
        
        public final Entity entity;
        public final Material material;

        public CachedMaterial(Entity entity, Material material) {
            this.entity = entity;
            this.material = material;
        }
        
    }
    private class CachedMatParam {
        
        public final Entity entity;
        public final MatValue value;
        public final EntityId target;

        public CachedMatParam(Entity entity, MatValue value, EntityId target) {
            this.entity = entity;
            this.value = value;
            this.target = target;
        }
        
    }
    
}
