/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.esboost.BoostEntityContainer;
import codex.esboost.connection.ConnectionManager;
import codex.esboost.EntityUtils;
import codex.esboost.components.LightInfo;
import codex.esboost.components.InfluenceCone;
import codex.esboost.components.LightPower;
import codex.esboost.components.ProbeInfo;
import codex.esboost.components.SceneMember;
import codex.esboost.connection.Connector;
import codex.esboost.factories.Factory;
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
import com.simsilica.es.EntitySet;
import java.util.ArrayList;

/**
 *
 * @author codex
 */
public class LightingState extends GameAppState implements Connector<Light, Node> {
    
    private EntityData ed;
    private ConnectionManager connector;
    private SceneState sceneState;
    private EntitySet sceneMembers;
    private final Factory<LightProbe> probeFactory;
    private final ArrayList<BoostEntityContainer<? extends Light>> entities = new ArrayList<>();

    public LightingState(Factory<LightProbe> probeFactory) {
        this.probeFactory = probeFactory;
    }
    
    @Override
    protected void init(Application app) {
        ed = EntityUtils.getEntityData(app);
        connector = EntityUtils.getConnectionManager(app);
        sceneState = getState(SceneState.class, true);
        sceneMembers = ed.getEntities(LightInfo.filter(false, LightInfo.RUNTIME_PROBE),
                LightInfo.class, SceneMember.class);
        entities.add(new DirectionalContainer(ed));
        entities.add(new PointContainer(ed));
        entities.add(new SpotContainer(ed));
        entities.add(new AmbientContainer(ed));
        entities.add(new PrebuiltProbeContainer(ed));
        entities.forEach(e -> e.start());
    }
    @Override
    protected void cleanup(Application app) {
        sceneMembers.release();
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
        if (sceneMembers.applyChanges()) {
            sceneMembers.getAddedEntities().forEach(e -> assignLight(e, true));
            sceneMembers.getChangedEntities().forEach(e -> assignLight(e, true));
            sceneMembers.getRemovedEntities().forEach(e -> assignLight(e, false));
        }
    }
    @Override
    public void connect(Light light, Node scene) {
        scene.addLight(light);
    }
    @Override
    public void disconnect(Light light, Node scene) {
        scene.removeLight(light);
    }
    @Override
    public MultiConnectionHint getMultiConnectionHint() {
        return Connector.MultiConnectionHint.OnlyObjectB;
    }
    
    private void assignLight(Entity e, boolean attach) {
        Light light = getLight(e.getId());
        if (light != null) {
            if (attach) {
                connector.makePendingConnection(this, sceneState, light, e.get(SceneMember.class).getScene());
            } else {
                connector.makeDeletionContainingRequest(light);
            }
        }
    }
    public Light getLight(EntityId id) {
        for (BoostEntityContainer<? extends Light> c : entities) {
            Light l = c.getObject(id);
            if (l != null) {
                return l;
            }
        }
        return null;
    }
    
    private class DirectionalContainer extends BoostEntityContainer<DirectionalLight> {
        
        private final Transform transform = new Transform();
        
        public DirectionalContainer(EntityData ed) {
            super(ed, LightInfo.filter(LightInfo.DIRECTIONAL), LightInfo.class);
        }
        
        @Override
        protected DirectionalLight addObject(Entity entity) {
            //DirectionalLight light = new DirectionalLight();
            DirectionalLight light = new DirectionalLight();
            lazyObjectUpdate(light, entity);
            persistentObjectUpdate(light, entity);
            connector.makePendingConnection(LightingState.this, sceneState, light, sceneState.getRootNode());
            return light;
        }
        @Override
        protected void lazyObjectUpdate(DirectionalLight t, Entity entity) {
            t.setColor(entity.get(LightInfo.class).getColor());
        }
        @Override
        protected void persistentObjectUpdate(DirectionalLight t, Entity entity) {
            t.setDirection(EntityUtils.getWorldTransform(ed, entity.getId(), transform).getRotation().mult(Vector3f.UNIT_Z));            
        }
        @Override
        protected void removeObject(DirectionalLight t, Entity entity) {
            connector.makeDeletionContainingRequest(t);
        }
        
    }
    private class PointContainer extends BoostEntityContainer<PointLight> {
        
        private final Transform transform = new Transform();
        
        public PointContainer(EntityData ed) {
            super(ed, LightInfo.filter(LightInfo.POINT), LightInfo.class, LightPower.class);
        }
        
