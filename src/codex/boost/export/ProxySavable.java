/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.export;

import com.jme3.export.Savable;

/**
 *
 * @author codex
 * @param <T>
 */
public interface ProxySavable <T> extends Savable {
    
    public void setObject(T object);
    
    public T getObject();
    
    public default <R extends T> R getObject(Class<R> type) {
        return (R)getObject();
    }
    
}
