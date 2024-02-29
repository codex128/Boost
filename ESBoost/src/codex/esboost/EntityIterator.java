/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost;

import codex.esboost.components.Parent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import java.util.Iterator;

/**
 *
 * @author codex
 */
public class EntityIterator implements Iterable<EntityId>, Iterator<EntityId> {
    
    private final EntityData ed;
    private EntityId current;
    
    public EntityIterator(EntityData ed, EntityId start) {
        this.ed = ed;
        this.current = start;
    }
    
    @Override
    public Iterator<EntityId> iterator() {
        return this;
    }
    @Override
    public boolean hasNext() {
        return current != null;
    }
    @Override
    public EntityId next() {
        EntityId output = current;
        Parent parent = ed.getComponent(current, Parent.class);
        if (parent != null) {
            current = parent.getId();
        } else {
            current = null;
        }
        return output;
    }
    
}
