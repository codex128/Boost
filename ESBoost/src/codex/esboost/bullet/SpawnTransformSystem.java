/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.bullet;

import codex.esboost.EntityUtils;
import codex.esboost.components.SpawnAtWorldTransform;
import com.jme3.math.Transform;
import com.simsilica.bullet.SpawnPosition;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.sim.AbstractGameSystem;
import com.simsilica.sim.SimTime;

/**
 *
 * @author codex
 */
public class SpawnTransformSystem extends AbstractGameSystem {

    private EntityData ed;
    private EntitySet entities;
    private final Transform tempTransform = new Transform();
    
    @Override
    protected void initialize() {
        ed = getManager().get(EntityData.class, true);
        entities = ed.getEntities(SpawnAtWorldTransform.class);
    }
    @Override
    protected void terminate() {
        entities.release();
        entities = null;
    }
    @Override
    public void update(SimTime time) {
        if (entities.applyChanges()) {
            for (Entity e : entities.getAddedEntities()) {
                EntityUtils.getWorldTransform(ed, e.getId(), tempTransform);
                ed.setComponent(e.getId(), new SpawnPosition(tempTransform.getTranslation(null), tempTransform.getRotation(null)));
            }
        }
    }
    
}
