/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost;

import codex.esboost.connection.ConnectionManager;
import codex.esboost.components.*;
import com.jme3.app.Application;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.es.CreatedBy;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.common.Decay;
import com.simsilica.sim.GameSystemManager;
import com.simsilica.sim.SimTime;
import com.simsilica.state.GameSystemsState;
import java.util.function.Consumer;

/**
 * Useful utility methods for ECS.
 * 
 * @author codex
 */
public class EntityUtils {    
    
    public static final String ENTITYID_USERDATA = EntityId.class.getName();
    
    private static void fetchWorldTransform(EntityData ed, EntityId id, Transform store, Vector3f tempVec) {
        Parent parent = ed.getComponent(id, Parent.class);
        if (parent != null) {
            fetchWorldTransform(ed, parent.getId(), store, tempVec);
        }
        Position position = ed.getComponent(id, Position.class);
        Rotation rotation = ed.getComponent(id, Rotation.class);
        Scale scale = ed.getComponent(id, Scale.class);
        if (position != null) {
            store.getTranslation().addLocal(store.getRotation().mult(
                    position.getPosition().mult(store.getScale(), tempVec), tempVec));
        }
        if (rotation != null) {
            store.getRotation().multLocal(rotation.getRotation());
        }
        if (scale != null) {
            store.getScale().multLocal(scale.getScale());
        }
    }
    private static Vector3f vectorDivide(Vector3f vector, Vector3f divisor, Vector3f store) {
        if (store == null) {
            store = new Vector3f();
        }
        store.x = vector.x/divisor.x;
        store.y = vector.y/divisor.y;
        store.z = vector.z/divisor.z;
        return store;
    }
    
    /**
     * Calculates the world transform of the entity.
     * <p>
     * This method does not check for a {@link WorldTransform} component.
     * 
     * @param ed entity data
     * @param id entity id
     * @param store stores result
     * @return 
     */
    public static Transform fetchWorldTransform(EntityData ed, EntityId id, Transform store) {
        if (store == null) {
            store = new Transform();
        } else {
            store.set(Transform.IDENTITY);
        }
        fetchWorldTransform(ed, id, store, new Vector3f());
        return store;
    }
    
    /**
     * Gets the world transform of the entity.
     * 
     * @param ed entity data
     * @param id id of the entity
     * @return world transform
     */
    public static Transform getWorldTransform(EntityData ed, EntityId id) {
        return getWorldTransform(ed, id, null);
    }
    
    /**
     * Gets the world transform of an entity and stores it in the transform instance.
     * 
     * @param ed entity data
     * @param id id of the entity
     * @param store transform instance to store the result in
     * @return world transform
     */
    public static Transform getWorldTransform(EntityData ed, EntityId id, Transform store) {
        if (store == null) {
            store = new Transform();
        } else {
            store.set(Transform.IDENTITY);
        }
        WorldTransform wt = ed.getComponent(id, WorldTransform.class);
        if (wt != null) {
            return wt.toTransform(store);
        }
        fetchWorldTransform(ed, id, store, new Vector3f());
        return store;
    }
    
    /**
     * Gets the world transform of an entity's parent.
     * 
     * @param ed entity data
     * @param id id of the entity
     * @return world transform of the entity's parent
     */
    public static Transform getParentWorldTransform(EntityData ed, EntityId id) {
        return getParentWorldTransform(ed, id, null);
    }
    
    /**
     * Gets the world transform of an entity's parent and stores it
     * in the transform instance.
     * 
     * @param ed entity data
     * @param id id of the entity
     * @param store transform to store the result in
     * @return world transform
     */
    public static Transform getParentWorldTransform(EntityData ed, EntityId id, Transform store) {
        Parent parent = ed.getComponent(id, Parent.class);
        if (parent != null) {
            return getWorldTransform(ed, parent.getId(), store);
        } else if (store != null) {
            return store.set(Transform.IDENTITY);
        } else {
            return new Transform();
        }
    }
    
    /**
     * Gets the component from the entity or parent entities.
     * 
     * @param <T> component class type
     * @param ed entity data
     * @param id starting entity
     * @param type component class
     * @return 
     */
    public static <T extends EntityComponent> T getComponentInHierarchy(EntityData ed, EntityId id, Class<T> type) {
        while (id != null) {
            T component = ed.getComponent(id, type);
            if (component != null) {
                return component;
            }
            Parent parent = ed.getComponent(id, Parent.class);
            if (parent == null) {
                return null;
            }
            id = parent.getId();
        }
        return null;
    }
    
