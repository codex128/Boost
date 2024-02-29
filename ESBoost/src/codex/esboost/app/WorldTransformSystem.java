/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.app;

import codex.esboost.EntityUtils;
import codex.esboost.components.Position;
import codex.esboost.components.WorldTransform;
import com.jme3.math.Transform;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntitySet;
import com.simsilica.sim.AbstractGameSystem;
import com.simsilica.sim.SimTime;

/**
 *
 * @author codex
 */
public class WorldTransformSystem extends AbstractGameSystem {

    private EntityData ed;
    private EntitySet entities;
    private final Transform tempTransform = new Transform();
    
    @Override
    protected void initialize() {
        ed = getManager().get(EntityData.class, true);
        entities = ed.getEntities(Position.class);
    }
    @Override
    protected void terminate() {
        entities.release();
    }
    @Override
    public void update(SimTime time) {
        entities.applyChanges();
        for (Entity e : entities) {
            EntityUtils.fetchWorldTransform(ed, e.getId(), tempTransform);
            ed.setComponent(e.getId(), new WorldTransform(tempTransform));
        }
    }
    
}