        @Override
        protected PointLight addObject(Entity entity) {
            PointLight light = new PointLight();
            lazyObjectUpdate(light, entity);
            persistentObjectUpdate(light, entity);
            connector.makePendingConnection(LightingState.this, sceneState, light, sceneState.getRootNode());
            return light;
        }
        @Override
        protected void lazyObjectUpdate(PointLight t, Entity entity) {
            t.setRadius(entity.get(LightPower.class).getPower());
            t.setColor(entity.get(LightInfo.class).getColor());
        }
        @Override
        protected void persistentObjectUpdate(PointLight t, Entity entity) {
            t.setPosition(EntityUtils.getWorldTransform(ed, entity.getId(), transform).getTranslation());
        }
        @Override
        protected void removeObject(PointLight t, Entity entity) {
            connector.makeDeletionContainingRequest(t);
        }
        
    }
    private class SpotContainer extends BoostEntityContainer<SpotLight> {
        
        private final Transform transform = new Transform();

        public SpotContainer(EntityData ed) {
            super(ed, LightInfo.filter(LightInfo.SPOT), LightInfo.class, LightPower.class, InfluenceCone.class);
        }

        @Override
        protected SpotLight addObject(Entity entity) {
            SpotLight light = new SpotLight();
            lazyObjectUpdate(light, entity);
            persistentObjectUpdate(light, entity);
            connector.makePendingConnection(LightingState.this, sceneState, light, sceneState.getRootNode());
            return light;
        }
        @Override
        protected void lazyObjectUpdate(SpotLight t, Entity entity) {
            t.setSpotRange(entity.get(LightPower.class).getPower());
            entity.get(InfluenceCone.class).applyToSpotLight(t);
            t.setColor(entity.get(LightInfo.class).getColor());
        }
        @Override
        protected void persistentObjectUpdate(SpotLight t, Entity entity) {
            EntityUtils.getWorldTransform(ed, entity.getId(), transform);
            t.setPosition(transform.getTranslation());
            t.setDirection(transform.getRotation().mult(Vector3f.UNIT_Z));
        }
        @Override
        protected void removeObject(SpotLight t, Entity entity) {
            connector.makeDeletionContainingRequest(t);
        }
        
    }
    private class AmbientContainer extends BoostEntityContainer<AmbientLight> {

        public AmbientContainer(EntityData ed) {
            super(ed, LightInfo.filter(LightInfo.AMBIENT), LightInfo.class);
        }

        @Override
        protected AmbientLight addObject(Entity entity) {
            AmbientLight light = new AmbientLight();
            lazyObjectUpdate(light, entity);
            connector.makePendingConnection(LightingState.this, sceneState, light, sceneState.getRootNode());
            return light;
        }
        @Override
        protected void lazyObjectUpdate(AmbientLight t, Entity entity) {
            t.setColor(entity.get(LightInfo.class).getColor());
        }
        @Override
        protected void persistentObjectUpdate(AmbientLight t, Entity entity) {}
        @Override
        protected void removeObject(AmbientLight t, Entity entity) {
            connector.makeDeletionContainingRequest(t);
        }
        
    }
    private class PrebuiltProbeContainer extends BoostEntityContainer<LightProbe> {
        
        private final Transform transform = new Transform();
        
        public PrebuiltProbeContainer(EntityData ed) {
            super(ed, LightInfo.filter(LightInfo.PREBUILT_PROBE), LightInfo.class, ProbeInfo.class, LightPower.class);
        }
        
        @Override
        protected LightProbe addObject(Entity e) {
            LightProbe probe = probeFactory.create(e.get(ProbeInfo.class).getName(ed), true);
            lazyObjectUpdate(probe, e);
            persistentObjectUpdate(probe, e);
            connector.makePendingConnection(LightingState.this, sceneState, probe, sceneState.getRootNode());
            return probe;
        }
        @Override
        protected void lazyObjectUpdate(LightProbe t, Entity e) {
            t.getArea().setRadius(e.get(LightPower.class).getPower());
        }
        @Override
        protected void persistentObjectUpdate(LightProbe t, Entity e) {
            EntityUtils.getWorldTransform(ed, e.getId(), transform);
            t.setPosition(transform.getTranslation());
        }
        @Override
        protected void removeObject(LightProbe t, Entity e) {
            connector.makeDeletionContainingRequest(t);
        }
        
    }
    
}