/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.boost.GameAppState;
import codex.boost.scene.SceneGraphIterator;
import codex.esboost.BoostEntityContainer;
import codex.esboost.anim.AnimationDriver;
import codex.esboost.components.AnimAction;
import codex.esboost.components.AnimatedModel;
import codex.esboost.components.ModelInfo;
import codex.esboost.components.TargetTo;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.app.Application;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.sim.SimTime;
import com.simsilica.state.GameSystemsState;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class AnimationState extends GameAppState {
    
    private EntityData ed;
    private ModelViewState modelState;
    private AnimContainer anims;
    private EntitySet actions;
    private SimTime time;
    private final HashMap<EntityId, AnimationDriver> driverCache = new HashMap<>();
    
    @Override
    protected void init(Application app) {
        GameSystemsState systems = getState(GameSystemsState.class, true);
        time = systems.getStepTime();
        ed = systems.get(EntityData.class);
        modelState = getState(ModelViewState.class, true);
        anims = new AnimContainer();
        actions = ed.getEntities(AnimAction.class, TargetTo.class);
        anims.start();
    }
    @Override
    protected void cleanup(Application app) {
        anims.stop();
        actions.release();
    }
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        anims.update();
        if (actions.applyChanges()) {
            actions.getAddedEntities().forEach(e -> runAction(e));
            actions.getChangedEntities().forEach(e -> runAction(e));
        }
    }
    
    public void setDriver(EntityId id, AnimationDriver driver) {
        AnimView view = anims.getObject(id);
        if (view != null) {
            System.out.println("set driver directly to animation");
            view.setDriver(driver);
        } else {
            System.out.println("cache driver for later");
            driverCache.put(id, driver);
        }
    }
    
    public AnimationDriver getDriver(EntityId id) {
        AnimView view = anims.getObject(id);
        if (view != null) {
            return view.driver;
        } else {
            return null;
        }
    }
    public AnimComposer getAnimComposer(EntityId id) {
        AnimView view = anims.getObject(id);
        if (view != null) {
            return view.anim;
        } else {
            return null;
        }
    }
    public SkinningControl getSkinning(EntityId id) {
        AnimView view = anims.getObject(id);
        if (view != null) {
            return view.skin;
        } else {
            return null;
        }
    }
    
    private void runAction(Entity e) {
        AnimView view = anims.getObject(e.get(TargetTo.class).getTargetId());
        if (view != null) {
            view.runAction(e.get(AnimAction.class));
        }
    }
    
    private class AnimView {
        
        private final Entity entity;
        private AnimComposer anim;
        private SkinningControl skin;
        private AnimationDriver driver;
        
        public AnimView(Entity entity) {
            this.entity = entity;
            Spatial spatial = modelState.getSpatial(this.entity.getId());
            if (spatial == null) {
                throw new NullPointerException("Failed to locate model.");
            }
            if (!this.entity.get(AnimatedModel.class).isSearchChildren()) {
                anim = spatial.getControl(AnimComposer.class);
            } else for (Spatial s : new SceneGraphIterator(spatial)) {
                anim = s.getControl(AnimComposer.class);
                if (anim != null) break;
            }
            if (anim == null) {
                throw new NullPointerException("Failed to locate animation control.");
            }
            skin = anim.getSpatial().getControl(SkinningControl.class);
            AnimationDriver d = driverCache.remove(this.entity.getId());
            if (d != null) {
                setDriver(d);
            }
        }
        
        public final void runAction(AnimAction action) {
            anim.setCurrentAction(action.getAction(), action.getLayer(), action.isLoop());
        }
        
        public final void setDriver(AnimationDriver driver) {
            if (this.driver == driver) return;
            this.driver = driver;
            this.driver.setupAnimations(anim, skin);
        }
        
        public final void update() {
            if (driver != null) {
                driver.updateAnimations(time, anim, skin);
            }
        }
        
    }
    private class AnimContainer extends BoostEntityContainer<AnimView> {

        public AnimContainer() {
            super(ed, ModelInfo.class, AnimatedModel.class);
        }
        
        @Override
        protected AnimView addObject(Entity entity) {
            return new AnimView(entity);
        }
        @Override
        protected void lazyObjectUpdate(AnimView object, Entity e) {}
        @Override
        protected void persistentObjectUpdate(AnimView object, Entity e) {}
        @Override
        protected void removeObject(AnimView t, Entity entity) {}
        
    }
    
}
