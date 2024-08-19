/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.scene;

import com.jme3.scene.Spatial;
import java.util.Iterator;

/**
 *
 * @author codex
 */
public class SceneAncestryIterator implements Iterable<Spatial>, Iterator<Spatial> {
    
    private Spatial current;
    private boolean freeze = true;
    
    public SceneAncestryIterator(Spatial spatial) {
        this.current = spatial;
    }
    
    @Override
    public Iterator<Spatial> iterator() {
        return this;
    }
    @Override
    public boolean hasNext() {
        return freeze || current.getParent() != null;
    }
    @Override
    public Spatial next() {
        if (!freeze) {
            current = current.getParent();
        } else {
            freeze = false;
        }
        return current;
    }
    
}
