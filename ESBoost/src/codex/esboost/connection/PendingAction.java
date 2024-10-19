/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.connection;

import java.util.function.Consumer;

/**
 *
 * @author codex
 * @param <R>
 * @param <K>
 */
public class PendingAction <R, K> {
    
    private final Provider<R, K> provider;
    private final K key;
    private final Consumer<R> action;

    public PendingAction(Provider<R, K> provider, K key, Consumer<R> action) {
        this.provider = provider;
        this.key = key;
        this.action = action;
    }
    
    public boolean apply(Provider<R, K> provider) {
        if (isProvider(provider)) {
            R object = this.provider.fetchConnectingObject(key);
            if (object != null) action.accept(object);
            return true;
        }
        return false;
    }
    
    public boolean isProvider(Provider<R, K> provider) {
        return this.provider == provider;
    }

    public Provider<R, K> getProvider() {
        return provider;
    }

    public K getKey() {
        return key;
    }

    public Consumer<R> getAction() {
        return action;
    }
    
}
