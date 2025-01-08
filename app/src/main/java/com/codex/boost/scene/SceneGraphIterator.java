/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.scene;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Iterates through the scene graph by depth-first.
 *
 * @author gary
 */
public class SceneGraphIterator implements Iterable<Spatial>, Iterator<Spatial> {
    
    private Spatial current;
    private Spatial main;
    private int depth = 0;
    private final LinkedList<PathNode> path = new LinkedList<>();

    public SceneGraphIterator(Spatial main) {
        if (main instanceof Node) {
            path.add(new PathNode((Node)main));
        }
        this.main = main;
    }

    @Override
    public Iterator<Spatial> iterator() {
        return this;
    }
    @Override
    public boolean hasNext() {
        if (main != null) return true;
        trim();
        return !path.isEmpty();
    }
    @Override
    public Spatial next() {
        if (main != null) {
            current = main;
            main = null;
        }
        else {
            current = path.getLast().iterator.next();
            if (current instanceof Node) {
                path.addLast(new PathNode((Node)current));
            }
        }
        return current;
    }
    
    /**
     * Get the current spatial.
     *
     * @return
     */
    public Spatial current() {
        return current;
    }
    
    /**
     * Makes this iterator ignore all children of the current spatial.
     * The children of the current spatial will not be iterated through.
     */
    public void ignoreChildren() {
        if (current instanceof Node) {
            path.removeLast();
        }
    }
    
    /**
     * Get the current depth of the iterator.
     * @return 
     */
    public int getDepth() {
        return main != null ? 0 : path.size()-1;
    }
    
    /**
     * Trims the path to the first available node.
     */
    private void trim() {
        if (!path.isEmpty() && !path.getLast().iterator.hasNext()) {
            path.removeLast();
            trim();
        }
    }
    
    private static class PathNode {

        Node node;
        Iterator<Spatial> iterator;

        PathNode(Node node) {
            this.node = node;
            iterator = this.node.getChildren().iterator();
        }
        
    }

}
