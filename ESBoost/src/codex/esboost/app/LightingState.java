/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.BoostEntityContainer;
import codex.esboost.EntityUtils;
import codex.esboost.render.SceneLight;
import codex.esboost.components.EntityLight;
import codex.esboost.components.InfluenceCone;
import codex.esboost.components.LightPower;
import codex.esboost.components.ProbeInfo;
import codex.esboost.factories.Factory;
import com.jme3.app.Application;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import java.util.ArrayList;

/**
 *
 * @author codex
 */
public class LightingState extends GameAppState {
    
    private EntityData ed;
    private final Factory<LightProbe> probeFactory;
    private final ArrayList<BoostEntityContainer<? extends SceneLight>> entities = new ArrayList<>();

    public LightingState(Factory<LightProbe> probeFactory) {
        this.probeFactory = probeFactory;
    }
    
    @Override
    protected void init(Application app) {
        System.out.println("initialize lighting state");
        ed = EntityUtils.getEntityData(app);
        System.out.println(0);
        entities.add(new DirectionalContainer(ed));
        System.out.println(1);
        entities.add(new PointContainer(ed));
        System.out.println(2);
        entities.add(new SpotContainer(ed));
        System.out.println(3);
        entities.add(new AmbientContainer(ed));
        System.out.println(4);
        entities.add(new PrebuiltProbeContainer(ed));
        System.out.println(5);
        entities.forEach(e -> e.start());
        System.out.println("finish lighting state init");
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
        for (BoostEntityContainer c : entities) {
            c.update();
        }
    }
    
    public SceneLight getLight(EntityId id) {
        for (BoostEntityContainer<? extends SceneLight> c : entities) {
            SceneLight l = c.getObject(id);
            if (l != null) {
                return l;
            }
        }
        return null;
    }
    
    private class DirectionalContainer extends BoostEntityContainer<SceneLight<DirectionalLight>> {
        
        private final Transform transform = new Transform();
        
        public DirectionalContainer(EntityData ed) {
            super(ed, EntityLight.filter(EntityLight.DIRECTIONAL), EntityLight.class);
        }
        
        @Override
        protected SceneLight<DirectionalLight> addObject(Entity entity) {
            //DirectionalLight light = new DirectionalLight();
            SceneLight<DirectionalLight> light = new SceneLight<>(new DirectionalLight());
            lazyObjectUpdate(light, entity);
            persistentObjectUpdate(light, entity);
            light.assign(rootNode);
            return light;
        }
        @Override
        protected void lazyObjectUpdate(SceneLight<DirectionalLight> t, Entity entity) {
            t.getLight().setColor(entity.get(EntityLight.class).getColor());
        }
        @Override
        protected void persistentObjectUpdate(SceneLight<DirectionalLight> t, Entity entity) {
            t.getLight().setDirection(EntityUtils.getWorldTransform(ed, entity.getId(), transform).getRotation().mult(Vector3f.UNIT_Z));            
        }
        @Override
        protected void removeObject(SceneLight<DirectionalLight> t, Entity entity) {
            t.assign(null);
        }
        
    }
    private class PointContainer extends BoostEntityContainer<SceneLight<PointLight>> {
        
        private final Transform transform = new Transform();
        
        public PointContainer(EntityData ed) {
            super(ed, EntityLight.filter(EntityLight.POINT), EntityLight.class, LightPower.class);
        }
        
        @Override
        protected SceneLight<PointLight> addObject(Entity entity) {
            System.out.println("create point light");
            //PointLight light = new PointLight();
            SceneLight<PointLight> light = new SceneLight(new PointLight());
            lazyObjectUpdate(light, entity);
            persistentObjectUpdate(light, entity);
            light.assign(rootNode);
            return light;
        }
        @Override
        protected void lazyObjectUpdate(SceneLight<PointLight> t, Entity entity) {
            t.getLight().setRadius(entity.get(LightPower.class).getPower());
            t.getLight().setColor(entity.get(EntityLight.class).getColor());
        }
        @Override
        protected void persistentObjectUpdate(SceneLight<PointLight> t, Entity entity) {
            t.getLight().setPosition(EntityUtils.getWorldTransform(ed, entity.getId(), transform).getTranslation());
        }
        @Override
        protected void removeObject(SceneLight<PointLight> t, Entity entity) {
            t.assign(null);
        }
        
    }
    private class SpotContainer extends BoostEntityContainer<SceneLight<SpotLight>> {
        
