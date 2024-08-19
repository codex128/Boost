/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import codex.esboost.FunctionFilter;
import codex.esboost.factories.Prefab;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class FilterInfo implements EntityComponent {
    
    private final Prefab prefab;
    private final EntityId viewPort;

    public FilterInfo(Prefab prefab, EntityId viewPort) {
        this.prefab = prefab;
        this.viewPort = viewPort;
    }

    public Prefab getPrefab() {
        return prefab;
    }
    public EntityId getViewPort() {
        return viewPort;
    }
    
    public static FunctionFilter<FilterInfo> filter(EntityData ed, String type) {
        return new FunctionFilter<>(FilterInfo.class, c -> c.prefab.getName(ed).equals(type));
    }
    
}
