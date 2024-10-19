/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.esboost.EntityUtils;
import codex.esboost.components.Parent;
import codex.esboost.components.Position;
import codex.esboost.components.Rotation;
import codex.esboost.components.Scale;
import codex.esboost.components.WorldToLocalTag;
import codex.esboost.components.WorldTransform;
import com.jme3.math.Transform;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.sim.AbstractGameSystem;
import com.simsilica.sim.SimTime;

/**
 * Converts local transform to world transform.
 * 
 * @author codex
 */
public class WorldTransformSystem extends AbstractGameSystem {

    private EntityData ed;
    private EntitySet localToWorld, worldToLocal;
    private final Transform tempTransform = new Transform();
    
    @Override
    protected void initialize() {
        ed = getManager().get(EntityData.class, true);
        localToWorld = ed.getEntities(Position.class);
        worldToLocal = ed.getEntities(WorldTransform.class, WorldToLocalTag.class);
    }
    @Override
    protected void terminate() {
        localToWorld.release();
        worldToLocal.release();
    }
    @Override
    public void update(SimTime time) {
        if (worldToLocal.applyChanges()) {
            final Transform world = new Transform();
            final Transform parent = new Transform();
            for (Entity e : worldToLocal.getAddedEntities()) {
                Parent p = ed.getComponent(e.getId(), Parent.class);
                if (p != null) {
                    e.get(WorldTransform.class).toTransform(world);
                    EntityUtils.getWorldTransform(ed, p.getId(), parent);
                    EntityUtils.worldToLocal(world, parent, tempTransform);
                } else {
                    e.get(WorldTransform.class).toTransform(tempTransform);
                }
                e.set(new Position(tempTransform.getTranslation()));
                e.set(new Rotation(tempTransform.getRotation()));
                e.set(new Scale(tempTransform.getScale()));
                ed.removeComponent(e.getId(), WorldToLocalTag.class);
            }
        }
        localToWorld.applyChanges();
        for (Entity e : localToWorld) {
            EntityUtils.fetchWorldTransform(ed, e.getId(), tempTransform);
            ed.setComponent(e.getId(), new WorldTransform(tempTransform));
        }
    }
    
}
