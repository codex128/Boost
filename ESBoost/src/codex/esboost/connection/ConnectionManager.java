/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.connection;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * Manages connections between objects.
 * 
 * @author codex
 */
public class ConnectionManager {
    
    private final ConcurrentHashMap<Provider, ConnectionBundle> bundles = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<PendingAction> actions = new ConcurrentLinkedQueue<>();
    
    public void applyChanges(Provider provider) {
        ConnectionBundle b = bundles.get(provider);
        if (b != null) {
            b.applyChanges();
        }
        for (Iterator<PendingAction> it = actions.iterator(); it.hasNext();) {
            if (it.next().apply(provider)) {
                it.remove();
            }
        }
    }
    public void withdraw(Provider provider) {
        ConnectionBundle b = bundles.remove(provider);
        if (b != null) {
            b.clear();
        }
        for (Iterator<PendingAction> it = actions.iterator(); it.hasNext();) {
            if (it.next().isProvider(provider)) {
                it.remove();
            }
        }
    }
    
    public <T, R, K> void makePendingConnection(Connector<T, R> connector, Provider<R, K> provider, T object, K key) {
        fetchBundle(provider).makePendingConnection(connector, object, key);
    }
    
    public void makeDeletionRequest(Provider provider, Delete delete) {
        fetchBundle(provider).makeDeletionRequest(delete);
    }
    public void makeDeletionRequest(Delete delete) {
        for (ConnectionBundle b : bundles.values()) {
            b.makeDeletionRequest(delete);
        }
    }
    public void makeDeletionContainingRequest(Object object) {
        Delete delete = Delete.containing(object);
        for (ConnectionBundle b : bundles.values()) {
            b.makeDeletionRequest(delete);
        }
    }
    public void makeDeletionContainingRequest(Provider provider, Object object) {
        fetchBundle(provider).makeDeletionRequest(Delete.containing(object));
    }
    
    public <R, K> void makePendingAction(Provider<R, K> provider, K key, Consumer<R> action) {
        actions.add(new PendingAction(provider, key, action));
    }
    
    private ConnectionBundle fetchBundle(Provider provider) {
        ConnectionBundle b = bundles.get(provider);
        if (b == null) {
            b = new ConnectionBundle(provider);
            bundles.put(provider, b);
        }
        return b;
    }
    
}
