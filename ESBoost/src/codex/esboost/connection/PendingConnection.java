/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.connection;

/**
 *
 * @author codex
 * @param <T> object1 type
 * @param <R> object2 type
 * @param <K> key type
 */
public class PendingConnection <T, R, K> {
    
    private final Connector<T, R> connector;
    private final T object;
    private final K key;

    public PendingConnection(Connector<T, R> connector, T object, K key) {
        this.connector = connector;
        this.object = object;
        this.key = key;
    }
    
    public Connection<T, R> create(Provider<R, K> provider) {
        return new Connection(this, provider);
    }
    
    public Connector<T, R> getConnector() {
        return connector;
    }
    public T getObject() {
        return object;
    }
    public K getKey() {
        return key;
    }
    
}