    /**
     * Traverses the entity hierarchy through {@link Parent} components.
     * <p>
     * This traversal starts at the lowest entity, and works its
     * way up, so that it traverses every parent of the root entity.
     * 
     * @param ed entity data
     * @param root the entity to start at
     * @param foreach consumer to be called for each step
     */
    public static void traverseEntityHierarchy(EntityData ed, EntityId root, Consumer<EntityId> foreach) {
        while (root != null) {
            foreach.accept(root);
            Parent parent = ed.getComponent(root, Parent.class);
            if (parent == null) break;
            root = parent.getId();
        }
    }
    
    /**
     * Appends the entity's id to the userdata of the spatial.
     * <p>
     * The key of the userdata is "EntityId".
     * 
     * @param id id of the spatial, or null to clear the currently appended id.
     * @param spatial spatial to append to
     */
    public static void appendId(EntityId id, Spatial spatial) {
        spatial.setUserData(ENTITYID_USERDATA, (id != null ? id.getId() : null));
    }
    
    /**
     * Fetches an appended {@link EntityId} from the spatial, if it exists.
     * 
     * @param spatial spatial to fetch from
     * @param depth amount the algorithm will travel up the spatial hierarchy to find the id, or -1 for no constraint
     * @return appended entity id, or null if none is found
     */
    public static EntityId fetchId(Spatial spatial, int depth) {
        while (spatial != null) {
            Long id = spatial.getUserData(ENTITYID_USERDATA);
            if (id != null) return new EntityId(id);
            if (depth-- == 0) return null;
            spatial = spatial.getParent();
        }
        return null;
    }
    
    /**
     * Creates a {@link Decay} component starting at the current time
     * and ending at a future time so many seconds away.
     * 
     * @param time current SimTime
     * @param seconds seconds the decay lasts after the current time
     * @return decay component
     */
    public static Decay duration(SimTime time, double seconds) {
        return new Decay(time.getFrame(), time.getFutureTime(seconds));
    }
    
    /**
     * Runs the {@link Consumer} if the component exists in the entity.
     * 
     * @param <T> component class type
     * @param ed entity data
     * @param id id of the entity
     * @param component class of the desired component
     * @param notify consumer
     */
    public static <T extends EntityComponent> void onComponentExists(EntityData ed, EntityId id, Class<T> component, Consumer<T> notify) {
        T c = ed.getComponent(id, component);
        if (c != null) {
            notify.accept(c);
        }
    }
    
    /**
     * Creates a ray from the world transform on an entity.
     * 
     * @param ed
     * @param id
     * @param store stores world transform (or null, to instantiate a new transform)
     * @return ray conforming to the world transform of the entity
     */
    public static Ray rayFromTransform(EntityData ed, EntityId id, Transform store) {
        if (store == null) {
            store = new Transform();
        }
        EntityUtils.getWorldTransform(ed, id, store);
        return new Ray(store.getTranslation(), store.getRotation().mult(Vector3f.UNIT_Z));
    }
    
    /**
     * Returns the component belonging to the entity, or a default component
     * if none exists.
     * <p>
     * The default component class type determines the class type of the output component.
     * 
     * @param <T> component type
     * @param ed entity data
     * @param id entity id
     * @param defaultValue default component (used if no other is found)
     * @return 
     */
    public static <T extends EntityComponent> T getComponent(EntityData ed, EntityId id, T defaultValue) {
        assert defaultValue != null : "Default value cannot be null.";
        T c = ed.getComponent(id, (Class<T>)defaultValue.getClass());
        if (c != null) {
            return c;
        } else {
            return defaultValue;
        }
    }
    
    /**
     * Returns the component belonging to the entity, or a default component
     * if none exists.
     * 
     * @param <T> component type
     * @param ed entity data
     * @param id entity id
     * @param type class type of the returned component
     * @param defaultValue default component (used if no other is found)
     * @return 
     */
    public static <T extends EntityComponent> T getComponent(EntityData ed, EntityId id, Class<T> type, T defaultValue) {
        T c = ed.getComponent(id, type);
        if (c != null) {
            return c;
        } else {
            return defaultValue;
        }
    }
    