        private final Transform transform = new Transform();

        public SpotContainer(EntityData ed) {
            super(ed, EntityLight.filter(EntityLight.SPOT), EntityLight.class, LightPower.class, InfluenceCone.class);
        }

        @Override
        protected SceneLight<SpotLight> addObject(Entity entity) {
            SceneLight<SpotLight> light = new SceneLight<>(new SpotLight());
            lazyObjectUpdate(light, entity);
            persistentObjectUpdate(light, entity);
            light.assign(rootNode);
            return light;
        }
        @Override
        protected void lazyObjectUpdate(SceneLight<SpotLight> t, Entity entity) {
            t.getLight().setSpotRange(entity.get(LightPower.class).getPower());
            entity.get(InfluenceCone.class).applyToSpotLight(t.getLight());
            t.getLight().setColor(entity.get(EntityLight.class).getColor());
        }
        @Override
        protected void persistentObjectUpdate(SceneLight<SpotLight> t, Entity entity) {
            EntityUtils.getWorldTransform(ed, entity.getId(), transform);
            t.getLight().setPosition(transform.getTranslation());
            t.getLight().setDirection(transform.getRotation().mult(Vector3f.UNIT_Z));
        }
        @Override
        protected void removeObject(SceneLight<SpotLight> t, Entity entity) {
            t.assign(null);
        }
        
    }
    private class AmbientContainer extends BoostEntityContainer<SceneLight<AmbientLight>> {

        public AmbientContainer(EntityData ed) {
            super(ed, EntityLight.filter(EntityLight.AMBIENT), EntityLight.class);
        }

        @Override
        protected SceneLight<AmbientLight> addObject(Entity entity) {
            //AmbientLight light = new AmbientLight();
            SceneLight<AmbientLight> light = new SceneLight<>(new AmbientLight());
            lazyObjectUpdate(light, entity);
            light.assign(rootNode);
            return light;
        }
        @Override
        protected void lazyObjectUpdate(SceneLight<AmbientLight> t, Entity entity) {
            t.getLight().setColor(entity.get(EntityLight.class).getColor());
        }
        @Override
        protected void persistentObjectUpdate(SceneLight<AmbientLight> t, Entity entity) {}
        @Override
        protected void removeObject(SceneLight<AmbientLight> t, Entity entity) {
            t.assign(null);
        }
        
    }
    private class PrebuiltProbeContainer extends BoostEntityContainer<SceneLight<LightProbe>> {
        
        private final Transform transform = new Transform();
        
        public PrebuiltProbeContainer(EntityData ed) {
            super(ed, EntityLight.filter(EntityLight.PREBUILT_PROBE), EntityLight.class, ProbeInfo.class, LightPower.class);
        }
        
        @Override
        protected SceneLight<LightProbe> addObject(Entity e) {
            SceneLight<LightProbe> probe = new SceneLight<>(probeFactory.create(e.get(ProbeInfo.class).getName(ed), true));
            lazyObjectUpdate(probe, e);
            persistentObjectUpdate(probe, e);
            probe.assign(rootNode);
            return probe;
        }
        @Override
        protected void lazyObjectUpdate(SceneLight<LightProbe> t, Entity e) {
            t.getLight().getArea().setRadius(e.get(LightPower.class).getPower());
        }
        @Override
        protected void persistentObjectUpdate(SceneLight<LightProbe> t, Entity e) {
            EntityUtils.getWorldTransform(ed, e.getId(), transform);
            t.getLight().setPosition(transform.getTranslation());
        }
        @Override
        protected void removeObject(SceneLight<LightProbe> t, Entity e) {
            t.assign(null);
        }
        
    }
    
}