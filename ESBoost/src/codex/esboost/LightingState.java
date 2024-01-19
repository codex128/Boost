/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost;

import codex.boost.GameAppState;
import codex.esboost.components.EntityLight;
import codex.esboost.components.InfluenceCone;
import codex.esboost.components.Position;
import codex.esboost.components.LightPower;
import codex.esboost.components.ProbeSource;
import com.jme3.app.Application;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightProbe;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.state.GameSystemsState;
import java.util.ArrayList;

/**
 *
 * @author codex
 */
public class LightingState extends GameAppState {
    
    private EntityData ed;
    private final ArrayList<GameEntityContainer<? extends Light>> entities = new ArrayList<>();
    
    @Override
    protected void init(Application app) {
        ed = getState(GameSystemsState.class, true).get(EntityData.class, true);
        entities.add(new DirectionalContainer(ed));
        entities.add(new PointContainer(ed));
        entities.add(new SpotContainer(ed));
        entities.add(new AmbientContainer(ed));
        entities.add(new ProbeContainer(ed));
        entities.forEach(e -> e.start());
    }
    @Override
    protected void cleanup(Application app) {
        entities.forEach(e -> e.stop());
        entities.clear();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        for (GameEntityContainer<? extends Light> c : entities) {
            c.update();
        }
    }
    
    public Light getLight(EntityId id) {
        for (GameEntityContainer<? extends Light> c : entities) {
            Light l = c.getObject(id);
            if (l != null) {
                return l;
            }
        }
        return null;
    }
    public <T extends Light> T getLight(EntityId id, Class<T> type) {
        Light l = getLight(id);
        if (!type.equals(l.getClass())) {
            return null;
        }
        return (T)l;
    }
    
    private class DirectionalContainer extends GameEntityContainer<DirectionalLight> {
        
        private final Transform transform = new Transform();
        
        public DirectionalContainer(EntityData ed) {
            super(ed, filter(EntityLight.DIRECTIONAL), EntityLight.class);
        }
        
        @Override
        protected DirectionalLight addObject(Entity entity) {
            DirectionalLight light = new DirectionalLight();
            lazyObjectUpdate(light, entity);
            persistentObjectUpdate(light, entity);
            rootNode.addLight(light);
            return light;
        }
        @Override
        protected void lazyObjectUpdate(DirectionalLight t, Entity entity) {
            t.setColor(entity.get(EntityLight.class).getColor());
        }
        @Override
        protected void persistentObjectUpdate(DirectionalLight t, Entity entity) {
            t.setDirection(GameUtils.getWorldTransform(ed, entity.getId(), transform).getRotation().mult(Vector3f.UNIT_Z));            
        }
        @Override
        protected void removeObject(DirectionalLight t, Entity entity) {
            rootNode.removeLight(t);
        }
        
    }
    private class PointContainer extends GameEntityContainer<PointLight> {
        
        private final Transform transform = new Transform();
        
        public PointContainer(EntityData ed) {
            super(ed, filter(EntityLight.POINT), EntityLight.class, LightPower.class);
        }
        
        @Override
        protected PointLight addObject(Entity entity) {
            PointLight light = new PointLight();
            lazyObjectUpdate(light, entity);
            persistentObjectUpdate(light, entity);
            rootNode.addLight(light);
            return light;
        }
        @Override
        protected void lazyObjectUpdate(PointLight t, Entity entity) {
            t.setRadius(entity.get(LightPower.class).getPower());
            t.setColor(entity.get(EntityLight.class).getColor());
        }
        @Override
        protected void persistentObjectUpdate(PointLight t, Entity entity) {
            t.setPosition(GameUtils.getWorldTransform(ed, entity.getId(), transform).getTranslation());
        }
        @Override
        protected void removeObject(PointLight t, Entity entity) {
            rootNode.removeLight(t);
        }
        
    }
    private class SpotContainer extends GameEntityContainer<SpotLight> {
        
        private final Transform transform = new Transform();

        public SpotContainer(EntityData ed) {
            super(ed, filter(EntityLight.SPOT), EntityLight.class, LightPower.class, InfluenceCone.class);
        }

        @Override
        protected SpotLight addObject(Entity entity) {
            SpotLight light = new SpotLight();
            lazyObjectUpdate(light, entity);
            persistentObjectUpdate(light, entity);
            rootNode.addLight(light);
            return light;
        }
        @Override
        protected void lazyObjectUpdate(SpotLight t, Entity entity) {
            t.setSpotRange(entity.get(LightPower.class).getPower());
            entity.get(InfluenceCone.class).applyToSpotLight(t);
            t.setColor(entity.get(EntityLight.class).getColor());
        }
        @Override
        protected void persistentObjectUpdate(SpotLight t, Entity entity) {
            GameUtils.getWorldTransform(ed, entity.getId(), transform);
            t.setPosition(transform.getTranslation());
            t.setDirection(transform.getRotation().mult(Vector3f.UNIT_Z));
        }
        @Override
        protected void removeObject(SpotLight t, Entity entity) {
            rootNode.removeLight(t);
        }
        
    }
    private class AmbientContainer extends GameEntityContainer<AmbientLight> {

        public AmbientContainer(EntityData ed) {
            super(ed, filter(EntityLight.AMBIENT), EntityLight.class);
        }

        @Override
        protected AmbientLight addObject(Entity entity) {
            AmbientLight light = new AmbientLight();
            lazyObjectUpdate(light, entity);
            rootNode.addLight(light);
            return light;
        }
        @Override
        protected void lazyObjectUpdate(AmbientLight t, Entity entity) {
            t.setColor(entity.get(EntityLight.class).getColor());
        }
        @Override
        protected void persistentObjectUpdate(AmbientLight t, Entity entity) {}
        @Override
        protected void removeObject(AmbientLight t, Entity entity) {
            rootNode.removeLight(t);
        }
        
    }
    private class ProbeContainer extends GameEntityContainer<LightProbe> {
        
        private final Transform transform = new Transform();

        public ProbeContainer(EntityData ed) {
            super(ed, filter(EntityLight.PROBE), EntityLight.class, Position.class, ProbeSource.class);
        }
        
        @Override
        protected LightProbe addObject(Entity e) {
            Node probeNode = (Node)assetManager.loadModel(e.get(ProbeSource.class).getPath());
            LightProbe p = (LightProbe)probeNode.getLocalLightList().iterator().next();
            if (p == null) {
                throw new NullPointerException("Failed to locate light probe from "+e.get(ProbeSource.class));
            }
            rootNode.addLight(p);
            return p;
        }
        @Override
        protected void lazyObjectUpdate(LightProbe object, Entity e) {}
        @Override
        protected void persistentObjectUpdate(LightProbe object, Entity e) {
            GameUtils.getWorldTransform(ed, e.getId(), transform);
            object.setPosition(transform.getTranslation());
        }
        @Override
        protected void removeObject(LightProbe object, Entity e) {
            rootNode.removeLight(object);
        }
        
    }
    
    private static FunctionFilter<EntityLight> filter(int lightType) {
        return new FunctionFilter<>(EntityLight.class, c -> c.getType() == lightType);
    }
    
}