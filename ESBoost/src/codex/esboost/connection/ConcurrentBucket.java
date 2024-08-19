/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.connection;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author codex
 * @param <T>
 */
public class ConcurrentBucket <T> implements Iterable<T> {
    
    private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();
    private final LinkedList<T> bucket = new LinkedList<>();
    
    public void loadBucketFromQueue() {
        bucket.clear();
        T e;
        while ((e = queue.poll()) != null) {
            bucket.add(e);
        }
    }
    
    public void addQueue(T e) {
        queue.add(e);
    }
    public void clearQueue() {
        queue.clear();
    }
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }
    
    public void addBucket(T e) {
        bucket.add(e);
    }
    public void clearBucket() {
        bucket.clear();
    }
    public boolean isBucketEmpty() {
        return bucket.isEmpty();
    }
    
    @Override
    public Iterator<T> iterator() {
        return bucket.iterator();
    }
    
}
