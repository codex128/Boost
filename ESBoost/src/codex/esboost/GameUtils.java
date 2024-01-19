/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost;

import codex.esboost.bullet.GeometricShape;
import codex.esboost.components.*;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.common.Decay;
import com.simsilica.sim.SimTime;
import java.util.function.Consumer;

/**
 * Useful utility methods for ECS.
 * 
 * @author codex
 */
public class GameUtils {    
    
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
    
    /**
     * Calculates the world transform of the entity.
     * 
     * @param ed entity data
     * @param id id of the entity
     * @return world transform
     */
    public static Transform getWorldTransform(EntityData ed, EntityId id) {
        return getWorldTransform(ed, id, null);
    }
    
    /**
     * Calculates the world transform of an entity and stores it in
     * the transform instance.
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
        fetchWorldTransform(ed, id, store, new Vector3f());
        return store;
    }
    
    /**
     * Calculates the world transform of an entity's parent.
     * 
     * @param ed entity data
     * @param id id of the entity
     * @return world transform of the entity's parent
     */
    public static Transform getParentWorldTransform(EntityData ed, EntityId id) {
        return getParentWorldTransform(ed, id, null);
    }
    
    /**
     * Calculates the world transform of an entity's parent and stores it
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
            return new Transform().set(Transform.IDENTITY);
        }
    }
    
    /**
     * Fetches the component from the entity or parent entities.
     * 
     * @param <T>
     * @param ed
     * @param id
     * @param type
     * @return 
     */
    public static <T extends EntityComponent> T getComponent(EntityData ed, EntityId id, Class<T> type) {
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
     * Creates a collision shape based on the spatial shape.
     * 
     * @param shape shape generation method
     * @param spatial spatial to calculate from
     * @return physical collision shape
     */
    public static CollisionShape createGeometricCollisionShape(GeometricShape shape, Spatial spatial) {
        switch (shape) {
            case Box: return CollisionShapeFactory.createBoxShape(spatial);
            case DynamicMesh: return CollisionShapeFactory.createDynamicMeshShape(spatial);
            case GImpact: return CollisionShapeFactory.createGImpactShape(spatial);
            case MergedBox: return CollisionShapeFactory.createMergedBoxShape(spatial);
            case MergedHull: return CollisionShapeFactory.createMergedHullShape(spatial);
            case MergedMesh: return CollisionShapeFactory.createMergedMeshShape(spatial);
            case Mesh: return CollisionShapeFactory.createMeshShape(spatial);
            //case VHACD: return CollisionShapeFactory.createVhacdShape(spatial, parameters, new CompoundCollisionShape());
            //case VHACD4: return CollisionShapeFactory.createVhacdShape(spatial, parameters, new CompoundCollisionShape());
            case Vhacd: throw new UnsupportedOperationException("VHACD collision shapes are not supported.");
            case Vhacd4: throw new UnsupportedOperationException("VHACD collision shapes are not supported.");
            default: return null;
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
        spatial.setUserData(EntityId.class.getName(), id.getId());
    }
    
    /**
     * Fetches an appended {@link EntityId} from the spatial, if it exists.
     * 
     * @param spatial spatial to fetch from
     * @param depth amount the algorithm will travel up the entity hierarchy to find the id, or -1 for no constraint
     * @return appended entity id, or null if none is found
     */
    public static EntityId fetchId(Spatial spatial, int depth) {
        while (spatial != null) {
            Long id = spatial.getUserData(EntityId.class.getName());
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
        GameUtils.getWorldTransform(ed, id, store);
        return new Ray(store.getTranslation(), store.getRotation().mult(Vector3f.UNIT_Z));
    }
    
}
