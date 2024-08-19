/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.factories;

import com.simsilica.es.EntityId;
import java.util.HashMap;
import java.util.function.Function;

/**
 *
 * @author codex
 */
public class FactoryPlugins {
    
    private static final HashMap<Class, FactoryPlugins> PLUGINS = new HashMap<>();
    private final HashMap<String, Function<EntityId, Object>> plugins = new HashMap<>();
    
    public Object create(String name, EntityId customer) {
        Function<EntityId, Object> plugin = plugins.get(name);
        if (plugin == null) {
            return null;
        }
        return plugin.apply(customer);
    }
    
    public void register(String name, Function<EntityId, Object> plugin) {
        plugins.put(name, plugin);
    }
    
    public static <R> R create(Class<? extends Factory> factoryType, Class<R> outputType, String name, EntityId customer) {
        FactoryPlugins plugins = PLUGINS.get(factoryType);
        if (plugins != null) {
            Object output = plugins.create(name, customer);
            if (output != null && outputType.isAssignableFrom(output.getClass())) {
                return (R)output;
            }
        }
        return null;
    }
    
    public static <R extends Factory> void register(Class<R> type, String name, Function<EntityId, Object> plugin) {
        FactoryPlugins plugins = PLUGINS.get(type);
        if (plugins == null) {
            plugins = new FactoryPlugins();
            PLUGINS.put(type, plugins);
        }
        plugins.register(name, plugin);
    }
    
}
