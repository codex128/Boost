/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.connection;

/**
 * Represents a relation between two objects.
 * 
 * @author codex
 * @param <T> object1 type
 * @param <R> object2 type
 */
public class Connection <T, R> {
    
    private final Connector<T, R> connector;
    private final T objectA;
    private final R objectB;
    private boolean connected = false;
    
    public <K> Connection(PendingConnection<T, R, K> pending, Provider<R, K> provider) {
        this(pending.getConnector(), pending.getObject(), provider.fetchConnectingObject(pending.getKey()));
    }
    public Connection(Connector<T, R> connector, T objectA, R objectB) {
        this.connector = connector;
        this.objectA = objectA;
        this.objectB = objectB;
    }
    
    public boolean connect() {
        if (!connected) {
            connector.connect(objectA, objectB);
            connected = true;
            return true;
        }
        return false;
    }
    public boolean disconnect() {
        if (connected) {
            connector.disconnect(objectA, objectB);
            connected = false;
            return true;
        }
        return false;
    }

    public Connector<T, R> getConnector() {
        return connector;
    }
    public T getObjectA() {
        return objectA;
    }
    public R getObjectB() {
        return objectB;
    }
    public boolean isConnected() {
        return connected;
    }
    
    public boolean contains(Object object) {
        return connector.compareAObjects(objectA, object) || connector.compareBObjects(objectB, object);
    }
    public boolean equals(Object object1, Object object2) {
        return (this.objectA == object1 && this.objectB == object2)
                || (this.objectA == object2 && this.objectB == object1);
    }
    
    @Override
    public String toString() {
        return "Connection[1:"+objectA.getClass().getSimpleName()+" -> 2:"+objectB.getClass().getSimpleName()+"]";
    }
    
}