    /**
     * Returns the component belonging to the entity, or throws a {@link NullPointerException}
     * if none exists and {@code failOnMiss} is true.
     * 
     * @param <T> component type
     * @param ed entity data
     * @param id entity id
     * @param type class type of the returned component
     * @param failOnMiss if true, and if no component is found, an exception will be thrown.
     * @throws NullPointerException if no component is found and {@code failOnMiss} is true
     * @return 
     */
    public static <T extends EntityComponent> T getComponent(EntityData ed, EntityId id, Class<T> type, boolean failOnMiss) {
        T c = ed.getComponent(id, type);
        if (c != null) {
            return c;
        } else if (failOnMiss) {
            throw new NullPointerException("Failed to locate "+type.getSimpleName()+" component of "+id+".");
        } else {
            return null;
        }
    }
    
    /**
     * Fetches the entity data via {@link GameSystemsState}.
     * 
     * @param app
     * @return 
     */
    public static EntityData getEntityData(Application app) {
        return app.getStateManager().getState(GameSystemsState.class, true).get(EntityData.class, true);
    }
    
    /**
     * Fetches the entity data.
     * 
     * @param manager
     * @return 
     */
    public static EntityData getEntityData(GameSystemManager manager) {
        return manager.get(EntityData.class, true);
    }
    
    /**
     * Fetches the connection manager.
     * 
     * @param app
     * @return 
     */
    public static ConnectionManager getConnectionManager(Application app) {
        return app.getStateManager().getState(GameSystemsState.class, true).get(ConnectionManager.class, true);
    }
    
    /**
     * Fetches the connection manager.
     * 
     * @param manager
     * @return 
     */
    public static ConnectionManager getConnectionManager(GameSystemManager manager) {
        return manager.get(ConnectionManager.class, true);
    }
    
    /**
     * Returns a component filter that filters entities by creator.
     * 
     * @param creator creator entity
     * @param created 
     * @return 
     */
    public static FunctionFilter<CreatedBy> filterByCreator(EntityId creator, boolean created) {
        return new FunctionFilter<>(CreatedBy.class, c -> c.getCreatorId().equals(creator) == created);
    }
    
    /**
     * Converts world transform to local transform relative to the parent world transform.
     * 
     * @param world world transform to convert (not null, unaffected)
     * @param parent parent world transform to be relative to (not null, unaffected)
     * @param store stores result (null to create new {@link Transform} instance)
     * @return 
     */
    public static Transform worldToLocal(Transform world, Transform parent, Transform store) {
        if (store == null) {
            store = new Transform();
        }
        store.setScale(parent.getScale());
        Quaternion rotInv = parent.getRotation().inverse();
        world.getTranslation().subtract(parent.getTranslation(), store.getTranslation());
        store.getTranslation().divideLocal(store.getScale());
        rotInv.mult(store.getTranslation(), store.getTranslation());
        world.getRotation().mult(rotInv, store.getRotation());
        vectorDivide(world.getScale(), store.getScale(), store.getScale());
        return store;
    }
    
    /**
     * Sets the {@link Position}, {@link Rotation}, and (optionally) {@link Scale} components of the entity.
     * 
     * @param ed entity data
     * @param id entity id
     * @param transform transform to apply
     * @param applyScale true to apply scale, otherwise scale will not be applied to the entity
     */
    public static void setEntityTransform(EntityData ed, EntityId id, Transform transform, boolean applyScale) {
        ed.setComponents(id,
            new Position(transform.getTranslation()),
            new Rotation(transform.getRotation())
        );
        if (applyScale) {
            ed.setComponent(id, new Scale(transform.getScale()));
        }
    }
    
    /**
     * Gets the transform of a physics object in physics space.
     * 
     * @param object
     * @param store
     * @return 
     */
    public static Transform getPhysicsObjectTransform(PhysicsCollisionObject object, Transform store) {
        if (store == null) {
            store = new Transform();
        }
        object.getPhysicsLocation(store.getTranslation());
        object.getPhysicsRotation(store.getRotation());
        return store;
    }
    
    /**
     * Constructs a Ray from a Transform.
     * 
     * @param t
     * @param store
     * @return 
     */
    public static Ray transformToRay(Transform t, Ray store) {
        if (store == null) {
            store = new Ray();
        }
        store.origin.set(t.getTranslation());
        t.getRotation().mult(Vector3f.UNIT_Z, store.direction);
        return store;
    }
    
    /**
     * Converts a transform object into a direction.
     * 
     * @param t
     * @param store
     * @return 
     */
    public static Vector3f transformToDirection(Transform t, Vector3f store) {
        return t.getRotation().mult(Vector3f.UNIT_Z, store);
    }
    
    /**
     * Fetches the game time object.
     * 
     * @param app
     * @return 
     */
    public static SimTime getTime(Application app) {
        return app.getStateManager().getState(GameSystemsState.class, true).getStepTime();
    }
    
}
