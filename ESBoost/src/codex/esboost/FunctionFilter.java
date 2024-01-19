/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost;

import com.simsilica.es.ComponentFilter;
import com.simsilica.es.EntityComponent;
import java.util.function.Function;

/**
 *
 * @author codex
 * @param <T>
 */
public class FunctionFilter <T extends EntityComponent> implements ComponentFilter<T> {

    private final Class<T> type;
    private final Function<T, Boolean> filter;

    public FunctionFilter(Class<T> type, Function<T, Boolean> filter) {
        this.type = type;
        this.filter = filter;
    }
    
    @Override
    public Class<T> getComponentType() {
        return type;
    }
    @Override
    public boolean evaluate(EntityComponent c) {
        if (c != null && type.isAssignableFrom(c.getClass())) {
            return filter.apply((T)c);
        } else {
            return false;
        }
    }
    
}
