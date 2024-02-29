/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.esboost.factories;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 * @param <T>
 */
public interface Factory <T> {
    
    public T create(String name, EntityId customer);
    
    public default <R extends T> R create(String name, EntityId customer, Class<R> type, boolean failOnMiss) {
        T v = create(name, customer);
        if (v != null && type.isAssignableFrom(v.getClass())) {
            return (R)v;
        } else if (failOnMiss) {
            if (v == null) {
                throw isNull(name);
            } else {
                throw new RuntimeException("Cannot cast "+v.getClass().getSimpleName()+" to "+type.getSimpleName()+".");
            }
        } else {
            return null;
        }
    }
    public default <R extends T> R create(String name, EntityId customer, Class<R> type) {
        return create(name, customer, type, false);
    }
    public default <R extends T> R create(String name, Class<R> type, boolean failOnMiss) {
        return create(name, null, type, failOnMiss);
    }
    public default <R extends T> R create(String name, Class<R> type) {
        return create(name, null, type, false);
    }
    public default T create(String name, EntityId customer, boolean failOnMiss) {
        T v = create(name, customer);
        if (v != null) {
            return v;
        } else if (failOnMiss) {
            throw isNull(name);
        } else {
            return null;
        }
    }
    public default T create(String name, boolean failOnMiss) {
        return create(name, (EntityId)null, failOnMiss);
    }
    public default T create(String name) {
        return create(name, (EntityId)null);
    }
    
    public static final String DIRECT_ASSET_PREFIX = "asset:";
    public static boolean isDirectAsset(String name) {
        return name.startsWith(DIRECT_ASSET_PREFIX);
    }
    public static String getAssetPath(String name) {
        return name.substring(DIRECT_ASSET_PREFIX.length());
    }
    
    public static NullPointerException isNull(String name) {
        return new NullPointerException("Manufactured object from \""+name+"\" was null.");
    }
    
}
