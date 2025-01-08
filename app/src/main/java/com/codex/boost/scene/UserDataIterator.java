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
public abstract class UserDataIterator <T> implements Iterable<T> {
    
    private Spatial spatial;
    private String name;
    private Class<T> type;

    public UserDataIterator(String paramName, Class<T> type) {
        this.name = paramName;
        this.type = type;
    }

    public abstract void accept(Spatial spatial, T userdata);
    
    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
    }
    public void setParamName(String paramName) {
        this.name = paramName;
    }
    public void setType(Class<T> type) {
        this.type = type;
    }
    
    @Override
    public DataIterator iterator() {
        return new DataIterator();
    }
    public class DataIterator implements Iterator<T> {

        private SceneGraphIterator delegate;
        private Spatial next;
        private T data;
        
        private DataIterator() {
            delegate = new SceneGraphIterator(spatial);
        }
        
        @Override
        public boolean hasNext() {
            while (true) {
                if (!delegate.hasNext()) return false;
                next = delegate.next();
                data = next.getUserData(name);
                if (data != null) return true;
            }
        }
        @Override
        public T next() {
            return data;
        }
        
        public Spatial getSpatial() {
            return next;
        }
        
    }
    
}
