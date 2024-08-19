/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.connection;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public class ConnectionBundle {
    
    private final Provider provider;
    private final LinkedList<Connection> connections = new LinkedList<>();
    private final ConcurrentBucket<PendingConnection> pending = new ConcurrentBucket<>();
    private final ConcurrentBucket<Delete> delete = new ConcurrentBucket<>();

    public ConnectionBundle(Provider provider) {
        this.provider = provider;
    }
    
    public void applyChanges() {
        // create local list versions
        delete.loadBucketFromQueue();
        pending.loadBucketFromQueue();
        // apply pending connections
        for (PendingConnection p : pending) {
            Object b = provider.fetchConnectingObject(p.getKey());
            if (b != null) {
                Connection c = new Connection(p.getConnector(), p.getObject(), b);
                if (!c.getConnector().getMultiConnectionHint().isASupport()) {
                    delete.addBucket(deleteObjectNotConnection(c.getObjectA(), c));
                }
                if (!c.getConnector().getMultiConnectionHint().isBSupport()) {
                    delete.addBucket(deleteObjectNotConnection(c.getObjectB(), c));
                }
                connections.add(c);
            }
        }
        // apply break requests
        if (!connections.isEmpty()) {
            for (Delete d : delete) {
                for (Iterator<Connection> it = connections.iterator(); it.hasNext();) {
                    Connection c = it.next();
                    if (d.delete(c)) {
                        c.disconnect();
                        it.remove();
                    }
                }
            }
            // make connections
            if (!pending.isBucketEmpty()) for (Connection c : connections) {
                c.connect();
            }
        }
        // clear local lists
        delete.clearBucket();
        pending.clearBucket();
    }
    public void clear() {
        for (Connection c : connections) {
            c.disconnect();
        }
        connections.clear();
    }
    
    public <T, R, K> void makePendingConnection(Connector<T, R> connector, T object, K key) {
        pending.addQueue(new PendingConnection(connector, object, key));
    }
    public void makeDeletionRequest(Delete request) {
        delete.addQueue(request);
    }
    
    private static Delete deleteObjectNotConnection(Object object, Connection connection) {
        return Delete.and(Delete.containing(object), Delete.not(connection));
    }
    
}
